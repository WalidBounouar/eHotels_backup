var initialInfo;

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}

function loadHotels(){
  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/hotels/employee/allHotels";
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
        $('#hotel-input').append($('<option>', {
            value: item.hotelID,
            text : item.chainName + ": " + item.streetNumber + " " + item.streetName + " " + item.city + ", " + item.state + " (" + item.zip + ")"
        }));
    });
  });
}

function saveChanges() {

    var roomNum = $("#roomNumber-input").val();
    var capacity = $("#capacity-input").val();
    var price = $("#price-input").val();
    var extendable = $("#extendable-input").is(':checked');
    var view = $("#view-input").val();
    var price = $("#price-input").val();

    var hotelID = $("#hotel-input").val();

    var session = findSessionCookie();

    var employeeData = {
        hotelID: hotelID,
        price: price,
        extendable: extendable,
        capacity: capacity,
        price: price,
        view: view,
        roomNumber: roomNum
    }

    var reqURL = apiURL + "/rooms/employee/addRoom";
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
        window.location.replace("/viewrooms.html");
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function formatList(elementArray){

    var output = "";

    elementArray.forEach(element => {
        output = output + "- " + JSON.stringify(element) + "\n";
    });

    return output;

}
