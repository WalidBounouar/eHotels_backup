CREATE TABLE ehotels.issue(
	id int NOT NULL AUTO_INCREMENT,
    roomid int NOT NULL,
    category varchar(255) NOT NULL,
    description varchar(2000) NOT NULL,
    datereported datetime NOT NULL,
    isfixed bool NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(roomid) REFERENCES ehotels.room(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);