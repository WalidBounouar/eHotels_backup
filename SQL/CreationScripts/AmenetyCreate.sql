CREATE TABLE ehotels.amenity(
	id int NOT NULL AUTO_INCREMENT,
    roomid int NOT NULL,
    amenity varchar(255) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(roomid) REFERENCES ehotels.room(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);