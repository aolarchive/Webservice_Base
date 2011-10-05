USE test;

DROP TABLE IF EXISTS test;
CREATE TABLE test
(
    bool                        BOOLEAN NOT NULL,
    string 			VARCHAR(100) NOT NULL,
    ti				TINYINT NOT NULL,
    time			DATETIME NOT NULL,
    i				INT NOT NULL
) ENGINE=InnoDB;

