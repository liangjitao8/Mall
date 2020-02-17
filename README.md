# Mall
商城项目，仅做找工作展示用

#项目介绍   
将以前学习的Spring+SpringMVC+Mybatis的商城项目转成SpringBoot.
系统基本实现的网上购物浏览商品的流程，实现了管理员对店铺、商品类别以及区域的增删改查操作，店家注册、管理旗下店铺和商品，还有前端页面的展示。

#技术栈    
后端：SpringBoot+Mybatis+MySQL+Redis
前端：html+css+js+jQuery+BootStrap

#快速部署   
1.sql文件导入数据库   
2.将打包的资源图片（ProjectO2O）解压放在D盘（路径修改的位置在web模块config包下的SourceConfiguration中）   
3.在application.properties中修改sql的端口号和redis的配置信息   
4.配置好后等maven下载完需要的jar包就可以启动项目了（web模块下的O2oApplication运行main方法）   
5.localhost:8080/o2o/frontend/index 浏览前端展示系统    
  localhost:8080/o2o/shop/ownerlogin 进入店家管理界面（仅完成店铺，商品，类别管理功能） 账号：shop 密码:123   
  localhost:8080/o2o/superadmin/main 进入后台管理界面   

