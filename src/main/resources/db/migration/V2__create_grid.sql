CREATE TABLE IF NOT EXISTS grid (
                                    id INT UNSIGNED UNIQUE AUTO_INCREMENT PRIMARY KEY,
                                    title CHAR NOT NULL,
                                    user_id INT NOT NULL,
                                    FOREIGN KEY (user_id) REFERENCES app_user(id) NOT NULL,
                                    CONSTRAINT title_unique_per_user UNIQUE (title, user_id)
);