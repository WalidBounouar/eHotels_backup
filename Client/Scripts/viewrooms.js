var renting;

function search() {

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

function redirect(event) {
    window.location.replace(event.data.path);
}

function changeClickToAdd(id) {
    $("#add-remove-btn-"+id).off('click');
    $("#add-remove-btn-"+id).click({id_param: id}, deleteRoom);
}

function changeEdit(id) {
    $("#edit-"+id).off('click');
    $("#edit-"+id).text("Edit");
    $("#edit-"+id).click({id_param: id}, editRoom);
}

function editRoom(event){
  event.stopPropagation();
  window.location.replace("/editroom.html?id="+event.data.id_param);
}

function addRoom(){
  window.location.replace("/addroom.html");
}

function deleteRoom(event){
  event.stopPropagation();

  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/rooms/employee/deleteRoom/" + event.data.id_param;
  var settings = {
      "async": true,
      "crossDomain": true,
      "url": reqURL,
      "method": "DELETE",
      "headers": {authorization: session},
      "dataType": 'json',
      "contentType": 'application/json'
  }

  var addRequest = $.ajax(settings);

  addRequest.done(function (response) {
      console.log("success");
      alert("Room deleted successfully!");
      window.location.replace("/viewrooms.html");
  });

  addRequest.fail(function (response) {
      console.log("fail");

	  if(response.status === 401){
          alert("Cannot delete. Room has active bookings/rentings! \n" + formatList(response.responseJSON));
		  return;
      }

      alert(response.responseJSON.message);
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

    var results = response;
    for (var i = 0; i < results.length; i++) {
        //set up all variable first
        var currentResult = results[i];
        var id = currentResult.roomID;
        var anchor = "/editroom.html?id=" + id;
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
                            <button id="edit-`+id+`" type="button" class="btn btn-primary">Edit</i></button>
                            <button id="add-remove-btn-`+id+`" type="button" class="btn btn-danger">Delete</i></button>
                        </div>
                    </div>
                </div>
            </div>`;
        $("#list-items-div").append(html);
        changeClickToAdd(id);
        changeEdit(id);
    }

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