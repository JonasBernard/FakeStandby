const { slice } = require("nunjucks/src/filters");

function getFileSystem() {
  return [require("fs"), require("fs-extra")];
}

function getHttps() {
  return require("https");
}

function getLanguageImportData(fs) {
  let rawdata = fs.readFileSync("languages.json");
  try {
    let languageImportData = JSON.parse(rawdata);
    if (
      !languageImportData.languages ||
      languageImportData.languages.length == 0
    ) {
      console.error("No language data found. The script will have no effect.");
      languageImportData.languages = [];
    }
    console.log("Found " + languageImportData.languages.length + " languages.");
    return languageImportData;
  } catch (e) {
    console.log("Error reading languages.json");
    console.error(e);
    process.exit(-1);
  }
}

async function createPathIfNeeded(fs, fse, path, onFinish) {
  fs.exists(path, async function (exists) {
    if (exists) onFinish();
    else {
      const wd = path.split("/").slice(0,-1).join("/");
      if (wd.includes("/")) await fse.ensureDir(wd);
      await fs.promises.writeFile(path, '');
      onFinish();
    }
  });
}

async function downloadTo(https, fs, fse, url, path) {
  return new Promise((resolve, reject) => {
    createPathIfNeeded(fs, fse, path, () => {
      const file = fs.createWriteStream(path);
      try {
        https.get(url, function (response) {
          response.pipe(file);

          // after download completed close filestream
          file.on("finish", () => {
            file.close();
            console.log("Download completed from url " + url + " to " + path);

            // If file is empty, delete it again as gradle build will fail otherwise
            fs.readFile(path, function(err, data) {
              if (data.length == 0) {
                fs.unlink(path, (err) => {
                  if (err) {
                      console.log(err);
                  }
                  console.log('Deleted file ' + path + "again as it was empty.");
              })
              }
            })

            resolve();
          });
        });
      } catch (e) {
        reject("Download failed: " + e);
      }
    });
  });
}

function newPOEditor() {
  return require("node-poeditor");
}

function preprocessLanguage(language) {
  if (language.android_path && !Array.isArray(language.android_path)) {
    language.android_path = [language.android_path];
  }
  return language;
}

function constructAndroidXMLPaths(languageImportData, language) {
  return language.android_path.map(path => (
    languageImportData.workspace_path +
    languageImportData.android_base_path +
    path +
    languageImportData.android_file_name
  ));
}

function constructFastlanePath(languageImportData, language) {
  return (
    languageImportData.workspace_path +
    languageImportData.fastlane_base_path +
    language.fastlane_path +
    languageImportData.fastlane_file_name
  );
}

async function getPOEditorAndroidXMLDownloadURL(poconnect, token, lang) {
  console.log("Getting Android xml files for: " + lang);

  const projectId = "331983";
  const data = {
    language: lang,
    type: "android_strings",
    tags: '["app"]',
    filters: '["translated"]',
  };

  try {
    const result = await poconnect.projects.export(token, projectId, data);

    return result.url;
  } catch (err) {
    console.error(err);
    process.exit(2);
  }
}

async function getPOEditorFastlaneMetadataDownloadURL(poconnect, token, lang) {
  console.log("Getting fastlane metadata files for: " + lang);

  const projectId = "331983";
  const data = {
    language: lang,
    type: "key_value_json",
    tags: '["store"]',
    filters: '["translated"]',
  };

  try {
    const result = await poconnect.projects.export(token, projectId, data);

    return result.url;
  } catch (err) {
    console.error(err);
    process.exit(3);
  }
}

async function processLanguage(
  languageImportData,
  language,
  https,
  fs, fse,
  poconnect,
  token
) {
  const name = language.name;
  const poeditor_code = language.poeditor_code;
  const android_path = language.android_path;
  const fastlane_path = language.fastlane_path;

  console.log("Start processing language: " + name);

  language = preprocessLanguage(language);

  if (android_path) {
    console.log("Getting URL for android xml download...");
    const url = await getPOEditorAndroidXMLDownloadURL(
      poconnect,
      token,
      poeditor_code
    );
    const paths = constructAndroidXMLPaths(languageImportData, language);
    for (let path of paths) {
      await downloadTo(https, fs, fse, url, path);
    }
  } else console.log("Skipped android xml for " + name);

  if (fastlane_path) {
    console.log("Getting URL for fastlane metadata download...");
    const url = await getPOEditorFastlaneMetadataDownloadURL(
      poconnect,
      token,
      poeditor_code
    );
    const path = constructFastlanePath(languageImportData, language);
    await downloadTo(https, fs, fse, url, path);
  } else console.log("Skipped fastlane metatdata for " + name);

  console.log("Finished language: " + name);
  console.log("");
}

(async () => {
  const token = process.env.POEDITOR_API_TOKEN;

  if (token.length == 0) {
    console.error("No API token for PoEditor provided. Stopping");
    process.exit(4);
  }

  const [fs, fse] = getFileSystem();
  const languageImportData = getLanguageImportData(fs);

  const poconnect = newPOEditor();
  const https = getHttps();

  for (let language of languageImportData.languages) {
    await processLanguage(languageImportData, language, https, fs, fse, poconnect, token);
  }
})();
