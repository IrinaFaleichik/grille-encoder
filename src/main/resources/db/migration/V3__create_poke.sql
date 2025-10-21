CREATE TABLE IF NOT EXISTS poke (
                                    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                    x_start INT NOT NULL,
                                    y_start INT NOT NULL,
                                    x_end INT NOT NULL,
                                    y_end INT NOT NULL,
                                    grid_id INT NOT NULL,
                                    FOREIGN KEY (grid_id) REFERENCES grid(id) ON DELETE CASCADE
    );
