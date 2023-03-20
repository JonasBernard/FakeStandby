function getFileSystem() {
  return [require("fs"), require("fs-extra")];
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

function constructWhatsNewPath(languageImportData, language) {
  return (
    languageImportData.workspace_path +
    languageImportData.play_store_base_path +
    languageImportData.play_store_file_name
        .replace("{{language.play_store_code}}", language.play_store_code)
  );
}

function getPOEditorJSONFilePath(languageImportData, language) {
    return (
        languageImportData.workspace_path +
        languageImportData.fastlane_base_path +
        language.fastlane_path +
        languageImportData.fastlane_file_name
      );
}

function readPOEditorJSONFile(languageImportData, language, fs) {
    const path = getPOEditorJSONFilePath(languageImportData, language);
    console.log("Reading file " + path);
    let rawdata = "";
    try {
      rawdata = fs.readFileSync(path);
    } catch (e) {
      console.log("Error reading file. Maybe it does not exist because no translations are available.")
    }
    if (!rawdata || rawdata == "") {
        console.error("No translated data found. Skipping language.");
        return {};
    }

    try {
        let translated_data = JSON.parse(rawdata);
        return translated_data;
    } catch (e) {
        console.log("Error reading poeditor.json for language " + language.name);
        console.error(e);
        process.exit(-1);
    }
}

function getLatestChangelog(languageImportData, language, fs) {
    const translated_data = readPOEditorJSONFile(languageImportData, language, fs);
    if (Object.keys(translated_data).length == 0) return [null, null, null];

    let translated_changelogs = Object.keys(translated_data)
        .filter(string_name => string_name.includes("Changelog for version"));
    
    let latest_changelog_string_name = translated_changelogs.pop();
    const re = /version (\d+(\.\d)*)/i;
    let version_tag = 'v' + latest_changelog_string_name.match(re)[0];
    
    let latest_changelog_string_value = translated_data[latest_changelog_string_name];
    let key = Object.keys(latest_changelog_string_value)[0];
    let translated_changelog = latest_changelog_string_value[key];

    const re2 = /store_changelog_(\d+)/i
    let version_number = key.match(re2)[0];

    return [version_tag, version_number, translated_changelog];
}

async function writeToFile(fse, path, data) {
    return new Promise((resolve, reject) => {
        fse.outputFile(path, data, reject);
        resolve();
    });
}

async function processLanguage(
    languageImportData,
    language,
    fs,
    fse
) {
  const name = language.name;
  const play_store_code = language.play_store_code;

  console.log("Start processing language: " + name);

  if (play_store_code) {
    const [version_tag, version_number, translated_changelog] = getLatestChangelog(languageImportData, language, fs);

    if (version_tag == null || version_number == null || translated_changelog == null) {
        console.log("Skipping " + name + " as it has no translation for the latest changelog.");
    } else {
        const path = constructWhatsNewPath(languageImportData, language);
        console.log("Writing changelog for " + name + " to " + path);
        await writeToFile(fse, path, translated_changelog);
    }
  } else console.log("Skipped " + language.name + " as it has no play store support.");


  console.log("Finished language: " + name);
  console.log("");
}

(async () => {
  const [fs, fse] = getFileSystem();
  const languageImportData = getLanguageImportData(fs);

  for (let language of languageImportData.languages) {
    await processLanguage(languageImportData, language, fs, fse);
  }
})();
