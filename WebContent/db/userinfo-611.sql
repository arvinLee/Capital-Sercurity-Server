drop table T_USERINFO cascade constraints;
drop sequence T_USERINFO_SEQ;

--用户信息表
create table T_USERINFO
(
  id       				NUMBER(20) not null,
  fullname 				VARCHAR2(100),
  islock         		NUMBER(1),
  secretlevel   		VARCHAR2(100),
  username				VARCHAR2(100)
);
alter table T_USERINFO
  add constraint T_USERINFO_PKEY primary key (id);

  
-- Create sequence 
create sequence T_USERINFO_SEQ
minvalue 1
maxvalue 9999999999999999999999999
start with 1000
increment by 1
cache 20
cycle;

insert into t_userinfo(id,fullname,islock,secretlevel,username) values(0,'superuser',0,'秘密','system')


