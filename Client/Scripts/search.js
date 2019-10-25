var renting;

function search() {

    //ugggggggggggh of couuuuuuurse I have to convert date values
    var dates = getCorrectDate();
    var startVal = dates.startVal;
    var endVal = dates.endVal;

    //this is probably sooooo inefficient but do I care? No this is the fastest I've ever written code in my life
    var town = "";
    if($("#inputTown").val() != "Choose..."){
       town = $("#inputTown").val();
    }

    var state = "";
    if($("#inputState").val() != "Choose..."){
       state = $("#inputState").val();
    }

    var chain = "";
    if($("#inputHotelChain").val() != "Choose..."){
       chain = $("#inputHotelChain").val();
    }

    var searchData = {
      startDate: startVal,
      endDate: endVal,
      minCapacity: $("#minCapacity").val(),
      maxCapacity: $("#maxCapacity").val(),
      minRating: $("#minRating").val(),
      maxRating: $("#maxRating").val(),
      minNumberRooms: $("#minRooms").val(),
      maxNumberRooms: $("#maxRooms").val(),
      town: town,
      state: state,
      hotelChain: chain,
      minPrice: $("#minPrice").val(),
      maxPrice: $("#maxPrice").val()
    }

    var session = findSessionCookie();

    console.log(session);
    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var reqURL = apiURL + "/rooms/search";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(searchData)
    }

    var searchRequest = $.ajax(settings);

    searchRequest.done(function (response) {
        console.log("success");
        renderSearch(response);
    });

    searchRequest.fail(function (response) {
        // COMBAK: DO something
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.error);
    });
}

function getCorrectDate(){
  if($("#startDate").val() == "" || $("#endDate").val() == ""){
      var startVal = "";
      var endVal = "";
  } else {
      var startVal = $("#startDate").val().split("/");
      var endVal = $("#endDate").val().split("/");
      startVal = startVal[2] + "-" + startVal[0] + "-" + startVal[1] + " 16:00:00";
      endVal = endVal[2] + "-" + endVal[0] + "-" + endVal[1] + " 11:00:00";
  }
  return {startVal,endVal};
}

function loadTownState(){
  var session = findSessionCookie();

  console.log(session);
  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/hotels/citiesAndStates";
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
    $.each(response.cities, function (i, item) {
        $('#inputTown').append($('<option>', {
            value: item,
            text : item
        }));
    });
    $.each(response.states, function (i, item) {
        $('#inputState').append($('<option>', {
            value: item,
            text : item
        }));
    });
    $.each(response.chains, function (i, item) {
        $('#inputHotelChain').append($('<option>', {
            value: item,
            text : item
        }));
    });
  });

  searchRequest.fail(function (response) {
      // COMBAK: DO something
      console.log("fail");
      console.log(response);
  });
}

function loadClients(){
  var session = findSessionCookie();
  $('#inputClient').empty();
  
  console.log(session);
  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/users/employee/clients";
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
    console.log(response);
    $.each(response, function (i, item) {
        $('#inputClient').append($('<option>', {
            value: item.id,
            text : item.firstName + " " + item.lastName + " (" + item.ssn + ")"
        }));
    });
  });

  searchRequest.fail(function (response) {
      // COMBAK: DO something
      console.log("fail");
      console.log(response);
  });
}

function redirect(event) {
    window.location.replace(event.data.path);
}

function changeClickToAdd(id) {
    $("#add-remove-btn-"+id).off('click');
    $("#add-remove-btn-"+id).click({id_param: id}, bookRoom);
}

function rentRoom() {

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    if($("#startDate").val() == "" || $("#endDate").val() == ""){
      alert("Please select dates in order to book!");
      return;
    }

    var clientid = $("#inputClient").val();

    var dates = getCorrectDate();

    var data = {
      roomID: renting,
      startDate: dates.startVal,
      endDate: dates.endVal,
      clientID: clientid
    }

    var reqURL = apiURL + "/bookingRenting/employee/directRent";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(data)
    }

    var addRequest = $.ajax(settings);

    addRequest.done(function (response) {
        console.log("success");
        alert("Room booked successfully!");
        window.location.replace("/searchrentings.html");
    });

    addRequest.fail(function (response) {
        console.log("fail");
        alert(response.responseJSON.error);
    });

}

function bookRoom(event) {
    event.stopPropagation();
    console.log("add to list " + event.data.id_param);

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    if($("#startDate").val() == "" || $("#endDate").val() == ""){
      alert("Please select dates in order to book!");
      return;
    }

    var permlevel = findPermCookie();
    if(permlevel === "EMPLOYEE"){
       renting = event.data.id_param;
       $("#selectClient").modal();
       loadClients();
       return;
    }

    var dates = getCorrectDate();
    console.log(dates);
    var data = {
      roomID: event.data.id_param,
      startDate: dates.startVal,
      endDate: dates.endVal
    }

    var reqURL = apiURL + "/bookingRenting/client/book";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "POST",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
        "data": JSON.stringify(data)
    }

    var addRequest = $.ajax(settings);

    addRequest.done(function (response) {
        console.log("success");
        alert("Room booked successfully!");
        window.location.replace("/mylist.html");
    });

    addRequest.fail(function (response) {
        console.log("fail");
        alert(response.responseJSON.error);
    });

}

function renderSearch(response) {
    console.log(response);
    $("#list-items-div").empty();

    //set the heading
    var headingValue = "Available Rooms:";
    $("#search-terms").text(headingValue)

    //feedback in case of no results
    if(response.length == 0) {
        $("#list-items-div").append('<p> No results found </p>');
        return;
    }

    var buttonName = "Book";

    var permlevel = findPermCookie();
    if(permlevel === "EMPLOYEE"){
       buttonName = "Rent";
    }


    var results = response;
    for (var i = 0; i < results.length; i++) {
        //set up all variable first
        var currentResult = results[i];
        var id = currentResult.roomID;
        var anchor = "/showdetails.html?from=search&id=" + id + "&start=" + $("#startDate").val() + "&end=" + $("#endDate").val();
        var chainName = currentResult.chainName;
        var view = currentResult.view;
        var rating = currentResult.starRating;

        var price = currentResult.price;
        var address = currentResult.streetNumber + " " + currentResult.streetName + ", " + currentResult.city + ", " + currentResult.state;
        if(view == "seaside") {
            posterPath = "media/seaview.jpg";
        } else {
            posterPath = "media/mountainview.jpg";
        }

        //generate html
        var html = `
            <div id="wrapper-`+id+`" class="container listitem-container">
                <div class="row">
                    <div class="col-sm-auto">
                        <a href="`+anchor+`">
                        <img src="`+ posterPath +`" width="300" height="272" class="listitem-poster" alt="imageAlt">
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <div class="row listitem-row">
                            <a href="`+anchor+`">
                            <h2>`+chainName + ": " + view +`</h2>
                            </a>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Rating: </span>
                            <p class="listitem-overview">`+rating+`</p>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Price:</span> <span>`+price+`$</span>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Address:</span> <span>`+address+`</span>
                        </div>
                        <div class="row listitem-row">
                            <!-- COMBAK: add onlclick for the buttons -->
                            <button id="add-remove-btn-`+id+`" type="button" class="btn btn-primary">`+ buttonName + `</i></button>
                        </div>
                    </div>
                </div>
            </div>`;
        $("#list-items-div").append(html);
        changeClickToAdd(id);
    }

}
