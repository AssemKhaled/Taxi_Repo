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
SELECT * FROM (SELECT 'DEVICE', '{"list":true,"create":true,"edit":true,"delete":true,"assignDeviceToDriver":true,"assignGeofenceToDevice":true,"assignToUser":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true,"calibration":true,"GetSpentFuel":true,"GetSensorSetting":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DEVICE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DRIVER', '{"list":true,"create":true,"edit":true,"delete":true,"assignToUser":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DRIVER'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'GEOFENCE', '{"list":true,"create":true,"edit":true,"delete":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'GEOFENCE'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'USER', '{"list":true,"create":true,"edit":true,"delete":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'USER'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ROLE', '{"list":true,"create":true,"edit":true,"delete":true,"assignToUser":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ROLE'
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
SELECT * FROM (SELECT 'NOTIFICATION', '{"list":true,"create":true,"edit":true,"delete":true,"assignGroupToNotification":true,"assignDeviceToNotification":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'NOTIFICATION'
);


UPDATE tc_user_user SET tc_user_user.userid= (SELECT id FROM tc_users WHERE email = 'vendor@fuinco.com') 
where tc_user_user.manageduserid IN (SELECT id FROM tc_users WHERE tc_users.accountType=0);

UPDATE tc_users SET tc_users.accountType=3 
where tc_users.id IN (select tc_user_user.manageduserid from tc_user_user where
 tc_user_user.userid = ( SELECT * FROM(SELECT f.id 
                    FROM tc_users f
                    WHERE f.email = 'vendor@fuinco.com')temp) and tc_users.email != 'vendor@fuinco.com' and tc_users.email != 'admin@fuinco.com');

