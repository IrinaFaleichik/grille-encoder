CREATE TABLE IF NOT EXISTS user (
                       id CHAR UNIQUE NOT NULL PRIMARY KEY,
                       name CHAR NOT NULL,
                       email CHAR UNIQUE NOT NULL
);