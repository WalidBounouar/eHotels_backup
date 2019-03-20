import random

print("INSERT INTO ehotels.amenity(roomid,amenity)VALUES");
amenities = ["'TV'", "'Mini Bar'", "'Ethernet'"];

for i in range(200):
    numberAmen = random.randint(0,2);
    if(numberAmen == 1):
        row = "(" + str(i+1) + "," + amenities[random.randint(0,2)] + "),";
        print(row);
    elif(numberAmen == 2):
        amenIndex = random.randint(0,2);
        row1 = "(" + str(i+1) + "," + amenities[amenIndex] + "),";
        row2 = "(" + str(i+1) + "," + amenities[(amenIndex+1)%3] + "),";
        print(row1);
        print(row2);
