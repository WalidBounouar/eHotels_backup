DELIMITER //
CREATE PROCEDURE ehotels.employeeregister(IN email varchar(255), IN password varchar(255), IN ssn varchar(9), IN lastname varchar(255), 
	IN middlename varchar(255), IN firstname varchar(255), IN streetnumber int, IN streetname varchar(255), IN city varchar(255), 
    IN state varchar(255), IN zip varchar(6))

BEGIN
	DECLARE logincredid int DEFAULT 0;
    INSERT INTO ehotels.logincred(email, password) VALUES(email, password);
    
    SELECT LAST_INSERT_id() INTO logincredid;
    
    INSERT INTO ehotels.employee(`ssn`, `lastname`, `middlename`, `firstname`, `streetnumber`, `streetname`, `city`, `state`, `zip`, `logincredid`)
		VALUES(ssn, lastname, middlename, firstname, streetnumber, streetname, city, state, zip, logincredid);

END//
DELIMITER ;