USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_update_test` $$
CREATE PROCEDURE `test`.`sp_update_test`
(
    in_bool				 BOOLEAN
)
BEGIN
    UPDATE test set
	   bool = in_bool;
END$$
DELIMITER :
