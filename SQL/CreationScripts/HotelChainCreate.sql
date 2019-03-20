CREATE TABLE ehotels.hotelchain(
	id int NOT NULL AUTO_INCREMENT,
    chainname varchar(255) NOT NULL,
    streetnumber int NOT NULL,
    streetname varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    state varchar(255) NOT NULL,
    zip varchar(6) NOT NULL,
    PRIMARY KEY(id)
);