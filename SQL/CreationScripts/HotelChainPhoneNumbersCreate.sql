CREATE TABLE ehotels.hotelchainphonenumber(
	id int NOT NULL AUTO_INCREMENT,
    chainid int NOT NULL,
    phonenumber varchar(25) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(chainid) REFERENCES ehotels.hotelchain(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);