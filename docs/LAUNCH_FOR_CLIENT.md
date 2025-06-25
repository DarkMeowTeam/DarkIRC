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

编写:

````java
public class IRCDemo {

    public static String token = null;

    final IRCClientProvider client = new IRCClient(new Listener(), IRCClientOptions.builder()
            .host(System.getenv("IRC_SERVER")) // 服务器地址 您应该提前在环境变量中设置
            .port(Integer.parseInt(System.getenv("IRC_PORT"))) // 服务器端口
            .brand(new DataClientBrand("default", "Test", 1)) // 客户端标识
            // 如果服务端开启了签名(server.signature) 那么必须配置 clientKey
            // IRC_KEY_CLIENT 会在创建客户端时输出  无论是 默认/通过API/游戏中管理员创建 都会输出
            .clientKey(new IRCClientSignatureKey(System.getenv("IRC_KEY_CLIENT")))
            // 如果你希望更安全 请配置 remoteVerify
            // IRC_KEY_REMOTE 所需要的字符会在 服务端首次启动时打印在控制台中 '[时间] [main/INFO]: 生成密钥对成功' 这行下方的 Base64 字符
            .remoteVerify(new IRCClientRemoteVerify(System.getenv("IRC_KEY_REMOTE")))
            .build()
    );
    
    public static void main(String[] args) throws Throwable {
        client.connect(); // 客户端发动连接
        Thread.sleep(9999999); // 空等待 别较真 这只是一个示范代码
    }
    
    public class Listener extends IRCClientListenableSimple {
        
        @Override
        public void onReadyLogin(IRCClientProvider client) {
            client.login(System.getenv("IRC_USERNAME"), IRCDemo.token != null ? IRCDemo.token : System.getenv("IRC_PASSWORD"), false);
        }
        
        @Override
        public void onUpdateSession(String token) {
            // 登录成功后会触发
            // 这里你需要将 token 保存到一个安全的地方 用于后续替代密码登录
            IRCDemo.token = token;
        }

        @Override
        public void onUpdateUserInfo(IRCDataSelfSessionInfo info, boolean isFirstLogin) {
            // 服务端可能会二次下发用户信息 (比如说你从普通用户变成管理员)
            if (isFirstLogin) {
                System.out.println("登录成功(用户名:" + info.getName() + ")");
                // 发送消息
                client.sendMessageToPublic("Hello world!");
            }
        }

        @Override
        public void onMessagePublic(@NotNull IRCDataOtherSessionInfo sender, @NotNull String message) {
            // 收到其它用户的消息
            System.out.println("[" + sender.getInfo().getName() + "] " + message);
        }

        @Override
        public void onMessageSystem(@NotNull String message) {
            // 收到系统消息
            System.out.println("[系统消息] " + message);
        }

        @Override
        public void onDisconnect(EnumDisconnectType type, String reason, boolean logout) {
            if (logout) {
                // 被服务端踢出且 logout 为 true 时清除 token
                IRCDemo.token = null;
            }
            System.out.println(reason);
        }
    }
}
````
