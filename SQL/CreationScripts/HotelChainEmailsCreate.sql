CREATE TABLE ehotels.hotelchainemail(
	id int NOT NULL AUTO_INCREMENT,
    chainid int NOT NULL,
    emailaddress varchar(255) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(chainid) REFERENCES ehotels.hotelchain(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);