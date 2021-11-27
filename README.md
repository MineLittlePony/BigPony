# BigPony

[![Build Status](https://travis-ci.org/MineLittlePony/BigPony.svg?branch=1.18)](https://travis-ci.org/MineLittlePony/BigPony)
![Downloads](https://img.shields.io/github/downloads/MineLittlePony/BigPony/total.svg?color=yellowgreen)
[![Discord Server](https://img.shields.io/discord/182490536119107584.svg?color=blueviolet)](https://discord.gg/HbJSFyu)
![License](https://img.shields.io/github/license/MineLittlePony/BigPony)
![](https://img.shields.io/badge/api-fabric-orange.svg)

Big Pony is a companion mod for MineLittlePony (though is not required). It allows you to customize the way you appear in the world, including model scaling and camera position. It is the spiritual successor to [FillyCam](https://github.com//MineLittlePony/FillyCam) by Hepolite.

## Multiplayer Support

Server admins have the ability to enable or disable various parts of this mod that may be considered "cheaty". Things like camera position and hitbox are opt-in, and will require the server to have this mod installed to allow.


## Building

1. JDK 17 is required. Install it using https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot

2. Open a terminal window in the same directory as the sources (git clone or extracted from zip). Run the following command (windows).

```
gradlew build
```

3. After some time, the built mod will be in `/build/libs`.

## Installation

Fabric (And FabricAPI) are required. Please refer to their installation instructions <a href="https://fabricmc.net">here</a> and come back once you have Fabric functioning.

Once you have fabric installed, simply download the BigPony-version.jar for your particular version from [the releases page](https://github.com/MineLittlePony/BigPony/releases) and place it into your mods folder next to the fabric-api jar

**Remember to use the fabric launcher profile when starting the game!**


## Maven

Stable Releases Channel: `https://repo.minelittlepony-mod.com/maven/release`

Unstable Snapshot Channel: `https://repo.minelittlepony-mod.com/maven/snapshot`

Dependency: `com.minelittlepony:BigPony:<version>`

Check [releases](https://github.com/MineLittlePony/BigPony/releases) for the most recent release version or the `gradle.properties` for most recent snapshot version.

