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
    console.log("Found " + languageImportData.languages.length + " languages in languages.json.");
    return languageImportData;
  } catch (e) {
    console.log("Error reading languages.json");
    console.error(e);
    process.exit(-1);
  }
}

function newPOEditor() {
  return require("node-poeditor");
}

async function getPoEditorLanguages(poconnect, token) {
    try {
        console.log("Fetching languages from PoEditor...");
        const projectId = "331983";
        let data = await poconnect.languages.list(token, projectId);
        console.log("Found " + data.languages.length + " languages on PoEditor.")
        return data.languages;
    } catch (err) {
        console.error("Failed fetiching langauges from PoEditor: ", err);
        process.exit(5);
    }
}

async function compareLanguages(languageImportData, poconnect, token) {
    let shouldAlert = false;
    let newLangConfig = [];

    const languagesPOE = await getPoEditorLanguages(poconnect, token);
    const languagesJSON = languageImportData.languages.map(lang => {return {"name": lang.name, "code": lang.poeditor_code}});

    const poeCodes = languagesPOE.map(l => l.code);
    const jsonCodes = languagesJSON.map(l => l.code);

    for (let lang of languagesPOE) {
        if (jsonCodes.includes(lang.code)) {
            console.log(`We have ${lang.name} both on PoEditor and in languages.json`);
        } else {
            shouldAlert = true;
            console.log(`The language ${lang.name} (code: ${lang.code}, percentage: ${lang.percentage}) is only on POEditor.`);
            console.log(`Consider adding it to the languages.json.`);
            const newConfig = {
                "name": lang.name,
                "poeditor_code": lang.code,
                "android_path": "(to be found out)",
                "fastlane_path": "(to be found out)",
                "play_store_code": "(to be found out)"
            };
            // console.log(newConfig);
            newLangConfig.push(newConfig);
        }
    }

    for (let lang of languagesJSON) {
        if (!poeCodes.includes(lang.code)) {
            console.log(`The language ${lang.name} (code: ${lang.code}) is only in languages.json.`);
            console.log("Consider adding it here: https://poeditor.com/projects/view?id=331983");
        }
    }

    if (shouldAlert) {
        console.log("\n\nHere are the new langauges:\n+++++++++++++++++++++++++++++++++++++++++\n", JSON.stringify(newLangConfig, null, 4));
    }
}

(async () => {
  const token = process.env.POEDITOR_API_TOKEN;

  if (token.length == 0) {
    console.error("No API token for PoEditor provided. Stopping.");
    process.exit(4);
  }

  const [fs, fse] = getFileSystem();
  const languageImportData = getLanguageImportData(fs);

  const poconnect = newPOEditor();
  const https = getHttps();

  compareLanguages(languageImportData, poconnect, token);
})();
