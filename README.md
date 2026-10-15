# HackClient - Fabric mod for Minecraft 26.2 (Java 25)

Press **Right Shift** (rebindable in Controls) or type `.gui` to open the menu.
Chat commands (prefix "."): .help .list .t <module> .pos1 .pos2 .speed <n>
.xray add|remove <block> | .xray list   .bf list | mode base|amethyst|both | min <n>

## Getting the jar (no local setup): GitHub Actions
1. Create a new GitHub repo and upload everything in this folder (keep .github/).
2. Open the repo's Actions tab -> "Build mod jar" -> wait ~3 min.
3. Download the `hackclient-jar` artifact, unzip, and put the .jar in `.minecraft/mods`
   together with Fabric Loader 0.19.3+ and Fabric API for 26.2.

## Building locally
Install JDK 25 and Gradle 9.5.1, then run `gradle build`. The jar lands in build/libs/.
(`gradle runClient` launches a dev client.)

## 26.3
Edit gradle.properties: set minecraft_version and fabric_api_version to the 26.3 builds.
