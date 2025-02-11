DROP USER IF EXISTS 'stms_user'@'localhost';

CREATE USER IF NOT EXISTS 'stms_user'@'localhost' IDENTIFIED BY 'password';

CREATE DATABASE IF NOT EXISTS stms;

GRANT ALL PRIVILEGES ON stms.* TO 'stms_user'@'localhost';
FLUSH PRIVILEGES;