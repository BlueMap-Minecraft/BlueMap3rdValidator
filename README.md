# BlueMap Wiki 3rd Party Addons File Validator

This tool validates the BlueMap Wiki's [3rd party addons file](https://github.com/BlueMap-Minecraft/BlueMapWiki/blob/master/assets/addon_browser/addons.conf).\
It checks if the file is valid [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md)
and if all required fields are present.

Designed to be used in GitHub Actions.


## Usage

```bash
./gradlew run --args="/path/to/BlueMapWiki/"
```
