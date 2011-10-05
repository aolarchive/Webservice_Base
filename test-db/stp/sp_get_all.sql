USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_get_all` $$
CREATE PROCEDURE `test`.`sp_get_all`
(
)
BEGIN
    SELECT * from test;
END$$
DELIMITER :
