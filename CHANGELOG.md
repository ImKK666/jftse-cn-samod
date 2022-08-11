# Change Log
基于 **[Stefan Stokic - JFTSE](https://github.com/sstokic-tgm/JFTSE)** 和 **[Andrexo9 - JFTSECN](https://github.com/Andrexo9/JFTSECN)** 修改。
本人业余时间较少，请不要抱有任何期望。

### 2021-05-06
#### 服务端
* 商城现在可以正常区分Mint和Gold。
  * 以前无论物品花费的是Mint还是Gold，都会按Gold算，不知道是原版就打算这么干还是懒得弄。
* 又修复了一些愚蠢的问题。

### 2021-05-06
#### 服务端
* 暂时禁用了一些防君子不防小人的措施。
* 修复一些愚蠢的问题。
#### 通信转接器 客户端/服务端
* 为了躲避某些人的持续性DDOS攻击，开发了新的通信转接器。
  * 在此不妨介绍一下原理。
  * 众所周知Cloudflare在提供免费CDN服务的同时，
    也免费提供不限量DDOS防护，
    但只支持HTTP/HTTPS协议。
  * 游戏客户端与服务端实际上直接使用了私有的数据包格式，
    并非HTTP协议规范，
    因此无法直接使用Cloudflare提供的免费防护服务。
  * 这个通信转接器的作用，
    就是把游戏客户端和服务端的连接通过WebSocket协议转接：
    * 发送端4层转7层
    * 接收端7层转4层，
  * 使其可以利用Cloudflare的免费DDOS防御。
* 然而这是个麻烦事，对于一个连业余时间都很少的人来说并没有那么容易。
  * 某些把自己当做造物主一样自我感觉良好且长期沉浸在自我感动中的人，
    宣称这玩意只是改了个配置文件，对其信徒进行踩一捧一式洗脑。
  * 那么为什么配置文件里的服务器地址写着127.0.0.1也能登录呢？
  * 我没说之前你可能搞不明白吧？
  * 不懂装懂的样子真的很丢人，建议好自为之。
* 通信转接器目前仅实现了基本功能，尚不稳定，且Cloudflare需要氪金优化，
  目前容易发生高延迟、掉线等情况，基本处于没法正常玩的状态，后续有必要再完善。
* 目前通信转接器暂不开源，后续可能为其专门新建一个仓库。

#### 游戏性
* 更新了狩猎模式物品掉落清单。

### 2021-04-22
#### 服务端
* 修复创建角色时昵称中的汉字会消失的问题。
* 添加角色昵称检查，不允许在昵称中使用不安全的字符，避免客户端的昵称合法性检查被恶意绕过。
  * 不允许使用的字符包括 . " ' [ ] { } $ % ^ & * ( ) ! @ ` # \ /

### 2021-04-21
#### 服务端
* 修复在商店购买物品时实际结算金额可能会计算错误的问题。

### 2021-04-20
#### 服务端
* 修复更新狩猎场版本后出现的国服客户端聊天文本异常的问题。
* 出于安全考虑，用户密码将通过BCrypt方式保存和校验，相应的，在数据库中手动创建用户时应写入BCrypt密文（Round=10）。

### 2021-04-19
#### 服务端
* 合并了 **[Stefan Stokic - JFTSE](https://github.com/sstokic-tgm/JFTSE)** 的master分支，对战支持狩猎模式。
  * *此外更新内容较多，具体请自行查看该仓库的提交记录及更新日志*。 
* 获取游戏服务器时，忽略对战通信服务。
* 出于兼容性原因，暂时屏蔽反作弊功能。
  
#### 游戏性相关
* 快速创建狩猎模式房间时，默认开启球拍技能和快捷栏。

---
### 2021-04-18
由此版本起，Change Log以中文书写。

#### 服务端
* 服务端启动时会将所有状态为“已登录”的账号重置为“未登录”，避免炸服后账号卡在已登录状态。
---
### 2020-07-25
#### Server

* Refactored Server.
* Optimized Server.
* Different project structure in whole.
* Using Spirng Boot.

#### Game-related

* Everything for home is now finished. Furniture Count, bonuses, house level working.
-- Bonuses applied to single-play matches.

* Shop fixes:
-- You can buy sets now, multiple items at once can be buyed from now on.
-- You can buy sets now, which adds you a character aswell, those items are transferred to that new character.
-- You can buy chars and create them without restarting the client anymore.
-- If enough housing points, you can buy the house with a greater level aswell and have that way more space. (and level the house ofcourse)

* Singleplay fixes:
-- Battle-mode now has correct HP player handling.
-- Using quick-slot items reduces the spell count on the server-side.

* Inventory fixes:
-- You can equip cloths and quick slot items and they are saved.
-- If a quick slot is used up then it gets removed rigtly.

* Login fixes:
-- Logging out resets your login state correctly.
-- If crashing, the disconnect is handled correctly by the server, that, your login state doesn't need to reseted anymore. (Ofcourse may testes on different User's PC give different result, you can report the case.)

* Restrictions
-- Everything related to rooms, creating them, joining and starting game is not implemented under the main branches anymore and will come in future with proper fixes and partially working matchplay.

### 2020-01-29

* Game server structure fixed.
* Minor fixed for room list.

### 2020-01-20

* Added time limited item from inventory.
* Fixed home place item.

### 2020-01-10

* Updated `hibernate-validator` dependency from `6.0.13` to `6.1.0`.
* Updated for match play mechanics.

### 2020-01-07

* Special characters are now allowed inside the chat room.

### 2019-12-16

* Fixed disconnect bug in sessions.
* Fixed lobby user list.
* Fixed login bugs when having multiple characters.
* Added deuce/advantage gameplay.
* Added quick equip item mechanics.

### 2019-12-12

* Added gold boost for new characters.
* Fixed bug battle mode and item reward.

### 2019-12-11

* Fixed some health bar bug.

### 2019-12-09

* Initial version base on [AnCoFT](https://github.com/AnCoFT/AnCoFT).

