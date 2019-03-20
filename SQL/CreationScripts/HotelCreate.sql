CREATE TABLE ehotels.hotel(
	id int NOT NULL AUTO_INCREMENT,
    chainid int NOT NULL,
	managerid int NOT NULL,
    starrating int NOT NULL,
    streetnumber int NOT NULL,
    streetname varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    state varchar(255) NOT NULL,
    zip varchar(6) NOT NULL,
    PRIMARY KEY(id),
	FOREIGN KEY(chainid) REFERENCES ehotels.hotelchain(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
	FOREIGN KEY(managerid) REFERENCES ehotels.employee(id)
		ON DELETE RESTRICT
        ON UPDATE CASCADE,
	CHECK (starrating >= 0 && starrating <= 5),
    CHECK (managerid IN (SELECT employeeid FROM ehotels.employeerole WHERE(role = 'Manager' || role = 'manager')))
);