function login() {
    //hide error message so user sees some feedback
    $("#signin-error").css("display", "none");

    // No validations needed, the only one needed is from backend

    var permLevel = (document.getElementById("perm-level").checked) ? "EMPLOYEE" : "CLIENT";
    var loginData = {
        // COMBAK: trim the values
        email: document.getElementById("email-input").value, // WARNING: not same name, but ok
        password: document.getElementById("password-input").value,
        permission: permLevel
    }

    var reqURL = apiURL + "/login";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(loginData)
    }

    var loginRequest = $.ajax(settings);

    loginRequest.done(function (response) {
        console.log(response);
        document.cookie = ehotelsCookieName + "=" + response.session;
        document.cookie = ehotelsPermLevel + "=" + permLevel;
        if(permLevel === "CLIENT"){
          window.location.replace("/mylist.html");
        } else {
          window.location.replace("/searchbookings.html");
        }
    });

    loginRequest.fail(function (response) {
        console.log(response);
        $("#signin-error").css("display", "block");
        var errorMsg = document.getElementById("signin-error");
        errorMsg.scrollIntoView();
    });

}

function register() {

    var email  = $("#remail-input").val();
    var password = $("#rpassword-input").val();
    var ssnVal = $("#ssn-input").val();
    var lastNameVal = $("#lastname-input").val();
    var middleNameVal = $("#middlename-input").val();
    var firstNameVal = $("#firstname-input").val();
    var streetNumberVal = $("#streetnumber-input").val();
    var streetNameVal = $("#streetname-input").val();
    var cityVal = $("#city-input").val();
    var stateVal = $("#state-input").val();
    var zipVal = $("#zip-input").val();

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

    var reqURL = apiURL + "/register";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(employeeData)
    }

    var updateRequest = $.ajax(settings);

    updateRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/mylist.html");
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}
