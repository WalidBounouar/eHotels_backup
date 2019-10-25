var renting;

function loadViews() {

    var session = findSessionCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    var reqURL = apiURL + "/rooms/capacityPerRoom";
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
        renderSearch(response);
    });

    searchRequest.fail(function (response) {
        // COMBAK: DO something
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.error);
    });
}

function redirect(event) {
    window.location.replace(event.data.path);
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
        var number = currentResult.capacity;
        var roomNum = currentResult.roomNumber;
        var address = currentResult.streetNumber + " " + currentResult.streetName + ", " + currentResult.city + ", " + currentResult.state;

        //generate html
        var html = `
            <div id="wrapper" class="container listitem-container">
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row listitem-row">
                            <h2>`+address + `</h2>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">RoomNumber:&nbsp;</span>
                            <p class="listitem-overview">`+roomNum+`</p>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Capacity:&nbsp;</span>
                            <p class="listitem-overview">`+number+`</p>
                        </div>
                    </div>
                </div>
            </div>`;
        $("#list-items-div").append(html);
    }

}
