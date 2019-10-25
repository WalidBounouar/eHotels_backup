var initialEmployeeInfo;

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

    var employeeID = getURLParameter('id');

    console.log(employeeID);

    var reqURL = apiURL + "/users/employee/employeeDetails/" + employeeID;
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
        initialEmployeeInfo = response;
        $("#ssn-input").val(response.ssn);
        $("#lastname-input").val(response.lastName);
        $("#middlename-input").val(response.middleName);
        $("#firstname-input").val(response.firstName);
        $("#streetnumber-input").val(response.streetNumber);
        $("#streetname-input").val(response.streetName);
        $("#city-input").val(response.city);
        $("#state-input").val(response.state);
        $("#zip-input").val(response.zip);

        //generate role list
        for (let i = 0; i < response.roles.length; i++) {
            var role = response.roles[i];
            var html = `
                <div>
                    <span class="listitem-overview">- ` + role + ` &nbsp;
                    <button type="button" class="btn btn-primary" onclick="deleteRole('` + role + `')">Delete</button>
                    </span>
                </div>
            `;
            $("#role-container").append(html);
        }

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

function addRole() {

    var newRole = $("#role-input").val();

    if(newRole === "" || newRole === null || newRole === undefined) {
        alert("Please input a role");
        return;
    }

    var employeeID = getURLParameter("id");
    var session = findSessionCookie();

    var roleData = {
        ownerID: employeeID,
        value: newRole
    }

    var reqURL = apiURL + "/users/employee/addRole";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(roleData)
    }

    var putRequest = $.ajax(settings);

    putRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/editemployee.html?id="+employeeID);
    });

    putRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });
}

function deleteRole(role) {

    var session = findSessionCookie();
    var employeeID = getURLParameter("id");

    var reqURL = apiURL + "/users/employee/deleteRole?employeeID="+employeeID+"&role="+role;
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
        window.location.replace("/editemployee.html?id="+employeeID);
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
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

    var employeeID = getURLParameter("id");

    var session = findSessionCookie();

    var employeeData = {
        id: employeeID,
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

    var reqURL = apiURL + "/users/employee/updateEmployee";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "PUT",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(employeeData)
    }

    var updateRequest = $.ajax(settings);

    updateRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/editemployee.html?id="+employeeID);
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function resetValues() {

    $("#ssn-input").val(initialEmployeeInfo.ssn);
    $("#lastname-input").val(initialEmployeeInfo.lastName);
    $("#middlename-input").val(initialEmployeeInfo.middleName);
    $("#firstname-input").val(initialEmployeeInfo.firstName);
    $("#streetnumber-input").val(initialEmployeeInfo.streetNumber);
    $("#streetname-input").val(initialEmployeeInfo.streetName);
    $("#city-input").val(initialEmployeeInfo.city);
    $("#state-input").val(initialEmployeeInfo.state);
    $("#zip-input").val(initialEmployeeInfo.zip);

}

function deleteEmployee() {

    var session = findSessionCookie();
    var employeeID = getURLParameter("id");

    var reqURL = apiURL + "/users/employee/deleteEmployee/"+employeeID;
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

        if(response.status === 401
            && response.responseJSON.message === "Employee manages hotels") {
            alert("Can't delete. Employee is manages hotels: \n"
                + formatList(response.responseJSON.hotels));
            return;
        }

        if(response.status === 401
            && response.responseJSON.message === "Employee is responsible for bookings/rentings") {
            alert("Can't delete. Employee is responsible for booking/renting: \n"
                + formatListClient(response.responseJSON.bookingsRentings));
            return;
        }

        alert(response.responseJSON.message);
    });

}

function formatList(elementArray){

    var output = "";

    elementArray.forEach(element => {
        output = output + "- " + element.chainName + ", " + element.state + ", " + element.city + ", " + element.zip + "\n";
    });

    return output;

}

function formatListClient(elementArray){

    var output = "";

    elementArray.forEach(element => {
        output = output + "- " + element.clientFirstName + ", " + element.clientLastName + ", SSN: " + element.clientSSN + ", "
			+ element.chainName + ", " + element.state + ", " + element.city + ", " + element.zip 
			+ ", room " + element.roomNumber + ", start: " + element.startDate.split(" ")[0] + "\n";
    });

    return output;

}
