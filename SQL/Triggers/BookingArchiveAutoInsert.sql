DELIMITER //
CREATE TRIGGER ehotels.bookingrentingdelete BEFORE DELETE ON ehotels.bookingrenting
FOR EACH ROW
BEGIN
    DECLARE var_startdate datetime;
    DECLARE var_enddate datetime;
    DECLARE var_clientid int;
    DECLARE var_clientlastname varchar(255);
    DECLARE var_clientfirstname varchar(255);
    DECLARE var_chainname varchar(255);
	DECLARE var_hotelzip varchar(6);
	DECLARE var_roomid int;
	DECLARE var_roomnumber int;
    
    DECLARE var_empid int;
    DECLARE var_emplastname varchar(255);
    DECLARE var_empfirstname varchar(255);

	IF(OLD.checkedin = FALSE) THEN
        SELECT startdate, enddate, clientid, lastname, firstname, chainname, hl.zip, roomid, roomnumber 
			INTO var_startdate, var_enddate, var_clientid, var_clientlastname, var_clientfirstname, var_chainname, var_hotelzip, var_roomid, var_roomnumber
            FROM ehotels.bookingrenting AS br, ehotels.client AS cl, ehotels.room AS rm, ehotels.hotel AS hl, ehotels.hotelchain AS hc
			WHERE(br.clientid = cl.id AND br.roomid = rm.id AND rm.hotelid = hl.id AND hl.chainid = hc.id AND br.id = OLD.id);
    
		INSERT INTO `ehotels`.`bookingarchive`(`startdate`,`enddate`,`clientid`,`clientlastname`,`clientfirstname`,`chainname`,`hotelzip`,`roomid`,`roomnumber`,`canceled`)
			VALUES(var_startdate, var_enddate, var_clientid, var_clientlastname, var_clientfirstname, var_chainname, var_hotelzip, var_roomid, var_roomnumber, TRUE);
	ELSE
		SELECT startdate, enddate, clientid, cl.lastname, cl.firstname, employeeid, emp.lastname, emp.firstname, chainname, hl.zip, roomid, roomnumber 
			INTO var_startdate, var_enddate, var_clientid, var_clientlastname, var_clientfirstname, var_empid, var_empfirstname, var_emplastname, var_chainname, var_hotelzip, var_roomid, var_roomnumber
            FROM ehotels.bookingrenting AS br, ehotels.client AS cl, ehotels.employee AS emp, ehotels.room AS rm, ehotels.hotel AS hl, ehotels.hotelchain AS hc
			WHERE(br.clientid = cl.id AND br.employeeid = emp.id AND br.roomid = rm.id AND rm.hotelid = hl.id AND hl.chainid = hc.id AND br.id = OLD.id);
    
		INSERT INTO `ehotels`.`rentingarchive`(`startdate`,`enddate`,`clientid`,`clientlastname`,`clientfirstname`,`employeeid`,`employeelastname`,`employeefirstname`,`chainname`,`hotelzip`,`roomid`,`roomnumber`)
			VALUES(var_startdate, var_enddate, var_clientid, var_clientlastname, var_clientfirstname, var_empid, var_emplastname, var_empfirstname, var_chainname, var_hotelzip, var_roomid, var_roomnumber);
	END IF;
END;
//