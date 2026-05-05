<div align="center">

![Banner](logo.svg)

----

![GitHub Created At](https://img.shields.io/github/created-at/eam2539/RWX?color=blue)
[![Discord](https://img.shields.io/discord/1352880561215246376?label=Discord)](https://discord.gg/q2amh4Gt3f)

**R**usted **W**arfare e**X**tension

重建和扩展 Rusted Warfare 的开源跨平台 RTS 游戏

</div>

> **项目状态**:当前项目仍在积极维护中，欢迎提交 Issue 和 Pull Request!

简体中文, [English](README.md)


## 🗺️ 开发路线图

- [ ] 复原项目结构
    - [x] Desktop build
    - [ ] Android build
- [ ] JVM模组系统
- [ ] 基于P2P的联机系统
- [ ] 合作模式支持
- [ ] 强化学习AI支持

## 💬 社区讨论

* [QQ群组](https://qun.qq.com/universal-share/share?ac=1&authKey=EQ73b%2BVCr5a5mUrJR1yOhp0oJFmDHuBglEPrHJEWmHxSyMZjH4xI2H0hgqXZ%2B3dW&busi_data=eyJncm91cENvZGUiOiI5ODI4MzgwODYiLCJ0b2tlbiI6ImZaMHZqZDJ2UllsZnpHTEQxRHBtanhiZDgwTFVZdXcwV1N6a1dZMm1oQVNaUEZNR1cxSWptV0VBOW9aYXVIYlAiLCJ1aW4iOiIzMDYwMzA5MzQwIn0%3D&data=Maw6iUgqhXNqLNXmhH7V6FVNDiLqVgvN6paLZrQeSiDxJLu8IxDpAhmmCqdXJsJNViI0SCU-oM5_h-buAOY00Q&svctype=4&tempid=h5_group_info)
* [Discord 群组](https://discord.gg/q2amh4Gt3f)

## 🔨 编译 Native 库

### 前置要求

- CMake 3.16+
- C++17 编译器
- JDK 17+ (用于 JNI)

### rocketConnector Native 库

rocketConnector 原生库依赖 libRocket。

**1. 克隆 libRocket：**

```bash
git clone https://github.com/libRocket/libRocket.git
```

**2. 编译 libRocket：**

Linux 或 macOS:

```bash
cd path/to/libRocket/Build
cmake . -DBUILD_SHARED_LIBS=OFF
#for linux
cmake --build . -j$(nproc) 
#for macOS
cmake --build . -j$(sysctl -n hw.logicalcpu)
```

Windows :

```bat
cd path\to\libRocket\Build
cmake --build . --parallel
```

**3. 编译 rocketConnector：**

```bash
# Point to libRocket source directory
export LIBROCKET_ROOT=/path/to/libRocket   
# for Windows: set LIBROCKET_ROOT=C:\path\to\libRocket 
./gradlew desktop:buildRocketConnectorNative 
# for Windows: gradlew.bat desktop:buildRocketConnectorNative
```

## 免责声明

本项目为 Rusted Warfare 的非官方扩展项目，旨在通过现代技术栈扩展游戏功能和玩法。本项目所使用的相关资产均归原作者所有。仅为学习研究目的，禁止商业用途。

---