INSERT INTO tc_users(name, email, hashedpassword,accountType)
SELECT * FROM (SELECT 'admin', 'admin@fuinco.com','21232f297a57a5a743894a0e4a801fc3' ,'1') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_users WHERE email = 'admin@fuinco.com' 
);

UPDATE tc_users SET  tc_users.accountType = 1 where tc_users.email= 'admin@fuinco.com';

INSERT INTO tc_users(name, email, hashedpassword,accountType)
SELECT * FROM (SELECT 'vendor', 'vendor@fuinco.com','21232f297a57a5a743894a0e4a801fc3' ,'2') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_users WHERE email = 'vendor@fuinco.com'
);
UPDATE tc_users SET  tc_users.accountType = 2 where tc_users.email = 'vendor@fuinco.com';
INSERT INTO tc_user_user(userid, manageduserid)
SELECT * FROM (SELECT ( SELECT id FROM tc_users WHERE email = 'admin@fuinco.com' ), (SELECT id FROM tc_users WHERE email = 'vendor@fuinco.com')) AS tmp
WHERE NOT EXISTS (
    SELECT manageduserid FROM tc_user_user WHERE manageduserid = (SELECT id FROM tc_users WHERE email = 'vendor@fuinco.com')
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DEVICE', '{"list":true,"create":true,"edit":true,"delete":true,"assignDeviceToDriver":true,"assignGeofenceToDevice":true,"assignToUser":true,"deleteFromElm":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true,"calibration":true,"GetSpentFuel":true,"GetSensorSetting":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DEVICE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DRIVER', '{"list":true,"create":true,"edit":true,"delete":true,"assignToUser":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true,"deleteFromElm":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DRIVER'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'GEOFENCE', '{"list":true,"create":true,"edit":true,"delete":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'GEOFENCE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'USER', '{"list":true,"create":true,"edit":true,"delete":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true,"deleteFromElm":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'USER'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ROLE', '{"list":true,"create":true,"edit":true,"delete":true,"assignToUser":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ROLE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ELM', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ELM'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'GROUP', '{"list":true,"create":true,"edit":true,"delete":true,"assignGroupToDriver":true,"assignGroupToGeofence":true,"assignGroupToDevice":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'GROUP'
);


INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'COMPUTED', '{"list":true,"create":true,"edit":true,"delete":true,"assignGroupToComputed":true,"assignDeviceToComputed":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'COMPUTED'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'SCHEDULED', '{"list":true,"create":true,"edit":true,"delete":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'SCHEDULED'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'POINTS', '{"list":true,"create":true,"edit":true,"delete":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'POINTS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'NOTIFICATION', '{"list":true,"create":true,"edit":true,"delete":true,"assignGroupToNotification":true,"assignDeviceToNotification":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NOTIFICATION'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'EVENT', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'EVENT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'GEOFENCEEXIT', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'GEOFENCEEXIT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'GEOFENCENTER', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'GEOFENCENTER'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DEVICEWORKINGHOURS', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DEVICEWORKINGHOURS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'CUSTOMREPORT', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'CUSTOMREPORT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'STOP', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'STOP'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DURATIONINSTOP', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DURATIONINSTOP'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ENGINEINSTOP', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ENGINEINSTOP'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'TRIP', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'TRIP'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'TRIPSPENTFUEL', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'TRIPSPENTFUEL'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'TRIPDISTANCESPEED', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'TRIPDISTANCESPEED'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DRIVEMORETHAN', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DRIVEMORETHAN'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'SUMMARY', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'SUMMARY'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'NUMTRIPS', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NUMTRIPS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'NUMSTOPS', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NUMSTOPS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'TOTALDISTANCE', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'TOTALDISTANCE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'NUMVISITEDPLACES', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NUMVISITEDPLACES'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ENGINEHOURSNOTMOVING', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ENGINEHOURSNOTMOVING'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'NUMBERDRIVERWORKINGHOURS', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NUMBERDRIVERWORKINGHOURS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DRIVERWORKINGHOURS', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DRIVERWORKINGHOURS'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'SENSORWEIGHT', '{"list":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'SENSORWEIGHT'
);

UPDATE tc_user_user SET tc_user_user.userid= (SELECT id FROM tc_users WHERE email = 'vendor@fuinco.com') 
where tc_user_user.manageduserid IN (SELECT id FROM tc_users WHERE tc_users.accountType=0);

UPDATE tc_users SET tc_users.accountType=3 
where tc_users.id IN (select tc_user_user.manageduserid from tc_user_user where
 tc_user_user.userid = ( SELECT * FROM(SELECT f.id 
                    FROM tc_users f
                    WHERE f.email = 'vendor@fuinco.com')temp) and tc_users.email != 'vendor@fuinco.com' and tc_users.email != 'admin@fuinco.com');

