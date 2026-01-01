# Sleepy Lsposed

一个现代化的 Xposed/Lsposed 模块，用于监控 Android 设备活动和媒体播放，使用 Kotlin 和 Material Design 3 构建。

[English](README.md) | 简体中文

## 功能特性

- 🎯 **前台应用监控**：跟踪当前活动的应用程序（按名称，非包名）
- 🎵 **媒体播放检测**：监控来自任何媒体应用的音乐和视频播放
- 🔋 **电池信息**：在报告中包含电池电量和充电状态
- 📡 **灵活的报告方式**：选择单独报告媒体或与应用状态合并
- 🎨 **Material Design 3**：现代化、直观的配置界面
- ⚙️ **完全可配置**：所有设置均可通过应用内偏好设置访问
- 🚀 **高效**：可配置检查间隔，对电池影响最小

## 系统要求

- Android 8.0 (API 26) 或更高版本
- Xposed Framework 或 LSPosed 模块框架
- 媒体检测需要通知监听权限

## 安装

1. 在设备上安装 LSPosed
2. 从 [Releases](https://github.com/gongfuture/sleepy-lsposed/releases) 下载最新的 APK
3. 安装 APK
4. 在 LSPosed 管理器中启用该模块
5. 选择推荐的作用域：
   - `android` (系统框架)
   - `com.android.systemui` (系统界面)
6. 重启设备

## 配置说明

1. 打开 Sleepy Lsposed 应用
2. 配置必需的设置：

### 服务器配置
- **API URL**：你的 Sleepy 服务器端点（必须以 `/device/set` 结尾）
- **密钥**：API 请求的认证密钥

### 设备配置
- **设备 ID**：此设备的唯一标识符
- **设备显示名称**：在服务器上显示的友好名称

### 行为设置
- **检查间隔**：状态检查之间的时间（毫秒，默认：3000）
- **跳过重复更新**：如果状态未改变，则不发送请求
- **包含电池信息**：在报告中添加电池电量和充电状态

### 媒体设置
- **启用媒体检测**：监控并报告媒体播放
- **媒体报告模式**：
  - **与应用状态合并**：在应用状态中包含媒体信息
  - **独立设备报告**：将媒体作为单独的设备报告
- **媒体设备 ID/名称**：单独报告媒体时使用

3. 从菜单启动服务（右上角三个点）

## 架构设计

### 组件

- **SleepyXposedModule**：Hook Android 系统以监控前台应用
- **SleepyMonitorService**：定期检查状态的后台服务
- **MediaMonitor**：监控活动的媒体会话
- **SleepyApiClient**：处理与服务器的 HTTP 通信
- **ConfigManager**：管理用户偏好设置
- **SettingsActivity**：Material Design 3 配置界面

### Hook 机制

该模块 Hook 到 `com.android.server.wm.ActivityRecord.setState()` 以检测应用进入前台的时机。这提供了实时的应用切换检测，无需轮询。

### 媒体检测

使用 Android 的 `MediaSessionManager` API 检测来自任何正确实现媒体会话的应用（如 Spotify、YouTube Music 等）的活动媒体播放。

## API 协议

模块向配置的 API 端点发送 JSON POST 请求：

```json
{
  "secret": "your_secret",
  "id": "device_id",
  "show_name": "设备名称",
  "using": true,
  "app_name": "[🔋85%⚡] 前台应用: Chrome\n【正在播放】: ♪歌曲名 - 艺术家"
}
```

## 开发

### 构建

```bash
./gradlew assembleRelease
```

APK 将位于 `app/build/outputs/apk/release/`

### 项目结构

```
app/src/main/
├── java/com/sleepy/lsposed/
│   ├── config/          # 配置管理
│   ├── data/            # 数据模型
│   ├── network/         # API 客户端
│   ├── service/         # 后台服务
│   ├── ui/              # 用户界面
│   ├── utils/           # 工具类
│   └── *.kt             # Xposed hooks
├── res/                 # 资源文件
└── AndroidManifest.xml
```

### 技术栈

- **语言**：Kotlin
- **界面**：Material Design 3
- **网络**：OkHttp
- **异步**：Kotlin Coroutines
- **框架**：Xposed/LSPosed API

## 实施的最佳实践

- ✅ Material Design 3 指南
- ✅ Android Architecture Components
- ✅ Kotlin Coroutines 用于异步操作
- ✅ 前台服务用于后台工作
- ✅ 适当的通知渠道
- ✅ SharedPreferences 用于配置
- ✅ 高效的电池使用
- ✅ 错误处理和日志记录

## 故障排除

### 模块不工作
1. 确保 LSPosed 已正确安装
2. 检查模块是否在 LSPosed 管理器中启用
3. 验证是否选择了正确的作用域（android, com.android.systemui）
4. 启用/更改作用域后重启

### 媒体检测不工作
1. 授予通知监听权限
2. 确保媒体应用正确实现 MediaSession API
3. 检查设置中是否打开了"启用媒体检测"

### 状态未更新
1. 验证所有配置字段是否已填写
2. 检查 API URL 是否正确
3. 查看日志中的网络错误
4. 确保服务正在运行（检查通知）

## 致谢

基于 [Sleepy 项目](https://github.com/sleepy-project/sleepy)：
- JavaScript 客户端：`autoxjs_device.js`
- Python 客户端：`win_device_ds.py`

## 许可证

本项目采用 GNU Affero 通用公共许可证 v3.0 (AGPL-3.0) 授权。

详见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎贡献！请：
1. Fork 仓库
2. 创建功能分支
3. 进行更改
4. 提交 pull request

## 支持

如有问题、疑问或建议，请在 GitHub 上提出 issue。
