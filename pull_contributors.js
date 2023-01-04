const fs = require('fs');
const fse = require('fs-extra');

// get tokens
async function readTokens() {
    try {
        return tokens = {
          github: process.env.GH_ACCESS_TOKEN,
          poeditor: process.env.POEDITOR_API_TOKEN,
        }
    } catch (error) {
        console.log(error);
        process.exit(1);
    }
}

function newOctoKit(token) {
    const { Octokit } = require("@octokit/rest");
    return new Octokit({
        auth: token,
        userAgent: 'nodeJS fastlane repo stats v0.1',
        timeZone: 'Europe/Berlin',
    });
}

async function getGitHubContributors(octokit) {
    const contributors = await octokit.repos.listContributors({
       owner: 'JonasBernard',
       repo: 'FakeStandby'
    });
    return contributors.data.filter(contributor => contributor.id != 32138575); // filter myself (JonasBernard)
}

function newPOEditor() {
    return require('node-poeditor');
}

// query from poeditor
async function getPOEditorStats(poconnect, token) {
  const projectId = '331983';
  try {
    const rawLanguageCount = await poconnect.languages.list(token, projectId);
    const rawContributors = await poconnect.contributors.list(token, projectId); // lang is an optional last argument

    return {
            'languages': rawLanguageCount.languages,
            'contributors': rawContributors.contributors
        }
  } catch (err) {
        console.error(err);
        process.exit(2);
  }
}

// render template
function renderTemplate(githubContributors, poeditorStats) {
  const nunjucks = require('nunjucks');
  return nunjucks.render('CONTRIBUTORS.template.md', {
    'githubContributors': githubContributors,
    'languages': poeditorStats.languages,
    'poeditorContributors': poeditorStats.contributors
  });
}

// write to file
async function writeFile(fileContent) {
  try {
    await fse.outputFile("CONTRIBUTORS.md", fileContent);
  } catch (error) {
     console.log(error);
     process.exit(3);
  }
}

(async () => {
    const tokens = await readTokens();

    const octokit = newOctoKit(tokens.github);
    const githubContributors = await getGitHubContributors(octokit);

    const poconnect = newPOEditor();
    const poeditorStats = await getPOEditorStats(poconnect, tokens.poeditor);

    const template = renderTemplate(githubContributors, poeditorStats);
    await writeFile(template);
})();
