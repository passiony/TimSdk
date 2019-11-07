# Tencent IM  Unity Demo
基于腾讯IM的Unity Demo


# 前言
一般游戏中的即时聊天，可以在unity中自己直接做，但是需要的功能比较复杂是，比如图片，语言，小视频等丰富功能是，就不是那么好实现了，现在腾讯有一个IM可以接入，里面包含了很多现有的功能，不过缺点就是，IM没有专门为Unity的教程+demo，需要自己摸索夸双平台。

# 产品简介
即时通信（Instant Messaging，IM）基于QQ 底层 IM 能力开发，仅需植入 SDK 即可轻松集成聊天、会话、群组、资料管理能力，帮助您实现文字、图片、短语音、短视频等富媒体消息收发，全面满足通信需要。

支持文字、图片、语音、小视频等丰富的富媒体消息
完善的私聊、群聊、直播间聊天模式
强大的用户资料与群组扩展及管理能力
集成UI开源（TUIKit）组件，节省成本，提高效率

[https://cloud.tencent.com/product/im/developer](https://cloud.tencent.com/product/im/developer)

# Unity集成IM
首先下载各平台demo，看着demo和文档接入
[https://github.com/tencentyun/TIMSDK](https://github.com/tencentyun/TIMSDK)

1.unity接入im，只能使用原生imsdk，不能用自带的tuikit(官方自带UI)
2.接入时统一查看 **常规集成(无UI库)** 的文档 
3.导入的库，也统一使用 **无UI的库**
## 1.android端
1.集成sdk：手动下载 **imsdk.arr**,导入Unity工程的Plugins/Android/libs目录，导出AndroidStudio工程后会自动配置。（或者你也可以根据文档，集成**imsdk.jar**,步骤多一些）
2.导出AS工程后，根据提示Build Gradle。直到运行无错误。
3.新建工具类，TIMSDKUtil.java；里面写一些IM的初始化，登录等方法，供C#端调用。
4.引入Imsdk：`import com.tencent.imsdk.*`，就可以使用IM的API了。
3.接下来的IM方法，就可以根据文档来写了，因为文档写的非常详细，我就不粘贴了。
4.等测试没有问题后，把TimSDKUtil.java 导出jar包，放到unity的Plugins/Android/libs目录下。就可以在unity直接调用IM的方法了

## 2.ios端
1.集成sdk：手动集成，根据地址下载**ImSDK.framework**，先放到桌面，不要导入工程。
2.Unity项目导出xcode工程
3.在xcode工程中添加依赖库ImSDK.framework
注意：
需要在【Build Setting】-【Other Linker Flags】添加 -ObjC。
需要在【Build Phases】-【Link Binary With Libraries】选择ImSDK的status为Optional。
需要在【General】-【Embedded Content】选择ImSDK的Embed为Embed&Sign。
4.如果使用了GenerateTestUserSig类，那么需要引入libz.tbd
5.接下来就可以新建一个IOS的工具类TimSDKBridge.m，里面写上C的方法，供C#调用
5.IM的方法，请参考官方文档，文档很详细，我就不在粘贴了。
6.等测试没有问题，把TimSDKBridge的脚本直接复制到Plugins/IOS下就可以了。就可以在Unity直接调用了。



## 总结
1.无论合适都要以官方文档为最终参考资料[官方文档https://cloud.tencent.com/document/product/269/37176](https://cloud.tencent.com/document/product/269/37176)
2.以上的过程描述都是一些关键点和易错点。因为详细的步骤，官方文档都已经很详细了，我就只写一些注意事项。有问题可以参考我的集成工程。
[https://github.com/passiony/TimSdk](https://github.com/passiony/TimSdk)
3.我的工程中，android和ios回传unity参数统一使用的json格式，因为数据结构比较复杂。
ios端使用的yy_model，
android端我写的3个工具类，最后导出的timsdk.jar包，我已经放到工程的libs下，但是3个java脚本我也放到了Plugins目录下，供大家参考，如果你想运行，可以先删除这3个脚本。

希望这篇文章能够对你有所帮助。