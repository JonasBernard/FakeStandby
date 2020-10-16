const fs = require('fs');
const fse = require('fs-extra');
let dirs = [];

langs = fs.readdirSync('.')

langs.forEach(lang => {
    if (fs.lstatSync(lang).isDirectory() ) {
        content = fs.readdirSync(lang)

        if (content.indexOf('poeditor.json') > -1) {
            dirs.push(lang);
        }
    }
});

console.log('Found json files for the following languages: ' + dirs);

dirs.forEach(dir => {
    let rawdata = fs.readFileSync(dir + '/poeditor.json');
    try {
        let changelogs = JSON.parse(rawdata);

        for (var comment in changelogs) {
            for (var term in changelogs[comment]) {
                if (term.match("store_changelog_*")) {
                    let version_code = term.split("_")[2];
                    let changelog = changelogs[comment][term];

                    let filename = dir + "/changelogs/" + version_code + ".txt";

                    fse.outputFile(filename, changelog, err => {
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
