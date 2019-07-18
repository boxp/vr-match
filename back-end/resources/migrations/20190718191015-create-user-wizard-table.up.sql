-- -----------------------------------------------------
-- Table `vr_match`.`user_wizard`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `vr_match`.`user_wizard` (
`user_id` BIGINT(20) NOT NULL,
`wizard_step` INT NOT NULL,
`wizard_complete` INT NOT NULL,
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`user_id`),
UNIQUE INDEX `UNIQUE` (`user_id` ASC),
CONSTRAINT `user_wizard_user_id`
FOREIGN KEY (`user_id`)
REFERENCES `vr_match`.`user` (`id`)
ON DELETE NO ACTION
ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4;
