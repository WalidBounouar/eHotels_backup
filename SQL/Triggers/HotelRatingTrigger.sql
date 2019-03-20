DELIMITER //
CREATE TRIGGER ehotels.hotelrating BEFORE INSERT ON ehotels.hotel
	FOR EACH ROW
    BEGIN
		IF (NEW.starrating < 0 || NEW.starrating > 5) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'hotel rating must be between 0 and 5';
		END IF;
	END; //