USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_add_test` $$
CREATE PROCEDURE `test`.`sp_add_test`
(
    in_bool     BOOLEAN,
    in_string   VARCHAR(100),
    in_ti       TINYINT,
    in_i        INT,
    in_time     DATETIME
)
BEGIN
    INSERT INTO test
        (
	   bool,
           string,
	   ti,
	   i,
	   time
        )
        VALUES
        (
           in_bool,
	   in_string,
           in_ti,
           in_i,
           in_time
        );
END$$
DELIMITER :
