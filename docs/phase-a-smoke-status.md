# 阶段 A 客户端烟测记录

- 目标：Minecraft 1.20.1 / Forge 47.4.23 / Java 17.0.19 / Windows 11。
- 构建：`gradlew.bat --no-daemon clean test build`，退出码 0；ForgeGradle reobfuscation 完成。
- 测试：临时 Mod ID `create_tetra_handcraft` 被 Forge 识别；实际依赖列表包含 Tetra 6.17.0、mUtil 6.3.0、Create 6.0.8、Create: Hand Made 0.5.0-beta。
- 首次失败：Forge language provider 范围误写成 `[47.4.23,48)`；改为 Forge 1.20.1 的语言版本范围 `[47,)` 后通过构建和加载器检查。
- 第二次失败：Create/Hand Made 的 Mixin refmap 在 ForgeGradle `runClient` 中未从 SRG 转成当前开发映射。按 Create Forge 1.20.1 官方开发指南，在 client/server Run 中启用 `mixin.env.remapRefMap` 并配置 `build/createSrgToMcp/output.srg` 后，日志确认 `create_hand_made.refmap.json` 成功 remap，此错误不再出现。
- 第三次失败：游戏初始化在注册 `create_hand_made:press_hammer` 时崩溃。首个异常为 `IllegalStateException: Cannot get config value before config is loaded`，位置为 Hand Made 0.5.0-beta 的 `PressHammerItem` 构造阶段。Forge crash report 明确写明该异常“only thrown in the development environment”。客户端没有进入主菜单，因此本次不是运行通过。
- 当前判定：基础工程 `clean test build` = PASS；ForgeGradle 开发客户端 = BLOCKED（上游 Hand Made JAR 的开发模式配置时序缺陷，需通过标准生产 Forge 实例或上游修复再验证）。未修改、解包重打或替换第三方 JAR。
- 原始日志和 crash report 的副本及 SHA-256 保存在私有交接证据目录，不包含于公开仓库。
