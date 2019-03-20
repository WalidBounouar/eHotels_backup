CREATE TABLE ehotels.bookingrenting(
	id int NOT NULL AUTO_INCREMENT,
    roomid int NOT NULL,
    startdate datetime NOT NULL,
    enddate datetime NOT NULL,
    employeeid int NOT NULL,
    clientid int NOT NULL,
    checkedin bool NOT NULL,
	paid bool NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(roomid) REFERENCES ehotels.room(id)
		ON DELETE RESTRICT
        ON UPDATE CASCADE,
	FOREIGN KEY(employeeid) REFERENCES ehotels.employee(id)
		ON DELETE RESTRICT
        ON UPDATE CASCADE,
	FOREIGN KEY(clientid) REFERENCES ehotels.client(id)
		ON DELETE RESTRICT
        ON UPDATE CASCADE
);