# 快速开始

欢迎来到 RWX。本页帮助你快速了解平台支持与核心特性。

## 系统要求

RWX 支持：

- Windows 7 或更高版本
- 带有 OpenGL 支持的 Linux
- Android 6.0 或更高版本
- macOS（社区测试；维护者设备有限）

## 平台

| 平台                               | 状态   |
|------------------------------------|--------|
| Desktop（Windows / Linux / macOS） | 已支持 |
| Android                            | 已支持 |

发行包由项目 CI
构建。当前打包矩阵见 [GitHub Actions 工作流](https://github.com/eam2539/RWX/blob/main/.github/workflows/ci.yml)。

## 特性一览

| 特性     | 状态                              | 文档                       |
|----------|-----------------------------------|----------------------------|
| P2P 联机 | 可用（复杂 NAT 下连通性仍属实验） | [P2P](./p2p)               |
| 区域控制 | 可用                              | [区域控制](./area-control) |
| 地图联通 | 可用                              | [地图联通](./linked-maps)  |

## 从源码构建

1. 安装 JDK 25。
2. 克隆 [RWX](https://github.com/eam2539/RWX)。
3. 使用仓库 Gradle 任务构建 Desktop / Android。
4. 对外分发时优先对齐 CI 流程。

具体任务名会随 Gradle 脚本演进，以 CI 为准。

## 下一步

- [P2P 联机](./p2p)
- [区域控制](./area-control)
- [地图联通](./linked-maps)
- [模组介绍](/zh/modding/introduction)
- [社区](/zh/community)
