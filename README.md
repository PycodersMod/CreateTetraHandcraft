# Create: Tetra Handcraft

一个独立的 Minecraft Forge 附属 Mod 工程，目标是将 Tetra 工具头与 Create: Hand Made 的手工加工能力结合，并补充经确认的 Create 前期机器配方。

## 当前状态

- 当前工程目录：[`forge/1.20.1/`](forge/1.20.1/)
- 当前为开发脚手架，尚未完成游戏内功能、双人联机或发布验收。
- Minecraft 1.20.1、Forge 47.4.23、Java 17 是当前开发目标，不代表已经通过实机兼容验收。
- 临时 Mod ID：`create_tetra_handcraft`，临时 Java 包：`com.pycoder.createtetrahandcraft`。它们是工程初始化标识，发布稳定版前须按项目决议最终确认。

## 开发依赖

开发运行需要 Forge 1.20.1、Tetra 6.17.0、Create 6.0.8 与 Create: Hand Made 0.5.0-beta。Tetra 的必需依赖 mUtil 6.3.0 也由开发配置解析。实际依赖 JAR、哈希和来源证据保存在本机私有记录；仓库不包含第三方 Mod 二进制文件。

## 构建

在 `forge/1.20.1/` 目录使用 Gradle Wrapper：

```powershell
./gradlew.bat clean test build
```

需要 Java 17。仓库不固定本机 JDK 路径；请通过当前终端的 `JAVA_HOME` 或 Gradle Toolchain 提供 Java 17。

## 许可

本项目自身采用 MIT License。依赖 Mod 由各自上游项目单独授权和分发；请在其官方发布页获取，不要从本仓库复制依赖 JAR。
