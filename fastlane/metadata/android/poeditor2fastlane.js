const fs = require('fs');
const fse = require('fs-extra');
let dirs = [];

let prefix = "metadata/android/";

langs = fs.readdirSync(prefix);

langs.forEach(lang => {
    if (fs.lstatSync(prefix + lang).isDirectory() ) {
        content = fs.readdirSync(prefix + lang);

        if (content.indexOf('poeditor.json') > -1) {
            dirs.push(prefix + lang);
        }
    }
});

console.log('Found poeditor.json files for the following languages: ' + dirs);

dirs.forEach(dir => {
    let rawdata = fs.readFileSync(dir + '/poeditor.json');
    try {
        let store_translations = JSON.parse(rawdata);

        for (var comment in store_translations) {
            for (var term in store_translations[comment]) {
                if (term.match("store_changelog_*")) {
                    let version_code = term.split("_")[2];
                    let changelog = store_translations[comment][term];

                    let filename = dir + "/changelogs/" + version_code + ".txt";

                    fse.outputFile(filename, changelog, err => {
                      if(err) {
                        console.log(err);
                      } else {
                        console.log(filename + ' saved!');
                      }
                    })
                }
                if (term.match("store_short_description")) {
                    let store_short_description = store_translations[comment][term] + "\n";

                    let filename = dir + "/short_description.txt";

                    fse.outputFile(filename, store_short_description, err => {
                      if(err) {
                        console.log(err);
                      } else {
                        console.log(filename + ' saved!');
                      }
                    })
                }
                if (term.match("store_full_description")) {
                    let store_full_description = store_translations[comment][term] + "\n";

                    let filename = dir + "/full_description.txt";

                    fse.outputFile(filename, store_full_description, err => {
                      if(err) {
                        console.log(err);
                      } else {
                        console.log(filename + ' saved!');
                      }
                    })
                }
            }
        }
    } catch(Exception) {

    }
});

console.log("Finished importing!")
