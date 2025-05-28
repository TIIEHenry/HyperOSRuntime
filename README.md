# HyperOSRuntime

为其他手机系统提供HyperOS系统软件的运行能力



#### 注意事项

- Android版本最好一样，否则可能会有问题
- 版本后缀QC代表高通，MTK代表联通，最好平台也一样
- 仔细阅读模块的README.md，有移植的机型和平台说明



#### 使用教程

1. 刷入对应magisk模块：在magisk/
2. 刷入xposed模块：在app/debug/
3. 安装HyperOS的系统/内置软件，比如天气、相册、相册编辑等（如果包名一样，谨慎选择覆盖你现在系统的APP，可能不兼容）
4. 解锁相册编辑AI功能需要安装apks/小米帐号_1.0.3_NekoYuzu.apk，扫码登录后安装覆盖安装新版
4. 使用Guise伪装原机型解锁AI能力，勾选所有HyperOS应用
5. 用MT管理器提取移植原机型的系统APP，安装到新手机
