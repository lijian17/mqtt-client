Android APP必备高级功能，消息推送之MQTT
原文地址：http://blog.csdn.net/qq_17250009/article/details/52774472

---MQTT服务器搭建-----------------------------------------------------------
点击这里[http://activemq.apache.org/apollo/download.html]，下载Apollo服务器，解压后安装。
命令行进入安装目录bin目录下（例：E:>cd E:\MQTT\apache-apollo-1.7.1\bin）。
输入apollo create XXX（xxx为创建的服务器实例名称，例：apollo create mybroker），之后会在bin目录下创建名称为XXX的文件夹。XXX文件夹下etc\apollo.xml文件下是配置服务器信息的文件。etc\users.properties文件包含连接MQTT服务器时用到的用户名和密码，默认为admin=password，即账号为admin，密码为password，可自行更改。
进入XXX/bin目录，输入apollo-broker.cmd run开启服务器，看到如下界面代表搭建完成
success

之后在浏览器输入http://127.0.0.1:61680/，查看是否安装成功。