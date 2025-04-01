DROP USER IF EXISTS 'stms_user'@'localhost';

CREATE USER IF NOT EXISTS 'stms_user'@'localhost' IDENTIFIED BY 'password';

CREATE DATABASE IF NOT EXISTS stms
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON stms.* TO 'stms_user'@'localhost';
FLUSH PRIVILEGES;
