import random

print("INSERT INTO ehotels.bookingrenting(roomid, startdate, enddate, employeeid, clientid, checkedin, paid) VALUES")
trueFalse = ["TRUE", "FALSE"]
rooms = [];

for i in range(20):
    
    roomID = random.randint(1, 200);
    while(roomID in rooms):
        roomID = random.randint(1, 200);
    rooms.append(roomID)
    
    startDay = random.randint(1, 15);
    startDate = "'2019-04-" + str(startDay) + " 00:00:00'"
    
    lenghtReserve = random.randint(1, 5);
    endDay = startDay + lenghtReserve;
    endDate = "'2019-04-" + str(endDay) + " 00:00:00'"

    isBooking = random.randint(0,1);
    if(isBooking == 1):
        employeeID = 1; #system
        checkedIn = "FALSE"
        paid = "FALSE";
    else:
        employeeID = random.randint(2,7);
        checkedIn = "TRUE"
        paid = trueFalse[random.randint(0,1)]

    clientID = random.randint(1,3);

    row = "(" + str(roomID) + "," + startDate + "," + endDate + "," + str(employeeID) + "," + str(clientID) + "," + checkedIn + "," + paid + "),"
    print(row)
