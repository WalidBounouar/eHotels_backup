CREATE TABLE ehotels.client(
	id int NOT NULL AUTO_INCREMENT,
    ssn varchar(6) NOT NULL,
    lastname varchar(255) NOT NULL,
    middlename varchar(255) NOT NULL,
    firstname varchar(255) NOT NULL,
    streetnumber int NOT NULL,
    streetname varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    state varchar(255) NOT NULL,
    zip varchar(6) NOT NULL,
    registrationdate date NOT NULL,
    logincredid int NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(logincredid) REFERENCES ehotels.logincred(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);