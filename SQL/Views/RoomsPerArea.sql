CREATE VIEW freeroomsperarea AS
SELECT COUNT(*) AS numberofrooms, city, state 
FROM ehotels.room, ehotels.hotel 
WHERE (room.hotelid = hotel.id
	AND room.id NOT IN 
		(SELECT br.roomid FROM ehotels.bookingrenting AS br 
		WHERE (br.startdate <= UTC_TIMESTAMP() AND br.enddate >= UTC_TIMESTAMP()))
) 
GROUP BY  city, state
