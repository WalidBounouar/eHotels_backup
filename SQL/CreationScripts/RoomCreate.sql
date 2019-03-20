CREATE TABLE ehotels.room(
	id int NOT NULL AUTO_INCREMENT,
    hotelid int NOT NULL,
    roomnumber int NOT NULL,
    capacity int NOT NULL,
    price float(15, 2) NOT NULL,
    extendable bool NOT NULL,
    view varchar(255) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(hotelid) REFERENCES ehotels.hotel(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
	CHECK(view IN ('mountain', 'seaside')),
    CHECK(price >= 0)
);