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
SELECT * FROM (SELECT 'DEVICE', '{"list":true,"create":true,"edit":true,"delete":true,"assignDeviceToDriver":true,"assignGeofenceToDevice":true,"assignToUser":true,"connectToElm":true,"verifyInElm":true,"updateInElm":true,"calibration":true}') AS tmp
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
SELECT * FROM (SELECT 'EVENTREPORT', '{"list":true,"export":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'EVENTREPORT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'USER', '{"list":true,"create":true,"edit":true,"delete":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'USER'
);
INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'ROLE', '{"list":true,"create":true,"edit":true,"delete":true,"assignToUser":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'ROLE'
);
INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DEVICEWORKINGHOURSREPORT', '{"list":true,"export":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DEVICEWORKINGHOURSREPORT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'DRIVERWORKINGHOURSREPORT', '{"list":true,"export":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'DRIVERWORKINGHOURSREPORT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'TRIPREPORT', '{"list":true,"export":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'TRIPREPORT'
);
INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'STOPREPORT', '{"list":true,"export":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'STOPREPORT'
);

INSERT INTO tc_permissions(name,functionality)
SELECT * FROM (SELECT 'BILLING', '{"getBilling":true}') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM tc_permissions WHERE name = 'BILLING'
);



UPDATE tc_user_user SET tc_user_user.userid= (SELECT id FROM tc_users WHERE email = 'vendor@fuinco.com') 
where tc_user_user.manageduserid IN (SELECT id FROM tc_users WHERE tc_users.accountType=0);

UPDATE tc_users SET tc_users.accountType=3 
where tc_users.id IN (select tc_user_user.manageduserid from tc_user_user where
 tc_user_user.userid = ( SELECT * FROM(SELECT f.id 
                    FROM tc_users f
                    WHERE f.email = 'vendor@fuinco.com')temp) and tc_users.email != 'vendor@fuinco.com' and tc_users.email != 'admin@fuinco.com');

