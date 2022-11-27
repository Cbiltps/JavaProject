-- 编写建库建表的 sql

create database if not exists blog_system;

use blog_system;

-- 创建一个博客表.
drop table if exists blog;
create table blog (
    blogId int primary key auto_increment,
    title varchar(1024),
    content mediumtext,
    userId int,         -- 文章作者的 id
    postTime datetime   -- 发布时间
);

-- 给博客表中插入点数据, 方便测试.
insert into blog values(null, '这是第一篇博客', '从今天开始, 我要认真学 Java', 1, now());
insert into blog values(null, '这是第二篇博客', '从昨天开始, 我要认真学 Java', 1, now());
insert into blog values(null, '这是第三篇博客', '从前天开始, 我要认真学 Java', 1, now());
insert into blog values(null, '这是第一篇博客', '从今天开始, 我要认真学 C++', 2, now());
insert into blog values(null, '这是第二篇博客', '从昨天开始, 我要认真学 C++', 2, now());
insert into blog values(null, '这是第三篇博客', '# 一级标题\n ### 三级标题\n > 这是引用内容', 2, now());

-- 创建一个用户表
drop table if exists user;
create table user (
    userId int primary key auto_increment,
    username varchar(128) unique,    -- 后续会使用用户名进行登录, 一般用于登录的用户名都是不能重复的.
    password varchar(128)
);

insert into user values(null, 'zhangsan', '123');
insert into user values(null, 'lisi', '123');