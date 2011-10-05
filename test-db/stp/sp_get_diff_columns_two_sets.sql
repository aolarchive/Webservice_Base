USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_get_diff_columns_two_sets` $$
CREATE PROCEDURE `test`.`sp_get_diff_columns_two_sets`
(
)
BEGIN
    SELECT string from test;
    SELECT bool from test;
END$$
DELIMITER :

