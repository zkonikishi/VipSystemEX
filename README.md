# VipSystem

**VipSystemEX — 兼容 Paper / Folia 的 VIP 管理插件（Java 25+, MC 26.1+）**

切换语言： [English](README.en.md)

[![](https://www.jitpack.io/v/zkonikishi/VipSystemEX.svg)](https://jitpack.io/#zkonikishi/VipSystemEX)

## 简介
VipSystemEx 可以帮你自主管理服务器的 VIP，支持限时、永久、可切换的 VIP。本插件使用带索引的 SQLite 或 MySQL 进行数据存储，支持自定义开启/关闭连接池，兼容性和性能都比较均衡。

**当前版本：26.2.1 | 支持 Minecraft 26.1+（Paper/Folia）**

### 使用前提
- Java 25+
- Paper / Folia 26.1+
- 如需通过 Vault 对接权限插件，请安装 Vault 和对应的权限插件（例如 LuckPerms、PermissionsEx）
- 如果你不打算使用 Vault，也可以开启 `groupCommands.enable: true`，改用命令方式管理权限组

## 命令

| 命令 | 用途 | 权限 |
| --- | --- | --- |
|/vipsys me | 查看你的vip情况 | 无 |
|/vipsys changevip | 切换已开通的Vip | vipsys.changevip |
|/vipsys give [玩家名] [Vip组名] [时间] | 给予玩家Vip(时间格式为xdxhxmxs，x天 x小时 x分 x秒) | vipsys.give |
|/vipsys addtime [玩家名] [时间] | 为玩家现有限时Vip增加时间 | vipsys.give |
|/vipsys remove [玩家名] | 移除玩家的Vip | vipsys.remove |
|/vipsys list | 查看Vip列表 | vipsys.list |
|/vipsys look [玩家名] | 查看玩家Vip情况 | vipsys.look |
|/vipsys reload | 重载插件 | vipsys.reload |
|/vipsys customs | 查看自定义函数 | vipsys.customs |

![](http://www.zhanshi123.me/wp-content/uploads/2020/02/QQ截图20200215093116.png)


## 用法介绍
### 基础用法
#### 准备工作
1. 将插件放入服务器的 `plugins` 目录并启动一次服务器。
2. 如果你需要使用中文版，请打开 `/plugins/VipSystem/config.yml`，把 `lang: en` 改成 `lang: zh_CN`，然后执行 `/vipsys reload`。
3. 创建好对应的 VIP 组，例如 `vip`、`svip`。
4. 如果你使用 Vault + 权限插件模式，请确保 Vault 和权限插件都已正常加载。
5. 如果你不想依赖 Vault，可以把 `groupCommands.enable` 设为 `true`，并在配置中填写对应的命令模板。
6. 如果你之前使用了旧版本的 VipSystem，可以直接替换为新版本，插件会自动进行配置文件和数据结构更新。

补充说明：插件会根据权限插件返回默认组，如果你没有自定义 `defaultGroup`，通常不需要额外配置默认组。
#### 发放Vip
设玩家名为 Test 需要发放给Test 30天的vip权限组  
则输入命令```/vipsys give Test vip 30d```  
玩家可以使用```/vipsys me```来查看自己的vip情况  
若需要移除vip，只需管理员输入```/vipsys remove Test```即可移除  
#### 使用占位符
如果你需要使用VipSystem提供的占位符，你需要安装PlaceholderAPI插件  
占位符映射如下  

| 占位符 | 用途 |
| --- | --- | 
| %vipsystem_vip% | vip组名 |
| %vipsystem_expire% | 到期时间 |
| %vipsystem_previous% | 开通之前的组名 |
| %vipsystem_left% | 剩余天数 |
| %vipsystem_left_formatted% | 剩余时间（格式化，如 2d 3h 15m 30s） |

### 进阶用法
#### 语言设置
插件内置了英语和中文，你可以将配置文件中的```lang```项目的值改为```zh_CN```或```en```来使用插件内置的两种语言  
你也可以自定义语言，将```lang```的值改为语言名之后，重载插件，插件会在```messages```目录下多生成一个以英文为模板的语言文件，你可以自主修改并重载生效语言文件
#### UUID模式
如果你的服务器是正版服务器  
建议你在配置文件中启用UUID模式```uuid: true```
#### 默认组设置
插件会根据权限组插件来决定vip到期后返回的权限组  
如果你需要修改默认组，请在配置文件中设置 ```defaultGroup: builder```  
将返回的默认组改为builder或其他
#### 返回开通前的上一个组
如果你想在玩家Vip到期时返回玩家开通Vip前的权限组  
你可以设置```previousGroup: true```来实现这个功能  
*注意: 本功能和默认组设置不能同时使用*
#### 数据库
插件默认使用HikariCP连接池连接SQLite数据库，如果没有特殊需要，你无需改动该部分配置文件  
如果你的服务端无法使用默认配置文件启动，并且报错中有hikarcp字样，请修改```usePool```的值为```false```  
如果你需要使用MySQL，你可以设置```useMySQL```的值为```true```并在下方配置数据库地址等信息
#### 全局Vip
如果你使用GroupManager作为权限组插件，无特殊需要，不需要更改此部分配置  
如果你使用PermissionsEx作为权限组插件，并且默认配置下vip无法生效到全部世界，请将```isGlobal```的值设置为```false```，如果仍无法生效到全部世界，请在```worlds```下手动添加你所有的世界名，例如
```
worlds:
  - world
  - world_nether
  - world_the_end
```
#### 自定义到期时间格式
如果需要更改日期格式，可以参考[https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html](https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)的占位符进行修改
#### 自定义命令
你可以自定义vip开通/到期时的命令，下面是默认的配置文件
```
customCommands:
  vip:
    activate:
      - 'say {0} has activated vip!'
      - 'give {0} minecraft:diamond'
    expire:
      - 'say {0} is no longer a vip'
  svip:
    activate:
      - 'say {0} has activated svip!'
      - 'give {0} minecraft:diamond'
    expire:
      - 'say {0} is no longer a svip'
```
意思是，在vip开通时，执行下方命令，发送消息并给玩家一个钻石
```
say {0} has activated vip!
give {0} minecraft:diamond
```
在vip到期时，执行下方命令，发送消息
```
say {0} is no longer a vip
```
svip组同理，如果你有更多的vip种类，可以按照上方格式自行添加  
如果不需要使用该功能，可以直接整项删除
## 自定义函数
插件支持你自己编辑自定义函数，来完成一些自动的定时操作  
比如你可以通过插件实现临时权限的功能  
![https://www.zhanshi123.me/wp-content/uploads/2020/02/1.png](https://www.zhanshi123.me/wp-content/uploads/2020/02/1.png)  
具体编辑的方法可以查看 [https://www.zhanshi123.me/?p=320](https://www.zhanshi123.me/?p=320)  
上图的功能你可以直接前往[https://www.mcbbs.net/thread-959456-1-1.html](https://www.mcbbs.net/thread-959456-1-1.html)进行下载
## 插件下载
该插件为免费插件，推荐直接从仓库构建或从发布版本下载。

### 构建方式
你可以 clone / fork 该项目并自行构建。进入项目目录后执行：
```bash
mvn clean package
```
构建完成后，目标文件会生成在 `target` 目录下。

### 发布版本
如果你想直接下载正式发布包，可以前往 GitHub Release 页面：
[VipSystemEx v26.2.1](https://github.com/zkonikishi/VipSystemEX/releases/tag/v26.2.1)

你也可以直接使用对应版本的 jar 文件。

### 赞助支持
如果你没有自行构建的能力，你也可以联系作者赞助，由作者提供构建后的文件。所有支付的款项都是对作者开发极大的支持！

### Vault 支持版本下载
如果你的服务器需要安装 Vault 作为权限桥接插件，可以使用下面这个可用版本：
[Vault-1.7.4.jar](https://www.zrips.net/cmivault/download.php?file=Vault-1.7.4.jar)

## 新版构建要求（MC 26.1+）
- 运行环境：Paper/Folia（支持 `folia-supported: true`）
- Java：25+
- Minecraft：26.1 及以上版本
- 构建依赖：`io.papermc.paper:paper-api:${paper.api.version}`

如果你使用的是不同 build 号的 26.1.x，请在 `pom.xml` 中修改 `paper.api.version` 属性（例如 `26.1.2.build.xx-stable`）。如果你的服务端是 Folia，建议保持 `folia-supported: true`。
## API
使用本插件作为前置(以Maven为例)  
```
<repository>
  <id>soldier-repo</id>
  <url>https://repo.zhanshi123.me/repository/maven-public/</url>
</repository>
```
```
<dependency>
  <groupId>me.zhanshi123</groupId>
  <artifactId>VipSystemEx</artifactId>
  <version>26.2.1</version>
  <scope>provided</scope>
</dependency>
```
你可以通过```VipSystemAPI.getInstance()```来获取```VipSystemAPI```的实例  
更多内容待补充
