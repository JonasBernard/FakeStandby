fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android build_apps
```
fastlane android build_apps
```
Build Debug and Test apps for localized screenshots
### android screenshots_phone
```
fastlane android screenshots_phone
```
Take localized screenshots for phones
### android screenshots_sevenInch
```
fastlane android screenshots_sevenInch
```
Take localized screenshots for small tablets
### android screenshots_tenInch
```
fastlane android screenshots_tenInch
```
Take localized screenshots for large tablets
### android import_poeditor
```
fastlane android import_poeditor
```
Import localized changelogs and descriptions from poeditor.json into android metadata
### android fetch_contributors
```
fastlane android fetch_contributors
```
Fetch GitHub and POEditor contributors and render a CONTRIBUTORS.md file

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
