<div align=center>
<h1>DarkIRC</h1>
<h4>一个非常简单的聊天软件实现，为 Minecraft 具有入侵性 Mod 设计</h4>
</div>

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
1. 克隆这个存储库 `git clone https://github.com/DarkMeowTeam/DarkIRC/`.
2. `CD`进入这个目录.
3. 使用你的 IDE 打开
4. 构建服务端 `gradle IRCServer:build` 构建客户端 `gradle IRCClient:build`