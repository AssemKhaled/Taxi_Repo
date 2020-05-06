
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

CREATE TABLE IF NOT EXISTS `tc_group_device` (
  `groupid` int(11) NOT NULL,
  `deviceid` int(11) NOT NULL,
  KEY `fk_group_device_groupid` (`groupid`),
  KEY `fk_group_device_deviceid` (`deviceid`),
  CONSTRAINT `fk_group_device_deviceid` FOREIGN KEY (`deviceid`) REFERENCES `tc_devices` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_group_device_groupid` FOREIGN KEY (`groupid`) REFERENCES `tc_groups` (`id`) ON DELETE CASCADE

);

--ALTER TABLE `tc_users`
--ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL ;

--ALTER TABLE `tc_devices` ADD `sensorSettings` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_devices` ADD `fuel` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_groups` ADD `type` varchar(255) NULL DEFAULT NULL;

--ALTER TABLE `tc_attributes` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
--ALTER TABLE `tc_notifications` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
