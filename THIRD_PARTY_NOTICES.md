# 第三方依赖说明

本仓库不再分发第三方 Mod 的 JAR、源码或美术资源。开发和运行测试时从各自上游发布源解析依赖。

| Mod | 目标版本 | 上游 | 当前核验说明 |
|---|---|---|---|
| Tetra | 6.17.0 | https://www.curseforge.com/minecraft/mc-mods/tetra | 官方发布文件；JAR 元数据标为 ARR，不随本项目重新分发 |
| mUtil | 6.3.0 | https://www.curseforge.com/minecraft/mc-mods/mutil | Tetra 的必需运行依赖 |
| Create | 6.0.8 | https://www.curseforge.com/minecraft/mc-mods/create | Create JAR 元数据声明 MIT；CurseForge 项目页显示 Custom License，需在任何再分发场景前核清许可差异。本仓库不再分发其二进制 |
| Create: Hand Made | 0.5.0-beta | https://www.curseforge.com/minecraft/mc-mods/create-hand-made | Forge 1.20.1 beta 发布文件；JAR 与项目页均标示 MIT |

具体文件 ID、JAR SHA-256、内部 Mod ID/版本和内部依赖元数据见私有本机依赖锁记录。以上核验不等于功能兼容通过。
