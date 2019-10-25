var initialEmployeeInfo;

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
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

    var email  = $("#email-input").val();
    var password = $("#password-input").val();
    var ssnVal = $("#ssn-input").val();
    var lastNameVal = $("#lastname-input").val();
    var middleNameVal = $("#middlename-input").val();
    var firstNameVal = $("#firstname-input").val();
    var streetNumberVal = $("#streetnumber-input").val();
    var streetNameVal = $("#streetname-input").val();
    var cityVal = $("#city-input").val();
    var stateVal = $("#state-input").val();
    var zipVal = $("#zip-input").val();

    var session = findSessionCookie();

    var employeeData = {
        email: email,
        password: password,
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

    var reqURL = apiURL + "/users/employee/addEmployee";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
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
        window.location.replace("/viewemployees.html");
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}
