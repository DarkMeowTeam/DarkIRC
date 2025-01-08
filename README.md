<div align=center>
<h1>DarkIRC</h1>
<h4>一个非常简单的聊天软件实现，为 Minecraft 具有入侵性 Mod 设计</h4>
</div>

![在HeyPixel服务器上作弊](/docs/CheatOnHeypixel2025.png)


## 功能
- 协议加密
- 客户端登录检查 (通过配置以禁止低版本客户端登录)
- 用户名密码登录
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

1. 在服务端上安装 JDK 22 并上传 `IRCServer-xxx.jar`

2. 运行 `java -jar IRCServer-xxx.jar` 

3. 观察运行目录 你应该能看到 `config.yml` 编辑它

````config.yml
# 数据库
database: jdbc:sqlite:data.db
# 连接密钥 需要确保客户端和此处一样 不同的密钥无法完成握手
key: publicIRCTest123
# 监听端口
port: 8080
# 是否启用 Proxy Protocol (部分内网穿透支持, 可以通过 Proxy Protocol 获取客户端真实 IP)
proxyProtocol: false
# 用户闲置
userLimit: 
   # 是否允许多设备同时登录
   # 关闭不会影响同设备多连接
   allowMultiDeviceLogin: false
````