# 彩虹大马

[![Current Version](https://img.shields.io/github/v/release/MineLittlePony/BigPony)](https://github.com/MineLittlePony/BigPony/releases/latest)
[![Build Status](https://github.com/MineLittlePony/BigPony/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/MineLittlePony/BigPony/actions/workflows/gradle-build.yml)
![Downloads](https://img.shields.io/github/downloads/MineLittlePony/BigPony/total.svg?color=yellowgreen)
[![Discord Server](https://img.shields.io/discord/182490536119107584.svg?color=blueviolet)](https://discord.gg/HbJSFyu)
![License](https://img.shields.io/github/license/MineLittlePony/BigPony)
![](https://img.shields.io/badge/api-fabric-orange.svg)

彩虹大马是MineLittlePony（非必需安装）的联动模组。本模组允许您改变您在世界中的形象，包括模型大小和摄像机位置。这个模组也是由Hepolite制作的[FillyCam](https://github.com//MineLittlePony/FillyCam)的精神续作。

## 多人游戏支持

服务器管理员可以选择性启用或停用本模组中可能显得“作弊”的部分功能，例如摄像机位置和命中框，当然这需要将本模组安装在服务器中才行。


## 构建

1. 需要JDK 17。使用https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot 安装。

2. 在资源（git克隆或是从zip中解压）的同目录中打开一个终端窗口，运行以下指令（使用Windows系统）。

```
gradlew build
```

3. 过一会后，构建出的模组将位于 `/build/libs` 中。

## 安装
需要Fabric （和FabricAPI）。请参考<a href="https://fabricmc.net">这里</a>，并在完成安装后继续续下一步。

安装好fabric后，只需要从[releases page](https://github.com/MineLittlePony/BigPony/releases)下载和您的fabric版本对应的BigPony-version.jar文件，并和fabric-api.jar文件一起放到您的mods文件夹中即可。
**记得使用fabric版本来启动游戏！**


## Maven

稳定版渠道：`https://repo.minelittlepony-mod.com/maven/release`

不稳定版渠道：`https://repo.minelittlepony-mod.com/maven/snapshot`

依赖：`com.minelittlepony:BigPony:<version>`

请检查[releases](https://github.com/MineLittlePony/BigPony/releases)以获取最新发布的版本，或是 `gradle.properties` 的最新快照版本。

