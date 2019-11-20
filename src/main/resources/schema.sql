
CREATE TABLE IF NOT EXISTS `tc_user_roles` (

  `roleId` int(11) NOT NULL auto_increment,   
  `delete_date` varchar(255)  NULL,
  `name` varchar(255)  NULL,
  `permissions` LONGTEXT  NULL,
  `userId` int(11), 
   PRIMARY KEY  (`roleId`)

);
CREATE TABLE IF NOT EXISTS `tc_permissions` (

  `id` int(11) NOT NULL auto_increment,   
  `delete_date` varchar(255)  NULL,
  `functionality` varchar(255)  NULL,
  `name` LONGTEXT  NULL,
   PRIMARY KEY  (`id`)

);
--ALTER TABLE `tc_users` 
--ADD COLUMN `delete_date` Date() NULL DEFAULT NULL;
--ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL ;

