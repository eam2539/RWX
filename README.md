<div align="center">

![Banner](logo.svg)

----

![GitHub Created At](https://img.shields.io/github/created-at/eam2539/RWX?color=blue&style=for-the-badge)
[![Discord](https://img.shields.io/discord/1352880561215246376?style=for-the-badge&logo=discord)](https://discord.gg/q2amh4Gt3f)
[![Netlify Status](https://api.netlify.com/api/v1/badges/5c73d6b0-e2f9-46d7-a0d2-271b8f81b6b2/deploy-status)](https://app.netlify.com/projects/rwx-docs/deploys)

**R**usted **W**arfare e**X**tension

Rebuilding and extending Rusted Warfare as an open-source cross-platform RTS game

</div>

> **Project Status**: Actively maintained. Issues and pull requests are welcome.

English, [简体中文](README_zh.md)

## Highlights

- **Desktop + Android** supported
- **P2P multiplayer** with WebRTC DataChannel transport and libp2p discovery/signaling
- **Area Control** mode: capture zones, continuous scoring, new win conditions
- **Linked Maps**: connect multiple maps with portals and transfer units between them
- **JVM modding** for deeper gameplay extensions

## Roadmap

### Platforms

- [x] Desktop build (Windows / Linux / macOS)
- [x] Android build

### Multiplayer

- [x] P2P-based multiplayer system
- [ ] Dedicated relay / fallback path improvements
- [ ] Co-op mode *(not planned right now)*

### Gameplay features

- [x] Area Control mode
- [x] Linked Maps / map portals
- [ ] Balance / UX polish for Area Control and Linked Maps

## Building

Java 25 is required. Common release tasks:

```bash
# Current-platform fat JAR and jpackage app image
./gradlew :desktop:platformFatJar :desktop:packageDesktopDistribution

# One large JAR containing every supported desktop native library
./gradlew :desktop:multiPlatformFatJar

# Android App Bundle, or per-ABI plus universal APKs
./gradlew :android:bundleRelease
./gradlew -PrwxAndroidAbiSplits=true :android:assembleRelease
```

Desktop jpackage images must be built on their target OS. Set `-PrwxTargetPlatform=<id>`
when needed; supported IDs are `linux-x64`, `linux-arm64`, `windows-x64`, `macos-x64`, and `macos-arm64`.

Android signing is read from `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and
`KEY_PASSWORD` (or the equivalent `rwxAndroid*` Gradle properties).

GitHub Actions builds all desktop targets, the universal JAR, Android APKs/AAB, and publishes checksummed assets for
`v*` tags. Configure these repository secrets for signed tag releases: `ANDROID_KEYSTORE_BASE64`,
`ANDROID_KEYSTORE_PASSWORD`,
`ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD`.

See the [workflow](.github/workflows/deploy.yml) and the
[getting started guide](https://rwx.netlify.app/tutorial/getting-started) for more details.

## Disclaimer

This is an unofficial extension project for Rusted Warfare, aiming to extend game functionality and gameplay through a
modern technology stack. All related assets used in this project belong to their original authors.
For educational and research purposes only, commercial use is prohibited.

---
