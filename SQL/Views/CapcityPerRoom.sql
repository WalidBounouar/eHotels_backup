CREATE VIEW capacityperroom AS
SELECT chainid, chainname, hotelid, hl.streetnumber, hl.streetname, hl.city, hl.state, hl.zip, room.id AS roomid, roomnumber, capacity 
FROM ehotels.room, ehotels.hotel AS hl, ehotels.hotelchain
WHERE(room.hotelid = hl.id AND hl.chainid = hotelchain.id)