DELIMITER //
CREATE TRIGGER ehotels.room_view BEFORE INSERT ON ehotels.room
	FOR EACH ROW
    BEGIN
		IF (NEW.view <> 'seaside' AND New.view <> 'mountain') THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'The room view was not "seaside" or "mountain"';
		END IF;
	END; //