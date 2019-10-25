CREATE TABLE ehotels.session(
	id int NOT NULL AUTO_INCREMENT,
    uuid varchar(60) NOT NULL,
	logincredid int NOT NULL,
    expiration datetime NOT NULL,
    permission varchar(60) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(logincredid) REFERENCES ehotels.logincred(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);