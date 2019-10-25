var initialClientInfo;

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}

function populateDetails() {

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var clientID = getURLParameter('id');

    console.log(clientID);

    var reqURL = apiURL + "/users/employee/clientDetails/" + clientID;
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "GET",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
    }

    var detailRequest = $.ajax(settings);

    detailRequest.done(function (response) {
        console.log("success");

        //side bar
        $("#sidenav-username").text(response.username);
        var pictureSrc = "media/" + getProfilePicName(response.photo);
        $("#sidenav-userpic").attr("src", pictureSrc);

        //actual content
        console.log(response);
        //NOTE: This is a hack to help make some features easy
        initialClientInfo = response;
        $("#ssn-input").val(response.ssn);
        $("#lastname-input").val(response.lastName);
        $("#middlename-input").val(response.middleName);
        $("#firstname-input").val(response.firstName);
        $("#streetnumber-input").val(response.streetNumber);
        $("#streetname-input").val(response.streetName);
        $("#city-input").val(response.city);
        $("#state-input").val(response.state);
        $("#zip-input").val(response.zip);

    });

    detailRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        if(response.status == 403) {
            window.location.replace("/index.html");
        }
        errorMsg = response.responseJSON.message;
        alert(errorMsg);
    });
}

function validateNewInfo(){

    var ssn = $("#ssn-input").val();

    if(ssn === "") {
        alert("SSN mandatory");
    } else if(ssn.length != 9) {
        alert("SSN should be length 9");
        return false;
    } else if(!(/^[a-zA-Z0-9]*$/.test(ssn))){
        alert("SSN should be alphanumeric");
        return false;
    }

    var lastName = $("#lastname-input").val();
    if(lastName === "") {
        alert("Last Name mandatory");
        return false;
    }

    //don't care for now
    var middleName = $("#middlename-input").val();

    var firstName = $("#firstname-input").val();
    if(firstName === "") {
        alert("First Name mandatory");
        return false;
    }

    var streetNumber = $("#streetnumber-input").val();
    if(streetNumber === "") {
        alert("Street Number mandatory");
        return false;
    } else if(!(/^[0-9]*$/.test(streetNumber))){
        alert("street number should be number");
        return false;
    }

    var streetName = $("#streetname-input").val();
    if(streetName === "") {
        alert("Street Name mandatory");
        return false;
    }

    var city = $("#city-input").val();
    if(city === "") {
        alert("City mandatory");
        return false;
    }

    var state = $("#state-input").val();
    if(state === "") {
        alert("State mandatory");
        return false;
    }

    var zip = $("#zip-input").val();
    if(zip === "") {
        alert("ZIP mandatory");
        return false;
    } else if(!(/^[a-zA-Z][0-9][a-zA-Z][0-9][a-zA-Z][0-9]$/.test(zip))){
        alert("Follow zip format A5A5A5");
        return false;
    }

    return true;

}

function saveChanges() {
    var valid = validateNewInfo();

    if(!valid) {
        return;
    }

    var ssnVal = $("#ssn-input").val();
    var lastNameVal = $("#lastname-input").val();
    var middleNameVal = $("#middlename-input").val();
    var firstNameVal = $("#firstname-input").val();
    var streetNumberVal = $("#streetnumber-input").val();
    var streetNameVal = $("#streetname-input").val();
    var cityVal = $("#city-input").val();
    var stateVal = $("#state-input").val();
    var zipVal = $("#zip-input").val();

    var clientID = getURLParameter("id");

    var session = findSessionCookie();

    var clientData = {
        id: clientID,
        ssn: ssnVal,
        lastName: lastNameVal,
        middleName: middleNameVal,
        firstName: firstNameVal,
        streetNumber: streetNumberVal,
        streetName: streetNameVal,
        city: cityVal,
        state: stateVal,
        zip: zipVal
    }

    var reqURL = apiURL + "/users/employee/updateClient";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "PUT",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(clientData)
    }

    var updateRequest = $.ajax(settings);

    updateRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/editclient.html?id="+clientID);
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function resetValues() {

    $("#ssn-input").val(initialClientInfo.ssn);
    $("#lastname-input").val(initialClientInfo.lastName);
    $("#middlename-input").val(initialClientInfo.middleName);
    $("#firstname-input").val(initialClientInfo.firstName);
    $("#streetnumber-input").val(initialClientInfo.streetNumber);
    $("#streetname-input").val(initialClientInfo.streetName);
    $("#city-input").val(initialClientInfo.city);
    $("#state-input").val(initialClientInfo.state);
    $("#zip-input").val(initialClientInfo.zip);

}

function deleteClient() {

    var session = findSessionCookie();
    var clientID = getURLParameter("id");

    var reqURL = apiURL + "/users/employee/deleteClient/"+clientID;
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "DELETE",
        "headers": {authorization: session}
    }

    var deleteRequest = $.ajax(settings);

    deleteRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/search.html");
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);

        if(response.status === 401) {
            alert("Can't delete. Client is responsible for booking/renting: \n"
                + formatList(response.responseJSON));
            return;
        }

        alert(response.responseJSON.message);
    });

}

function formatList(elementArray){

    var output = "";

    elementArray.forEach(element => {
        output = output + "- " + element.chainName + ", " + element.state + ", " + element.city + ", " + element.zip 
			+ ", room " + element.roomNumber + ", start: " + element.startDate.split(" ")[0] + "\n";
    });

    return output;

}
