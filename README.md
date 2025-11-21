# 项目
###### 项目说明
这是一个使用javafx编写的mysql客户端，支持基本的连接管理，分组管理、数据操作、操作命令查看、表&视图&事件&函数&过程管理、导入导出、数据传输、查询等功能，还支持暗色主题、系统主题跟随等能力。  
后续更新移步已至  
https://gitee.com/oyzh1994/easyshell

###### 启动入口
cn.oyzh.easymysql.EasyMysqlBootstrap 
注意，如果要运行项目，最好切换到最新分支，不然可能启动不了，主分支master代码是定期合并进去  
ide建议idea社区版或者专业版

###### 依赖说明
1. base工程，可选手动安装，也可使用中心仓库稳定版本  
 https://gitee.com/oyzh1994/base  
2. fx-base工程，可选手动安装，也可使用中心仓库稳定版本  
 https://gitee.com/oyzh1994/fx-base  
3. jdk版本要求21，推荐24  
注意，如果是linux的arm平台，建议使用aws的jdk，其他jdk可能缺失hsdis类库，其他情况下优先使用openjdk  
awsjdk21 https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html  
awsjdk24 https://docs.aws.amazon.com/corretto/latest/corretto-24-ug/downloads-list.html  
openjdk https://jdk.java.net/archive/

###### 结构说明 
docker -> docker配置文件  
docs -> 文档相关资源  
package -> 打包相关配置  
resource -> 项目相关资源文件  
src -> 项目相关代码

# Maven
###### 打包
mvn -X clean package -DskipTests

# 程序相关截图
###### 截图1
![img1.png](resource/screenshot/mysql1.png)
###### 截图2
![img2.png](resource/screenshot/mysql2.png)
###### 截图3
![img3.png](resource/screenshot/mysql3.png)
###### 截图4
![img4.png](resource/screenshot/mysql4.png)
###### 截图5
![img5.png](resource/screenshot/mysql5.png)
###### 截图6
![img6.png](resource/screenshot/mysql6.png)
