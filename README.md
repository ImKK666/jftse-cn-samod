# JFTSE-CN-SAMOD - Java Fantasy Tennis Server Emulator CN SAmod
## 拍拍部落服务端模拟器
![based](https://img.shields.io/badge/基于-Stefan_Stokic_--_JFTSE-orange)
![development](https://img.shields.io/badge/当前状态-正在开发-orange)
![update](https://img.shields.io/static/v1?label=%E6%9C%80%E8%BF%91%E6%9B%B4%E6%96%B0&message=2021-05-21&color=green)

## [更新日志](https://gitee.com/sakuyaares/jftse-cn-samod/blob/master/CHANGELOG.md)

 ↑ 点击查看更新日志

---
### 重要说明

某些意图不明的人士频繁通过DDOS攻击针对我的朋友和我。

在 2021-05-06 的更新日志里有提到我为此做的一些努力，但是这对一个业余时间都很少的人来说也不是一件容易的事。

攻击者还特别喜欢不懂装懂，喜欢在自己的圈子里把自己塑造成无所不能的造物主。
通过踩一捧一的演讲尝试将玩家洗脑成为自己的信徒，长期自我感觉良好、自我非常感动。

我想说的是，DDOS虽然破坏力大，难防，但是真的不是什么高级高尚的攻击手段。

用DDOS的人，在我们这些正儿八经创造GDP的行业从业者眼里，真的是非常下贱的。

建议不要以此作为炫耀的资本，不然会有越来越多的人知道你是个下贱的人。

可能是因为我的朋友断了这些人的财路，因此遭受报复，不让可怜的玩家变成韭菜，就要饿死拿镰刀的人。

俗话说断人财路如杀人父母，但如果是不义之财，那你妈还是死了更好。

---
## 目录
* [简介](#简介)
* [环境需求](#环境需求)
* [部署](#部署)
* [运行](#运行)
* [Copyright](#copyright)

---
## 简介

**JFTSE-CN-SAMOD** 是一个开源的拍拍部落服务端模拟器。

基于 **[Stefan Stokic - JFTSE](https://github.com/sstokic-tgm/JFTSE)** 和 **[Andrexo9 - JFTSECN](https://github.com/Andrexo9/JFTSECN)** 修改。

这个仓库是本人根据玩家友人的需求进行的修改，如果您希望为该项目添砖加瓦，请访问上述源仓库，与这些大佬开发者联系。

本人精力有限，恕不提供技术支持，也请您不要抱有任何期望，请见谅。

---
## 环境需求

请自行下载以下组件，Windows、Linux均可。部署方法请参照其官方文档。

| 名称 | 版本 |
|------|---------|
| JDK / OpenJDK | 15 / 15 |
| Maven | ≥ 3.6.3 |
| MySQL | 8.0 |  

<br />

## 其他建议

如果您希望它稳定运行，建议在Linux平台部署，并配合supervisor等工具对服务端进程进行管理。

如果您对MySql不熟悉，请您自行学习MySql的管理及数据操作的基础知识。

如果您希望自行修改源代码，建议使用IDEA。

<br />

---

## 安装

### 获取源代码
```
你可以使用git客户端  
git clone -b master https://gitee.com/sakuyaares/jftse-cn-samod.git
```
```
或者直接下载源码解包
```

### 编译

```
cd <源码目录>
mvn clean install
```
jar包将生成在target目录下

<br />

---
## 运行

<br />

### 首次运行

<br />

首次运行前执行 **sql/create/** 目录中的 **_create_fantasytennis.sql_**

运行服务端jar包
```
cd target
java -jar ft_server_emulator.jar
```
首次运行时服务端会自行进行数据初始化，需要花费一点时间，直到显示以下内容说明服务端初始化完成
> Emulator successfully started!

开始游玩前还需要添加服务器定义并创建账号:
1. 将 **sql/insert/** 中的 **__gameservertype.sql__** 导入数据库（必须先做这一步）
2. 打开 **sql/insert/** 中的 **__gameserver.sql__** ，修改其中的IP地址为服务器的公网地址，然后导入数据库
3. 在 Account 表中新增用户帐号，例如:   
```INSERT INTO `fantasytenniscn`.`Account`(`ap`, `gameMaster`, `password`, `status`, `username`) VALUES (0, b'1', '<BCrypt密文>', 0, 'test');```
注意密码字段需要写入BCrypt密文(ROUND=10)，您可以通过一些在线的BCrypt密文工具生成。
<br />

### 运行服务端

直接运行:
```
java -jar ft_server_emulator.jar
```

后台运行：
```
nohup java -jar ft_server_emulator &
```

在shell中运行服务端只供临时游戏使用  
退出shell时服务端进程也将退出  
如果需要长时间游玩，建议通过supervisor管理服务端进程

---
## Copyright

License: GPL 3.0    
Read file [LICENSE](LICENSE).
