import random

print("INSERT INTO ehotels.room (id,hotelid,roomnumber,capacity,price,extendable,view) VALUES")

views = ["'mountain'", "'seaside'"]
extendableValues = ["TRUE", "FALSE"]
count = 1;
for i in range(40):
    for j in range(5):
        hotelID = i + 1;
        roomNumber = j + 1;
        capacity = j + 2;
        viewIndex = random.randint(0,1);
        view = views[viewIndex];
        price = 50 + (35*viewIndex) + (capacity*3.5);
        extendable = extendableValues[random.randint(0,1)];

        row = "(" + str(count) + "," + str(hotelID) + "," + str(roomNumber) + "," + str(capacity) + "," + str(price) + "," + extendable + "," + view + "),";
        print(row)
        count+=1;
