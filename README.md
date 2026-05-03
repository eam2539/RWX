<div align="center">

![Banner](logo.svg)

----

![GitHub Created At](https://img.shields.io/github/created-at/eam2539/RWX?color=blue)
[![Discord](https://img.shields.io/discord/1352880561215246376?label=Discord)](https://discord.gg/q2amh4Gt3f)

**R**usted **W**arfare e**X**tension

Rebuilding and extending Rusted Warfare as an open-source cross-platform RTS game

</div>

English, [简体中文](README_zh.md)


## 🗺️ Roadmap

- [ ] Reconstruct project structure
    - [x] Desktop build
    - [ ] Android build
- [ ] JVM modding system
- [ ] P2P-based multiplayer system
- [ ] Co-op mode support
- [ ] Reinforcement learning AI support

## 💬 Community Discussion

* [Discord Server](https://discord.gg/q2amh4Gt3f)

## 🔨 Building Desktop

### Prerequisites

- CMake 3.16+
- C++17 compiler
- JDK 11+ (for JNI)

### rocketConnector Native Library

The rocketConnector native library requires libRocket.

**1. Clone libRocket:**

```bash
git clone https://github.com/libRocket/libRocket.git
```

**2. Build libRocket:**

Linux or macOS:

```bash
cd path/to/libRocket/Build
cmake . -DBUILD_SHARED_LIBS=OFF
#for linux
cmake --build . -j$(nproc)
#for macOS
cmake --build . -j$(sysctl -n hw.logicalcpu)
```

Windows:

```bat
cd path\to\libRocket\Build
cmake --build . --parallel
```

**3. Build rocketConnector:**

```bash
# Point to libRocket source directory
export LIBROCKET_ROOT=/path/to/libRocket   # for Windows: set LIBROCKET_ROOT=C:\path\to\libRocket 
./gradlew desktop:buildRocketConnectorNative # for Windows: gradlew.bat desktop:buildRocketConnectorNative
```

## Disclaimer

This is an unofficial extension project for Rusted Warfare, aiming to extend game functionality and gameplay through a
modern technology stack. All related assets used in this project belong to their original authors.
For educational and research purposes only, commercial use is prohibited.

---
