CREATE TABLE IF NOT EXISTS poke (
                                    id INT UNSIGNED UNIQUE AUTO_INCREMENT,
                                    x1 INT NOT NULL,
                                    y1 INT NOT NULL,
                                    x2 INT NOT NULL,
                                    y2 INT NOT NULL,
                                    grid_id INT NOT NULL,
                                    FOREIGN KEY (grid_id) REFERENCES grid(id)
    )
