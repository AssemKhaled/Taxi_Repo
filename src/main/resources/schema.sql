CREATE TABLE IF NOT EXISTS `tc_user_client_user` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `manageduserid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_device` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `deviceid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_driver` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `driverid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_group` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `groupid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_geofence` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `geofenceid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_schedule` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `scheduleid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_notification` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `notificationid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_computed` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `computedid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_role` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `roleid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_user_client_point` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `pointid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
);
CREATE TABLE IF NOT EXISTS `tc_points` (

  `id` int(11) NOT NULL auto_increment, 
  `name` varchar(512)  NULL,
  `latitude` double  NULL,
  `longitude` double  NULL,
  `userId` int(11),
  `photo` LONGTEXT  NULL,
  `delete_date` varchar(255)  NULL,
   PRIMARY KEY  (`id`)

);
CREATE TABLE IF NOT EXISTS `tc_schedule` (

  `id` int(11) NOT NULL auto_increment, 
  `expression` varchar(4000)  NULL,
  `task` LONGTEXT  NULL,
  `userId` int(11), 
  `date` varchar(255)  NULL,
  `date_type` varchar(255)  NULL,
  `email` varchar(255)  NULL,
  `delete_date` varchar(255)  NULL,
   PRIMARY KEY  (`id`)

);

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
  `functionality` varchar(1080)  NULL,
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

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='accountType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='parents'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='roleId'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='fuel'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `fuel` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='representative'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `representative` LONGTEXT NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='delete_from_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `delete_from_elm` LONGTEXT NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='icon'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `icon` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='create_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `create_date` timestamp NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='create_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `create_date` timestamp NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='expired'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN  `expired` TINYINT(1) DEFAULT 0 NOT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='port'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `port` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='protocol'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `protocol` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='device_type'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `device_type` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='sensorSettings'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `sensorSettings` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_groups'
AND column_name='type'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_groups'
, ' ADD COLUMN `type` varchar(255) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_attributes'
AND column_name='delete_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_attributes'
, ' ADD COLUMN `delete_date` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_notifications'
AND column_name='delete_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_notifications'
, ' ADD COLUMN `delete_date` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='dateType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN  `dateType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='dateOfBirth'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `dateOfBirth` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_schedule'
AND column_name='email'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_schedule'
, ' ADD COLUMN `email` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
ALTER TABLE `tc_devices` MODIFY `calibrationData` VARCHAR(1080);
--ALTER TABLE `tc_devices` DROP `positionid`;
--ALTER TABLE `tc_devices` ADD COLUMN `positionid` text NULL DEFAULT NULL;

--ALTER TABLE `tc_permissions` MODIFY `functionality` VARCHAR(1080);

----------------------------------------------------------------
--ALTER TABLE `tc_users`
--ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN  `dateType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN `dateOfBirth` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL ;

--ALTER TABLE `tc_devices` ADD `sensorSettings` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_devices` ADD `fuel` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_groups` ADD `type` varchar(255) NULL DEFAULT NULL;

--ALTER TABLE `tc_attributes` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
--ALTER TABLE `tc_notifications` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
