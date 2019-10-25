function redirect(event) {
    window.location.replace(event.data.path);
}

function changeClickToAdd(id) {
    $("#add-remove-btn-"+id).off('click');
    $("#add-remove-btn-"+id).text("Cancel");
    $("#add-remove-btn-"+id).click({id_param: id}, cancelRoom);
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

  var reqURL = apiURL + "/bookingRenting/client/cancelBooking/" + event.data.id_param;
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
      getList();
  });

  addRequest.fail(function (response) {
      console.log("fail");

      alert(response.responseJSON.message);
  });
}

function renderList(response) {
    console.log(response);
    $("#list-items-div").empty();


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
        var dates = convertDates(currentResult.startDate, currentResult.endDate);
        var startDate = dates.formated1;
        var endDate = dates.formated2;
        var anchor = "/showdetails.html?from=mylist&id=" + id + "&start=" + startDate + "&end=" + endDate + "&booked=true&bid=" + currentResult.bookingID;
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
            <div id="wrapper-`+currentResult.bookingID+`" class="container listitem-container">
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
                            <button id="add-remove-btn-`+currentResult.bookingID+`" type="button" class="btn btn-primary">Cancel</i></button>
                        </div>
                    </div>
                </div>
            </div>`;
        $("#list-items-div").append(html);
        changeClickToAdd(currentResult.bookingID);
    }

}

function getList() {

    console.log("getIds");
    var session = findSessionCookie();

    console.log(session);
    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var reqURL = apiURL + "/bookingRenting/client/myBookings";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "GET",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
    }

    var searchRequest = $.ajax(settings);

    searchRequest.done(function (response) {
        console.log("success");
        console.log(response);
        $("#loading-component").hide();
        renderList(response);
    });

    searchRequest.fail(function (response) {
        // COMBAK: DO something
        console.log("fail");
        console.log(response);
    });
}
