USE test;
DELIMITER $$

DROP PROCEDURE IF EXISTS `test`.`sp_math_subtract` $$
CREATE PROCEDURE `test`.`sp_math_subtract`
(
    in_i1        INT,
    in_i2        INT,

    OUT subed INT
)
BEGIN
    SET subed = (in_i1 - in_i2);
END$$
DELIMITER :
