# BigPony
Big Pony is a companion mod for MineLittlePony (though is not required). It allows you to customize the way you appear in the world, including model scaling and camera position. It is the spiritual successor to [FillyCam](https://github.com//MineLittlePony/FillyCam) by Hepolite.

In order to communicate your settings to other players, [PlayerSync](https://github.com/killjoy1221/PlayerSync) is required to be installed on both the client and server. If you don't wish to see others scaling, you do not need to install this.

## Installation
LiteLoader is required. After installing that, move the downloaded .litemod file into `.minecraft/mods`. PlayerSync goes into the same folder.

## Building
Requires jdk8, git, and optionally gradle.

Checkout the PlayerSync repository and build it. You will also need to install it to your local maven repo.

```cmd
git clone https://github.com/killjoy1221/PlayerSync
cd PlayerSync
./gradlew build install
```

Now you can build BigPony normally.
```cmd
cd ..
git clone https://github.com/MineLittlePony/BigPony
cd BigPony
./gradlew build
```
