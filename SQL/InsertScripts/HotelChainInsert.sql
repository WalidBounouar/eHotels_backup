INSERT INTO ehotels.hotelchain (id, chainname, streetnumber, streetname, city, state, zip)
	 VALUES(1, 'Quality Inn', '123', 'Quality Street', 'Quality Town', 'Quality state', 'Q5A5A5');

INSERT INTO ehotels.hotelchain (id, chainname, streetnumber, streetname, city, state, zip)
	VALUES(2, 'Marriot', '77', 'M Street', 'M Town', 'M state', 'M4B4B4');
    
INSERT INTO ehotels.hotelchain (id, chainname, streetnumber, streetname, city, state, zip)
	VALUES(3, 'W hotels', '456', 'W Street', 'W Town', 'W state', 'W5D5D5');
    
INSERT INTO ehotels.hotelchain (id, chainname, streetnumber, streetname, city, state, zip)
	VALUES(4, 'Hilton', '789', 'Hilton Drive', 'Paris', 'Rich state', 'P3C3C3');
    
INSERT INTO ehotels.hotelchain (id, chainname, streetnumber, streetname, city, state, zip)
	VALUES(5, 'Renaissance', '25', 'Main Street', 'Ottawa', 'Ontario', 'A8Z8Z8');
    
INSERT INTO ehotels.hotelchainemail(chainid, emailaddress)
	VALUES
		(1, 'contact@QualityInn.com'), (1, 'sales@QualityInn.com'), 
        (2, 'contact@Marriot.com'), (2, 'sales@Marriot.com'), 
        (3, 'contact@Whotels.com'), (3, 'sales@Whotels.com'), 
        (4, 'contact@Hilton.com'), (4, 'sales@Hilton.com'), 
		(5, 'contact@Renaissance.com'), (5, 'sales@Renaissance.com');