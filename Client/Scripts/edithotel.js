var initialInfo;

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

    var reqURL = apiURL + "/hotels/employee/hotelDetail/" + employeeID;
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

        getManagers(session, response.managerID);
        loadTownState(response.chainID);
        //side bar
        $("#sidenav-username").text(response.username);
        var pictureSrc = "media/" + getProfilePicName(response.photo);
        $("#sidenav-userpic").attr("src", pictureSrc);

        //actual content
        console.log(response);
        //NOTE: This is a hack to help make some features easy
        initialInfo = response;
        $("#starRating-input").val(response.starRating);
        $("#streetnumber-input").val(response.streetNumber);
        $("#streetname-input").val(response.streetName);
        $("#city-input").val(response.city);
        $("#state-input").val(response.state);
        $("#zip-input").val(response.zip);

        //generate role list
        for (let i = 0; i < response.phoneNumbers.length; i++) {
            var phone = response.phoneNumbers[i];
            var html = `
                <div>
                    <span class="listitem-overview">- ` + phone + ` &nbsp;
                    <button type="button" class="btn btn-primary" onclick="deletePhone('` + phone + `')">Delete</button>
                    </span>
                </div>
            `;
            $("#phone-container").append(html);
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

function getManagers(session, managerID){
  var reqURL = apiURL + "/users/employee/managers";
  var settings = {
      "async": true,
      "crossDomain": true,
      "url": reqURL,
      "method": "GET",
      "headers": {authorization: session}
  }

  var getRequest = $.ajax(settings);

  getRequest.done(function (response) {
    $.each(response, function (i, item) {
        $('#manager-input').append($('<option>', {
            value: item.id,
            text : item.firstName + " " + item.lastName + " (" + item.ssn + ")",
        }));
    });
    $("#manager-input option[value=" + managerID + "]").attr("selected", "selected");
  });

  getRequest.fail(function (response) {
      console.log("fail");
      console.log(response);
      alert(response.responseJSON.message);
  });
}

function addPhone() {

    var newPhone = $("#phone-input").val();

    if(newPhone === "" || newPhone === null || newPhone === undefined) {
        alert("Please input a role");
        return;
    }

    var hotelID = getURLParameter("id");
    var session = findSessionCookie();

    var phoneData = {
        ownerID: hotelID,
        value: newPhone
    }

    var reqURL = apiURL + "/hotels/employee/addHotelPhone";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(phoneData)
    }

    var putRequest = $.ajax(settings);

    putRequest.done(function (response) {
        console.log("success");
        console.log(response);
        alert(response.message);
        window.location.replace("/edithotel.html?id="+hotelID);
    });

    putRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });
}

function deletePhone(phone) {

    var session = findSessionCookie();
    var hotelID = getURLParameter("id");

    var reqURL = apiURL + "/hotels/employee/deleteHotelPhone?hotelID="+hotelID+"&pNumber="+phone;
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
        window.location.replace("/edithotel.html?id="+hotelID);
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function loadTownState(chain){
  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/hotels/employee/allChains";
  var settings = {
      "async": true,
      "crossDomain": true,
      "url": reqURL,
      "method": "GET",
      "headers": {authorization: session},
      "dataType": 'json',
      "contentType": 'application/json'
  }

  var searchRequest = $.ajax(settings);

  searchRequest.done(function (response) {
    $.each(response, function (i, item) {
        $('#hotelChain-input').append($('<option>', {
            value: item.id,
            text : item.name
        }));
    });
    $("#hotelChain-input option[value=" + chain + "]").attr("selected", "selected");
  });
}

function validateNewInfo(){

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

    var starVal = $("#starRating-input").val();
    var chain = $("#hotelChain-input").val();
    var manager = $("#manager-input").val();
    var streetNumberVal = $("#streetnumber-input").val();
    var streetNameVal = $("#streetname-input").val();
    var cityVal = $("#city-input").val();
    var stateVal = $("#state-input").val();
    var zipVal = $("#zip-input").val();

    var hotelID = getURLParameter("id");

    var session = findSessionCookie();

    var employeeData = {
        hotelID: hotelID,
        starRating: starVal,
        chainID: chain,
        managerID: manager,
        streetNumber: streetNumberVal,
        streetName: streetNameVal,
        city: cityVal,
        state: stateVal,
        zip: zipVal
    }

    var reqURL = apiURL + "/hotels/employee/updateHotel";
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
        alert(response.message);
        window.location.replace("/edithotel.html?id="+hotelID);
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function resetValues() {
  var hotelID = getURLParameter("id");
  window.location.replace("/edithotel.html?id="+hotelID);
}

function deleteHotel() {

    var session = findSessionCookie();
    var hotelID = getURLParameter("id");

    var reqURL = apiURL + "/hotels/employee/deleteHotel/"+hotelID;
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
        window.location.replace("/searchbookings.html");
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);

        if(response.status === 401){
          alert("Cannot delete. Hotel has active bookings/rentings! \n" + formatList(response.responseJSON));	
		  return;
        }

		alert(response.message);
    });

}

function formatList(elementArray){

    var output = "";

    elementArray.forEach(element => {
        output = output + "- " + element.clientFirstName + ", " + element.clientLastName + ", SSN: " + element.clientSSN + ", "
			+ element.chainName + ", " + element.state + ", " + element.city + ", " + element.zip 
			+ ", room " + element.roomNumber + ", start: " + element.startDate.split(" ")[0] + "\n";
    });

    return output;

}
