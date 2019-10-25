var initialInfo;

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}

function getManagers(){
  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

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
  });

  getRequest.fail(function (response) {
      console.log("fail");
      console.log(response);
      alert(response.responseJSON.message);
  });
}

function loadTownState(){
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
        starRating: starVal,
        chainID: chain,
        managerID: manager,
        streetNumber: streetNumberVal,
        streetName: streetNameVal,
        city: cityVal,
        state: stateVal,
        zip: zipVal
    }

    var reqURL = apiURL + "/hotels/employee/addHotel";
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
        alert(response.message);
        window.location.replace("/viewhotels.html");
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
