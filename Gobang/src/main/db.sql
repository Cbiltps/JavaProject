create database if not exists gobang charset utf8mb4;

use gobang;

drop table if exists user;

create table user (
    userId int primary key auto_increment,
    username varchar(50) unique,
    password varchar(50),
    score int, -- 天梯积分
    totalCount int, -- 比赛总场数
    winCount int -- 获胜场数
);

insert into user values(null, 'zhangsan', '123', 1000, 0, 0);
insert into user values(null, 'lisi', '123', 1000, 0, 0);
insert into user values(null, 'wangwu', '123', 1000, 0, 0);