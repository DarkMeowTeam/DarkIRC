<div align=center>
<h1>DarkIRC</h1>
<h4>一个非常简单的聊天软件实现，为 Minecraft 具有入侵性 Mod 设计</h4>
</div>

![在HeyPixel服务器上作弊](/docs/CheatOnHeypixel2025.png)


## 功能
- 连接安全加密 与 客户端签名验证
- 客户端登录检查 (通过配置以禁止低版本客户端登录)
- 用户名密码/token登录
- 公开聊天和用户名之间私有聊天
- 游戏内 ID 共享 (以允许制作同客户端用户不互相攻击)
- 游戏内 披风与皮肤 共享
- 同用户名多设备登录兼容 (当然,你也可以手动禁止它)
- 管理员:游戏内通过管理 IRC 用户

## 构建
`DarkIRC` 使用 Gradle 完成构建, 因此你需要安装它. 你可以在[这里](https://gradle.org/install/)找到它 .

在开始之前, 请检查您的系统上是否已安装 JDK 22 . (低于此版本可能也可以, 我没有进行过测试)

1. 克隆这个存储库 `git clone https://github.com/DarkMeowTeam/DarkIRC/`.
2. `CD`进入这个目录.
3. 使用你的 IDE 打开
4. 构建服务端 `gradle IRCServer:build` 构建客户端 `gradle IRCClient:build`

## 部署服务端

1. 在服务端上安装 JDK 17 (如果已安装该版本或更高版本 JDK 则不需要) 并上传 `IRCServer-xxx.jar`

2. 运行 `java -jar IRCServer-xxx.jar` 

3. 观察运行目录 你应该能看到 `config.yml` 编辑它

````config.yml
# 数据库
database: jdbc:sqlite:data.db
ircServer: 
   host: 0.0.0.0
   port: 45020
   # 是否开启代理协议 (常用于通过 frp/cdn)
   proxyProtocol: false
   # 是否开启连接加密 会在握手完成后开始
   encryption: true
   # 是否开启签名验证 开启后需要在客户端配置系统的私钥
   signature: false
userLimit:
   # 是否允许单一用户同时在多个不同的设备上登陆
   allowMultiDeviceLogin: true
webServer:
   # API访问IP白名单
   ipWhiteList: [""]
   key: ""
   port: 45021
````