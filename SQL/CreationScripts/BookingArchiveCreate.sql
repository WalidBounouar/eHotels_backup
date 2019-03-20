CREATE TABLE ehotels.bookingarchive(
	id int NOT NULL AUTO_INCREMENT,
    startdate datetime NOT NULL,
    enddate datetime NOT NULL,
    clientid int NOT NULL,
    clientlastname varchar(255) NOT NULL,
    clientfirstname varchar(255) NOT NULL,
    chainname varchar(255) NOT NULL,
	hotelzip varchar(6) NOT NULL,
	roomid int NOT NULL,
	roomnumber int NOT NULL,
    canceled bool NOT NULL,
    PRIMARY KEY(id)
);