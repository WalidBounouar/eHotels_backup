var hideBook = false;

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
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
      window.location.replace("/mylist.html");
  });

  addRequest.fail(function (response) {
      console.log("fail");

      alert(response.responseJSON.message);
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

function getDetailDates(){
  if(getURLParameter('start') == "" || getURLParameter('end') == ""){
      var startVal = "";
      var endVal = "";
  } else {
      var startVal = getURLParameter('start').split("/");
      var endVal = getURLParameter('end').split("/");
      startVal = startVal[2] + "-" + startVal[0] + "-" + startVal[1] + " 16:00:00";
      endVal = endVal[2] + "-" + endVal[0] + "-" + endVal[1] + " 11:00:00";
  }
  return {startVal,endVal};
}

function rentRoom() {

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var clientid = $("#inputClient").val();

    var dates = getDetailDates();

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

    if(getURLParameter('start') === null || getURLParameter('end') === null){
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

    var dates = getDetailDates();
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

// COMBAK: remove an element if the is no value in response
function loadRoomDetails(id, start, end) {

    var session = findSessionCookie();

    if(session == "") {
        window.location.replace("/index.html");
    }

    var reqURL = apiURL + "/rooms/details/" + id;
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "GET",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
    }

    $.ajax(settings).done(function (response) {

        console.log(response);

        //set breadcrumb and title
        var name = response.name;
        $("#show-name-breadcrumb").text(name);
        $("#title").text(name);

        var address = response.streetNumber + " " + response.streetName + ", " + response.city + ", " + response.state;
        var posterPath = "media/no_poster.png";
        if(response.view == "seaside") {
            posterPath = "media/seaview.jpg";
        } else {
            posterPath = "media/mountainview.jpg";
        }

        //set Overview
        $("#listitem-sdate").text(start);

        $("#listitem-edate").text(end);

        $("#listitem-price").text(response.price);

        $("#listitem-rating").text(response.starRating);

        $("#listitem-capacity").text(response.capacity);

        $("#listitem-extendable").text(response.extendable);

        $("#listitem-view").text(response.view);

        $("#listitem-address").text(address);

        var permlevel = findPermCookie();
        if(permlevel === "EMPLOYEE"){
          $("#rent-btn").show();
          $("#rent-btn").off('click');
          $("#rent-btn").click({id_param: response.roomID}, bookRoom);
          $("#add-remove-btn").hide();
        }

        if(hideBook){
          $("#add-remove-btn").hide();
          $("#remove-btn").show();
          $("#remove-btn").off('click');
          $("#remove-btn").text("Cancel");
          $("#remove-btn").click({id_param: getURLParameter('bid')}, cancelRoom);
        } else {
          $("#add-remove-btn").off('click');
          $("#add-remove-btn").text("Book");
          $("#add-remove-btn").click({id_param: response.roomID}, bookRoom);
        }


        //set poster
        $("#poster").attr("src", posterPath);

        //set backdrop
        var bPath = response.backdrop_path;
        if(bPath == "" || bPath == undefined || bPath == null) {
            $("#backdrop").remove();
        } else {
            var backdropPath = "https://image.tmdb.org/t/p/w1280" + response.backdrop_path;
            $("#backdrop").attr("src", backdropPath);
        }

    });
}

function setBreadcrumb() {
    var source = getURLParameter('from');
    var href = "";
    var text = "";
    if(source == "search") {
        href="/search.html";
        text = "Search";
    } else if (source == "discover") {
        href="/discover.html";
        text = "Discover";
    } else { //actually only for my list, but if someone "hack" url, I don't mind sending them to list
    href="/mylist.html";
    text = "My Bookings";
    hideBook = true;
    }
    $("#last-page-breadcrumb").attr("href", href);
    $("#last-page-breadcrumb").text(text);
}

function renderDetails(id, start, end) {

    validateSession();
    setBreadcrumb();

    //we do the ajax requests one after another. The timers were bothering merge
    loadRoomDetails(id, start, end);

}
