USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_get_count` $$
CREATE PROCEDURE `test`.`sp_get_count`
(
)
BEGIN
    SELECT count(*) from test;
END$$
DELIMITER :
