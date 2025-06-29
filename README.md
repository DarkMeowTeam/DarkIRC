<div align=center>
<h1>DarkIRC</h1>
<h4>一个非常简单的聊天软件实现，为 Minecraft 具有入侵性 Mod 设计</h4>
</div>

![在HeyPixel服务器上作弊](/docs/CheatOnHeypixel2025.png)


## 功能
- 连接安全加密 与 双向签名验证 (防止未授权客户端接入 与 防止连接破解者服务器绕过授权(如果把 IRC 登录和授权绑在一起的话))
- 客户端登录检查 (通过配置以禁止低版本客户端登录)
- 用户名密码/token登录
- 公开聊天和用户名之间私有聊天
- 游戏内 ID 共享 (以允许制作同客户端用户不互相攻击)
- 游戏内 披风与皮肤 共享
- 同用户名多设备登录兼容 (允许一个人开多个客户端连接)
- 管理:游戏内使用 指令 管理
- 管理:通过 API 管理

## 构建

`DarkIRC` 使用 Gradle 完成构建, 因此你需要安装它. 你可以在[这里](https://gradle.org/install/)找到它 .

在开始之前, 你需要安装最低版本为 17 的 JDK.

1. 克隆这个存储库 `git clone https://github.com/DarkMeowTeam/DarkIRC/`.
2. `CD`进入这个目录.
3. 运行 `gradle IRCServer:installDist` 以构建服务端
4. 执行 `cd .\IRCServer\build\install\IRCServer\bin\` 和 `IRCServer` 以启动服务端


## 使用客户端

在 `build.gradle.kts` 添加以下内容 (如果你使用的是其它系统 请自行转换)

````kotlin
val ircVersion: String by project

repositories {
    maven("https://nekocurit.asia/repository/release")
}

dependencies {
    implementation("net.darkmeow:IRCClient-all:$ircVersion")
}
````

也可以自行构建

最小化示例代码: 

````java
public void connect() throws Throwable {
    final IRCClientProvider client = new IRCClient(new TestListener(), IRCClientOptions.builder()
            .host(System.getenv("IRC_SERVER")) // 服务器地址
            .port(Integer.parseInt(System.getenv("IRC_PORT"))) // 服务器端口
            .brand(new DataClientBrand("default", "Test", 1)) // 客户端标识
            .build()
    );
    client.connect();
    System.out.println("连接成功");
}

public class TestListener extends IRCClientListenableSimple {

    @Override
    public void onDisconnect(EnumDisconnectType type, String reason, boolean logout) {
        System.out.println(reason);
    }

    @Override
    public void onReadyLogin(IRCClientProvider client) {
        client.login(System.getenv("IRC_USERNAME"), System.getenv("IRC_PASSWORD"), false);
    }

    @Override
    public void onUpdateUserInfo(IRCDataSelfSessionInfo info, boolean isFirstLogin) {
        // 服务端可能会二次下发用户信息 (比如说你从普通用户变成管理员)
        if (isFirstLogin) { 
            System.out.println("登录成功(用户名:" + info.getName() + ")");
        }
    }
}
````

## 部署服务端

1. 在服务端上安装 JDK 17 (如果已安装该版本或更高版本 JDK 则不需要) 并上传 `IRCServer-xxx.jar`

2. 运行 `java -jar IRCServer-xxx.jar` 

3. 观察运行目录 你应该能看到 `config.yml` 编辑它

````config.yml
# 数据库
database:
  url: "jdbc:sqlite:data.db"
  driver: "org.sqlite.JDBC"
  user: ""
  password: ""
ircServer: 
  host: "0.0.0.0"
  port: 45020
  # 是否开启代理协议 (常用于通过 frp/cdn)
  proxyProtocol: false
  # 是否开启连接加密 会在握手完成后开始
  encryption: true
  # 是否开启签名验证 开启后需要在客户端配置系统的私钥 仅在 encryption 开启时工作
  signature: true
  # 数据压缩配置
  compression:
    state: true
    threshold: 256
userLimit:
  # 是否允许单一用户同时在多个不同的设备上登陆
  allowMultiDeviceLogin: true
webServer:
  # API访问IP白名单
  ipWhiteList: [""]
  key: ""
  port: 45021
````