CREATE TABLE ehotels.employeerole(
	id int NOT NULL AUTO_INCREMENT,
    employeeid int NOT NULL,
    role varchar(255) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(employeeid) REFERENCES ehotels.employee(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);