import random

print("INSERT INTO ehotels.hotel(id,chainid,managerid,starrating,streetNumber,streetname,City,state,zip) VALUES")
streets = ["Main street", "Preston street", "10th avenue", "Conqueror Blvd", "Olive Street", "Random Street"];
cities = ["Ottawa", "Toronto", "Montreal", "Calgary", "Vancouver"];
states = ["Ontario", "Ontario", "Quebec", "Alberta", "British Columbia"];
zipStarts = ["A5A", "A5A", "B4B", "C3C", "D2D", "E1E"];
characters = ["A", "B", "C", "D", "E"];

for i in range(40):
    chainID = (i % 5) + 1;
    managerID = (i % 3) + 1;
    starRating = (i % 5) + 1;
    streetNumber = random.randint(10, 250);

    streetIndex = random.randint(0, 5);
    street = streets[streetIndex];

    index = random.randint(0, 4);
    city = cities[index];
    state = states[index];
    zipStart = zipStarts[index];
    actualZip = zipStart + str((i*3)%10) + characters[i%5] + str((i*5)%10);

    row = "(" + str(i+1) + "," + str(chainID) + "," + str(managerID) + "," + str(starRating) + "," + str(streetNumber) + ",'" + street + "','" + city + "','" + state + "','" + actualZip + "')" + ",";
    print(row)
