USE ehotels;

SELECT * FROM room AS R1, hotel, hotelchain
WHERE(R1.hotelid = hotel.id AND hotel.chainid = hotelchain.id
    AND R1.capacity >= 3
    AND R1.capacity <= 3 
	AND hotel.city = 'Ottawa' 
	AND hotel.state = 'Ottawa' 
    AND hotelchain.chainname = 'Hilton'
    AND hotel.starrating >= 2
    AND hotel.starrating <= 5
    AND 1 <= (
		SELECT count(*) FROM room AS R2 WHERE (R2.hotelid = R1.hotelid )
        )
	AND 50 >= (
		SELECT count(*) FROM room AS R2 WHERE (R2.hotelid = R1.hotelid )
        )
    AND R1.price >= 20.00
    AND R1.price <= 450.00
    AND R1.id NOT IN(
		SELECT BR.roomid FROM bookingrenting AS BR 
			WHERE(
				(BR.startdate >= '2019-12-31 23:59:59' AND BR.startdate >= '2019-12-31 23:59:59') 
				OR (BR.startdate <= '2019-12-31 23:59:59' AND BR.startdate <= '20199-12-31 23:59:59')
			)
	)
);