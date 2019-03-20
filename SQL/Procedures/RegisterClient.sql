DELIMITER //
CREATE PROCEDURE ehotels.clientregister(IN email varchar(255), IN password varchar(255), IN ssn varchar(6), IN lastname varchar(255), 
	IN middlename varchar(255), IN firstname varchar(255), IN streetnumber int, IN streetname varchar(255), IN city varchar(255), 
    IN state varchar(255), IN zip varchar(6), IN registrationdate date)

BEGIN
	DECLARE logincredid int DEFAULT 0;
    INSERT INTO ehotels.logincred(email, password) VALUES(email, password);
    
    SELECT LAST_INSERT_id() INTO logincredid;
    
    INSERT INTO ehotels.client(`ssn`, `lastname`, `middlename`, `firstname`, `streetnumber`, `streetname`, `city`, `state`, `zip`, `registrationdate`, `logincredid`)
		VALUES(ssn, lastname, middlename, firstname, streetnumber, streetname, city, state, zip, registrationdate, logincredid);

END//
DELIMITER ;