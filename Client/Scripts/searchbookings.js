var values = [];

function continueSearch() {
    var clientName = $("#inputClientName").val();
    var clientSSN = $("#inputSSN").val();

    var filteredVals = [];

    for(var i = 0; i < values.length; i++){
      var checked = values[i].clientFirstName.toLowerCase().includes(clientName.toLowerCase()) || values[i].clientLastName.toLowerCase().includes(clientName.toLowerCase())
         || values[i].clientSSN.toLowerCase().includes(clientSSN.toLowerCase());

      if(clientName === "" && clientSSN === ""){
        checked = true;
      } else if(clientName === ""){
        checked = values[i].clientSSN.toLowerCase().includes(clientSSN.toLowerCase());
      } else if(clientSSN === ""){
        checked = values[i].clientFirstName.toLowerCase().includes(clientName.toLowerCase()) || values[i].clientLastName.toLowerCase().includes(clientName.toLowerCase());
      }
       if(checked){
         filteredVals.push(values[i]);
       }
    }
    renderSearch(filteredVals);
}

function findBooking(id){
   for(var i = 0; i < values.length; i++){
      if(values[i].bookingID === id){
        return values[i];
      }
   }
}

function redirect(event) {
    window.location.replace(event.data.path);
}

function pad(n){return n<10 ? '0'+n : n}

function changeClickToAdd(id) {
    $("#add-remove-btn-"+id).off('click');
    $("#add-remove-btn-"+id).text("Rent");
    $("#add-remove-btn-"+id).click({id_param: id}, rentRoom);
}

function changeClickToRemove(id) {
    $("#remove-btn-"+id).off('click');
    $("#remove-btn-"+id).text("Cancel");
    $("#remove-btn-"+id).click({id_param: id}, cancelRoom);
}

function cancelRoom(event){
  event.stopPropagation();
  console.log("cancel booking " + event.data.id_param);

  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/bookingRenting/employee/cancelRentingOrBooking/" + event.data.id_param;
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
      alert("Booking canceled successfully!");
      window.location.replace("/searchbookings.html");
  });

  addRequest.fail(function (response) {
      console.log("fail");

      alert(response.responseJSON.message);
  });
}

function rentRoom(event) {
    event.stopPropagation();

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var d = new Date();
    var booking = findBooking(event.data.id_param);
    var start = booking.startDate.split(" ")[0].split("-");
    var month = pad(d.getMonth()+1);

    if(!(start[0] == d.getFullYear() && start[1] == month && start[2] == pad(d.getDate()))){
       //basically if we trying to check in a booking that's not for today
       if (!confirm('Warning: the start date of this booking is not today. Check in anyways?')) {
          return;
       }
    }

    var reqURL = apiURL + "/bookingRenting/employee/rent/"+event.data.id_param;
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "PUT",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json'
    }

    var addRequest = $.ajax(settings);

    addRequest.done(function (response) {
        console.log("success");
        alert("Room rented successfully!");
        window.location.replace("/searchbookings.html");
    });

    addRequest.fail(function (response) {
        console.log("fail");
        alert(response.responseJSON.error);
    });

}

function updateResults(){
  var session = findSessionCookie();

  console.log(session);
  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/bookingRenting/employee/allBookings";
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
      console.log("success");
      values = response;
      continueSearch();
  });

  searchRequest.fail(function (response) {
      // COMBAK: DO something
      console.log("fail");
      console.log(response);
      alert(response.responseJSON.error);
  });
}

function loadBookings(){
  var session = findSessionCookie();

  console.log(session);
  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/bookingRenting/employee/allBookings";
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
      console.log("success");
      values = response;
      renderSearch(values);
  });

  searchRequest.fail(function (response) {
      // COMBAK: DO something
      console.log("fail");
      console.log(response);
      alert(response.responseJSON.error);
  });
}

function renderSearch(results) {
    console.log(results);
    $("#list-items-div").empty();

    //set the heading
    var headingValue = "Available Bookings:";
    $("#search-terms").text(headingValue)

    //feedback in case of no results
    if(results.length == 0) {
        $("#list-items-div").append('<p> No results found </p>');
        return;
    }

    for (var i = 0; i < results.length; i++) {
        //set up all variable first
        var currentResult = results[i];
        var id = currentResult.bookingID;
        var clientName = currentResult.clientFirstName + " " + currentResult.clientLastName;
        var dates = convertDates(currentResult.startDate, currentResult.endDate);
        var startDate = dates.formated1;
        var endDate = dates.formated2;
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
                        <img src="`+ posterPath +`" width="300" height="272" class="listitem-poster" alt="imageAlt">
                    </div>
                    <div class="col-sm-6">
                        <div class="row listitem-row">
                            <h2>`+chainName + ": " + view +`</h2>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Client:&nbsp;</span>
                            <p class="listitem-overview">`+clientName+`</p>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Rating:&nbsp;</span>
                            <p class="listitem-overview">`+rating+`</p>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Price:&nbsp;</span> <span>`+price+`$</span>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Address:&nbsp;</span> <span>`+address+`</span>
                        </div>
                        <br> <!-- COMBAK make this better never since we care and we don't have time -->
                        <div class="row listitem-row">
                            <h3>Booking Information</h3>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Start Date:&nbsp;</span> <span id=listitem-sdate>`+startDate+`</span>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">End Date:&nbsp;</span> <span id=listitem-edate>`+endDate+`</span>
                        </div>
                        <div class="row listitem-row">
                            <!-- COMBAK: add onlclick for the buttons -->
                            <button id="add-remove-btn-`+id+`" type="button" class="btn btn-primary">Rent</i></button>
                            <button id="remove-btn-`+id+`" type="button" class="btn btn-primary">Cancel</i></button>
                        </div>
                    </div>
                </div>
            </div>`;
        $("#list-items-div").append(html);
        changeClickToAdd(id);
        changeClickToRemove(id);
    }

}
