DELIMITER //
CREATE TRIGGER ehotels.hotel_manager_update BEFORE UPDATE ON ehotels.hotel
	FOR EACH ROW
    BEGIN
		IF (NEW.managerid NOT IN (SELECT employeeid FROM ehotels.employeerole WHERE(role = 'Manager' || role = 'manager'))) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'The employee that manages the hotel needs to be a manager';
		END IF;
	END; //