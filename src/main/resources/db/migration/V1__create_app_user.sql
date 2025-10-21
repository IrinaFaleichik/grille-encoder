CREATE TABLE IF NOT EXISTS app_user (
                       id INT UNSIGNED UNIQUE AUTO_INCREMENT PRIMARY KEY,
                       username CHAR NOT NULL,
                       email CHAR UNIQUE NOT NULL
);