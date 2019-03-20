CREATE TABLE ehotels.hotelphonenumbers(
	id int NOT NULL AUTO_INCREMENT,
    hotelid int NOT NULL,
    phonenumber varchar(25) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(hotelid) REFERENCES ehotels.hotel(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);