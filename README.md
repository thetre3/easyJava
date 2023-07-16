# easyJava
This is a simple program that reads the structure of an SQL table (table name, column name, column properties, uniqueness, index properties) and dynamically builds a Java Spring MVC framework program. It supports dynamically defining the project path, file name, and the template class to be included in a custom-defined folder. If you need to add a custom template, you need to include the following lines in the template:

First line:
@ (Package name) @

Second line:
@ (Custom template imported from the same project) @
这是一个简单的程序关于读取sql表结构（读取表名列名列属性，唯一性索引属性）动态构建javaSpringmvc框架的程序，支持动态定义写入项目路径，动态定义文件名称以及动态加入定义文件夹的模板类，如果需要添加自定义模板需要在模板第一行以
@（包名称）@
第二行
@（同项目下导入的自定义模板）@
加入模板中
