CREATE TABLE IF NOT EXISTS `bicycle_binding_rs` (
  `userId` bigint(20) NOT NULL,
  `idCard` char(18) NOT NULL,
  `cardNum` char(9) NOT NULL,
  `bindingTime` bigint(20) NOT NULL,
  UNIQUE KEY `userId_cardNum_dekeys` (`userId`,`cardNum`) USING BTREE
) 
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT
;