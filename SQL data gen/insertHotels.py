import random

print("INSERT INTO ehotels.hotel(id,chainid,managerid,starrating,streetNumber,streetname,City,state,zip) VALUES")
streets = ["Main street", "Preston street", "10th avenue", "Conqueror Blvd", "Olive Street", "Random Street"];
cities = ["Ottawa", "Toronto", "Montreal", "Calgary", "Vancouver"];
areaCodes = ["613", "416", "514", "587", "604"]
states = ["Ontario", "Ontario", "Quebec", "Alberta", "British Columbia"];
zipStarts = ["A", "A", "B", "C", "D", "E"];
characters = ["A", "B", "C", "D", "E", "F", "G", "H", "I"];
phoneNumbers = [];

for i in range(40):
    chainID = (i % 5) + 1;
    managerID = (i % 3) + 1;
    starRating = random.randint(1, 5);
    streetNumber = random.randint(10, 250);

    streetIndex = random.randint(0, 5);
    street = streets[streetIndex];

    index = random.randint(0, 4);
    city = cities[index];
    state = states[index];
    zipStart = zipStarts[index];
    actualZip = zipStart + str(random.randint(0,9)) + characters[random.randint(0,8)] + str(random.randint(0,9)) + characters[random.randint(0,8)] + str(random.randint(0,9));

    row = "(" + str(i+1) + "," + str(chainID) + "," + str(managerID) + "," + str(starRating) + "," + str(streetNumber) + ",'" + street + "','" + city + "','" + state + "','" + actualZip + "')" + ",";
    print(row)

    numberPhones = random.randint(0,2);
    for j in range(numberPhones):
        phone = "(" + str(i+1) + ",'" + areaCodes[index]+"-"+str(random.randint(100, 999))+"-"+str(random.randint(1000, 9999)) + "'),";
        phoneNumbers.append(phone);


for x in phoneNumbers:
    print(x);
