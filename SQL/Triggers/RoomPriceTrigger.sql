DELIMITER //
CREATE TRIGGER ehotels.room_price BEFORE INSERT ON ehotels.room
	FOR EACH ROW
    BEGIN
		IF (NEW.price < 0) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'The room price was under 0';
		END IF;
	END; //