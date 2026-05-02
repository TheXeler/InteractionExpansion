# Interaction Expansion - KubeJS 集成使用文档

## 目录
- [简介](#简介)
- [安装与配置](#安装与配置)
- [基本用法](#基本用法)
- [API 参考](#api-参考)
- [示例脚本](#示例脚本)
- [高级用法](#高级用法)
- [常见问题](#常见问题)

## 简介

Interaction Expansion 是一个 Minecraft Forge 模组，它通过 KubeJS 提供了强大的方块交互系统。允许玩家通过 JavaScript 脚本自定义方块的交互行为，无需编写 Java 代码即可实现复杂的交互逻辑。

## 安装与配置

### 前置要求
- Minecraft 1.20.1
- Forge 47.x
- KubeJS 模组
- Interaction Expansion 模组

### 自动配置
Interaction Expansion 已通过 SPI 机制自动注册 KubeJS 插件，无需手动配置。插件类位于：
```

cc.sighs.interactionexpansion.InteractionExpansionJSPlugins
```
## 基本用法

### 添加交互

```
javascript
// 基础语法 - 为方块添加交互
InteractionExpansion.addInteraction("minecraft:stone", (context) => {
    // 交互逻辑
});

// 带名称的交互
InteractionExpansion.addInteraction("minecraft:stone", "我的交互", (context) => {
    // 交互逻辑
});
```
### 清除交互

```
javascript
// 清除特定方块的所有交互
InteractionExpansion.clearInteractions("minecraft:stone");

// 清除所有交互
InteractionExpansion.clearAll();
```
## API 参考

### InteractionExpansion 对象

#### `addInteraction(blockId, handler)`
- **参数**:
  - `blockId` (string): 方块的资源位置，如 "minecraft:stone"
  - `handler` (function): 交互处理函数，接收一个 InteractionContextWrapper 参数
- **说明**: 为指定方块添加一个交互，自动生成名称（格式："交互 #N"）

#### `addInteraction(blockId, name, handler)`
- **参数**:
  - `blockId` (string): 方块的资源位置
  - `name` (string): 交互的名称
  - `handler` (function): 交互处理函数
- **说明**: 为指定方块添加一个带有自定义名称的交互

#### `clearInteractions(blockId)`
- **参数**:
  - `blockId` (string): 方块的资源位置
- **说明**: 清除指定方块的所有交互

#### `clearAll()`
- **参数**: 无
- **说明**: 清除所有方块的所有交互

### InteractionContextWrapper 对象

当交互被触发时，会传入一个 `InteractionContextWrapper` 对象，包含以下方法：

#### `getBlockId()`
- **返回**: string - 当前交互方块的资源位置
- **说明**: 获取触发交互的方块 ID

#### `setData(key, value)`
- **参数**:
  - `key` (string): 数据键名
  - `value` (any): 要存储的值
- **说明**: 在交互上下文中设置数据

#### `getData(key)`
- **参数**:
  - `key` (string): 数据键名
- **返回**: any - 存储的值
- **说明**: 从交互上下文中获取数据

#### `getSelectedIndex()`
- **返回**: number - 选中的索引
- **说明**: 获取当前选中的交互项索引（默认为 0）

#### `getInteractionName()`
- **返回**: string - 交互名称
- **说明**: 获取当前交互的名称

#### `getPlayer()`
- **返回**: Player 对象
- **说明**: 获取触发交互的玩家对象

#### `getLevel()`
- **返回**: Level 对象
- **说明**: 获取当前世界/维度对象

#### `getPos()`
- **返回**: BlockPos 对象
- **说明**: 获取交互发生的位置坐标

## 示例脚本

### 简单消息提示
```
javascript
// startup_scripts/example.js
InteractionExpansion.addInteraction("minecraft:dirt", (context) => {
    const player = context.getPlayer();
    player.tell("你点击了泥土方块！");
});
```
### 多交互示例
```
javascript
// startup_scripts/multi_interaction.js

// 第一个交互
InteractionExpansion.addInteraction("minecraft:stone", "挖掘石头", (context) => {
    const player = context.getPlayer();
    player.tell("你正在挖掘石头...");
    
    // 给予物品
    player.give("minecraft:cobblestone", 1);
});

// 第二个交互
InteractionExpansion.addInteraction("minecraft:stone", "检查石头", (context) => {
    const player = context.getPlayer();
    const pos = context.getPos();
    player.tell(`石头位置: ${pos.x}, ${pos.y}, ${pos.z}`);
});
```
### 条件交互
```
javascript
// startup_scripts/conditional_interaction.js
InteractionExpansion.addInteraction("minecraft:iron_ore", "开采铁矿", (context) => {
    const player = context.getPlayer();
    const level = context.getLevel();
    
    // 检查玩家是否持有铁镐
    if (player.mainHandItem.id === "minecraft:iron_pickaxe") {
        player.tell("成功开采铁矿！");
        player.give("minecraft:raw_iron", 1);
        
        // 破坏方块
        level.setBlock(context.getPos(), "minecraft:air", 3);
    } else {
        player.tell("你需要铁镐才能开采这个铁矿！");
    }
});
```
### 数据存储示例
```
javascript
// startup_scripts/data_storage.js
InteractionExpansion.addInteraction("minecraft:chest", "特殊箱子", (context) => {
    const player = context.getPlayer();
    
    // 获取交互次数
    let count = context.getData("clickCount") || 0;
    count++;
    
    // 保存更新后的计数
    context.setData("clickCount", count);
    
    player.tell(`这是你第 ${count} 次点击这个箱子`);
    
    if (count >= 5) {
        player.tell("恭喜你获得了奖励！");
        player.give("minecraft:diamond", 1);
        context.setData("clickCount", 0); // 重置计数
    }
});
```
## 高级用法

### 访问 Minecraft 原生对象

由于 KubeJS 的限制，你可以直接访问 Minecraft 的原生对象：

```
javascript
// startup_scripts/advanced.js
InteractionExpansion.addInteraction("minecraft:crafting_table", "高级工作台", (context) => {
    const player = context.getPlayer();
    
    // 访问玩家的 NBT 数据
    const nbt = player.nbt;
    
    // 执行命令
    player.startup.runCommandSilent(`give ${player.username} minecraft:diamond 1`);
    
    // 获取玩家所在维度
    const dimension = player.level.dimension;
    player.tell(`你在维度: ${dimension}`);
});
```
### 动态交互名称

```
javascript
// startup_scripts/dynamic_names.js
let counter = 1;
InteractionExpansion.addInteraction("minecraft:gold_block", `金块交互 #${counter}`, (context) => {
    const player = context.getPlayer();
    player.tell("这是一个特殊的金块交互！");
    counter++;
});
```
## 常见问题

### Q: 如何调试脚本？
A: 使用 `player.tell()` 输出调试信息，或查看游戏日志文件。

### Q: 交互何时触发？
A: 当玩家右键点击已配置交互的方块时触发。

### Q: 支持哪些方块？
A: 支持所有注册的方块，包括原版和模组方块。使用格式："modid:block_name"。

### Q: 如何处理多个相同方块的交互？
A: 每个方块实例共享相同的交互定义，但可以通过 `context.setData()` 存储每个实例的独立数据。

### Q: 脚本放在哪里？
A: 将 JavaScript 脚本放置在 `.minecraft/kubejs/startup_scripts/` 目录下。

### Q: 如何重新加载脚本？
A: 在游戏中执行 `/kubejs reload` 命令来重新加载所有脚本。

## 注意事项

1. 脚本更改后需要重新加载才能生效
2. 确保方块 ID 格式正确（modid:block_name）
3. 错误处理很重要，建议在关键操作周围添加异常处理
4. 大量交互可能影响性能，请合理使用

---

*本文档基于 Interaction Expansion 模组的实际实现编写，适用于 Minecraft 1.20.1 版本。*
