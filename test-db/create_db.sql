CREATE DATABASE test;
use mysql
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `test`.* TO 'test'@'%' IDENTIFIED BY 'test';
GRANT ALL PRIVILEGES ON `test`.* TO 'test_admin'@'%' IDENTIFIED BY 'admin';
DELETE FROM mysql.user WHERE Host='localhost' AND User='';
GRANT SELECT ON `mysql`.`proc` TO 'test_admin'@'%';
GRANT SELECT ON `mysql`.`proc` TO 'test'@'%';
FLUSH PRIVILEGES;
