# VipSystemEx

[中文](README.md)

[![](https://www.jitpack.io/v/zkonikishi/VipSystemEX.svg)](https://jitpack.io/#zkonikishi/VipSystemEX)

## Description
VipSystemEx can do you a favor to manage the Vip of your server. The plugin supports permanent, timed, switchable vip group.  
VipSystemEx use cached MySQL or SQLite with indexes as database, supporting enable/disable connection pool(default on), which ensures the compatibility and the efficiency.

**Current Version: 26.2.1 | Supports Minecraft 26.1+ (Paper/Folia)**

## Commands

| Commands | Description | Permission |
| :-: | :-: | :-:|
|/vipsys me | Get your vip details | Null |
|/vipsys changevip | Change activated vip group | vipsys.changevip |
|/vipsys give [Player] [Group] [Time] | Give vip(Time format: xdxhxmxs, x days x hours x minutes x seconds) | vipsys.give |
|/vipsys addtime [Player] [Time] | Add time to a player's existing timed vip | vipsys.give |
|/vipsys remove [Player] | Remove vip | vipsys.remove |
|/vipsys list | Show the list of vip | vipsys.list |
|/vipsys look [Player] | Get the vip detail of a specific player | vipsys.look |
|/vipsys reload | Reload the plugin | vipsys.reload |
|/vipsys customs | List custom functions | vipsys.customs |

## Instructions
### Fundamental Usage
#### Preparations
Install the plugin to server,and restart.    
You don't need to configure the default group.The plugin will set the default group automatically according to your permission plugin.      
Create a vip group named as vip for example.  
Note: If you have installed previous version of VipSystem, the data will be imported from the old version.  
#### Give Vip
Assuming that the player to be given vip is Test.You need to give Test a 30-day vip group.    
Use the command```/vipsys give Test vip 30d```  
The player can use ```/vipsys me``` to check himself vip detail.    
To remove vip, use```/vipsys remove Test```.  
#### Placeholder
If you need to use placeholder provided by VipSystemEx, you need to install plugin PlaceholderAPI.    
Use command```/papi ecloud download vipsystem```to load extension.  
Then you can use the placeholders.  
Below are the mappings.  

| Placeholder | Description |
| :-: | :-: | 
| %vipsystem_vip% | vip group name |
| %vipsystem_expire% | vip expire date |
| %vipsystem_previous% | group name before activating vip |
| %vipsystem_left% | days left (decimal) |
| %vipsystem_left_formatted% | time left in human-readable format (e.g. 2d 3h 15m 30s) |

### Advanced Usage
#### Language
The plugin has two languages,Chinese & English, built-in.You can set```lang```to```zh_CN```or```en```to switch languages.    
You can also customize your language，set```lang```to your own language,reload the plugin,and the plugin will generate a new file named as what you set in folder```messages```copying from en.yml. You can edit it as you wish.  
#### UUID Mode
If you enabled online mode  
It is recommended to apply ```uuid: true```
#### Default Group
The plugin will determine the group to return according to the permission plugin.  
If you want to override the default group, you can set```defaultGroup: builder```    
To set the default group to builder or something else.
#### Database
The plugin use HikariCP as database to connect SQLite database as default.If you don't have special needs, you can ignore the section.    
If the plugin goes wrong when enabling, you need to set```usePool``` to ```false```  
If you need to use MySQL,you can set```useMySQL``` to ```true``` and configure the jdbc address below.
#### Global Vip
If you are using GroupManager, you need not to modify the section without special intention.   
If you are using PermissionsEx, in case the vip cannot be applied to all the worlds, you need to set```isGlobal``` to ```false```  
If the problem still occurs, you need to add all you worlds in```worlds```  
```
worlds:
  - world
  - world_nether
  - world_the_end
```
#### Custom Command
You can define the command to be executed when activating/disabling the vip.
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
When activating vip, the commands below will be executed, send messages and give a diamond.
```
say {0} has activated vip!
give {0} minecraft:diamond
```
When vip expires, the commands below will be executed, send messages.
```
say {0} is no longer a vip
```
If you need to add more groups, it is the same method.  
If you don't need the function, you can delete it.
## API
You can use```VipSystemAPI.getInstance()``` to get the instance of ```VipSystemAPI```  
Usage with Maven:  
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

## Build Requirements (MC 26.1+)
- Runtime: Paper/Folia (`folia-supported: true`)
- Java: 25+
- Minecraft: 26.1 and above
- API dependency: `io.papermc.paper:paper-api:${paper.api.version}`

If your server uses a different 26.1.x build, update `paper.api.version` in `pom.xml` (for example `26.1.2.build.xx-stable`).