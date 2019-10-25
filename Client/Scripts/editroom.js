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

    var roomID = getURLParameter('id');

    var reqURL = apiURL + "/rooms/details/" + roomID;
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

        loadHotels(response.hotelID);
        //actual content
        console.log(response);
        //NOTE: This is a hack to help make some features easy
        $("#roomNumber-input").val(response.roomNumber);
        $("#capacity-input").val(response.capacity);
        $("#price-input").val(response.price);
        $("#extendable-input").prop('checked', response.extendable);
        $("#view-input option[value=" + response.view + "]").attr("selected", "selected");
        $("#view-input").val(response.view);
        $("#price-input").val(response.price);


        //generate role list
        for (let i = 0; i < response.amenities.length; i++) {
            var phone = response.amenities[i];
            var html = `
                <div>
                    <span class="listitem-overview">- ` + phone + ` &nbsp;
                    <button type="button" class="btn btn-primary" onclick="deleteAmenity('` + phone + `')">Delete</button>
                    </span>
                </div>
            `;
            $("#amenity-container").append(html);
        }

        for (let i = 0; i < response.issues.length; i++) {
            var phone = response.issues[i];
            var html = `
                <div>
                    <span class="listitem-overview">- ` + phone + ` &nbsp;
                    <button type="button" class="btn btn-primary" onclick="deleteIssue('` + phone + `')">Delete</button>
                    </span>
                </div>
            `;
            $("#issue-container").append(html);
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

function addAmenity() {

    var newPhone = $("#amenity-input").val();

    if(newPhone === "" || newPhone === null || newPhone === undefined) {
        alert("Please input an amenity");
        return;
    }

    var roomID = getURLParameter("id");
    var session = findSessionCookie();

    var phoneData = {
        ownerID: roomID,
        value: newPhone
    }

    var reqURL = apiURL + "/rooms/employee/addAmenity";
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
        window.location.replace("/editroom.html?id="+roomID);
    });

    putRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });
}

function deleteAmenity(phone) {

    var session = findSessionCookie();
    var roomID = getURLParameter("id");

    var reqURL = apiURL + "/rooms/employee/deleteAmenity?roomID="+roomID+"&amenity="+phone;
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
        window.location.replace("/editroom.html?id="+roomID);
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function addIssue() {

    var newPhone = $("#issue-input").val();

    if(newPhone === "" || newPhone === null || newPhone === undefined) {
        alert("Please input an issue");
        return;
    }

    var roomID = getURLParameter("id");
    var session = findSessionCookie();

    var phoneData = {
        ownerID: roomID,
        value: newPhone
    }

    var reqURL = apiURL + "/rooms/employee/addIssue";
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
        window.location.replace("/editroom.html?id="+roomID);
    });

    putRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });
}

function deleteIssue(phone) {

    var session = findSessionCookie();
    var roomID = getURLParameter("id");

    var reqURL = apiURL + "/rooms/employee/deleteIssue?roomID="+roomID+"&issue="+phone;
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
        window.location.replace("/editroom.html?id="+roomID);
    });

    deleteRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function loadHotels(hotelnum){
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
    $("#hotel-input option[value=" + hotelnum + "]").attr("selected", "selected");
  });
}

function saveChanges() {
    var roomID = getURLParameter("id");

    var roomNum = $("#roomNumber-input").val();
    var capacity = $("#capacity-input").val();
    var price = $("#price-input").val();
    var extendable = $("#extendable-input").is(':checked');
    var view = $("#view-input").val();
    var price = $("#price-input").val();

    var hotelID = $("#hotel-input").val();

    var session = findSessionCookie();

    var employeeData = {
        roomID: roomID,
        hotelID: hotelID,
        price: price,
        capacity: capacity,
        extendable: extendable,
        price: price,
        view: view,
        roomNumber: roomNum
    }

    var reqURL = apiURL + "/rooms/employee/updateRoom";
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
        window.location.replace("/editroom.html?id="+roomID);
    });

    updateRequest.fail(function (response) {
        console.log("fail");
        console.log(response);
        alert(response.responseJSON.message);
    });

}

function resetValues() {
  var hotelID = getURLParameter("id");
  window.location.replace("/editroom.html?id="+hotelID);
}

function deleteRoom() {

    var session = findSessionCookie();
    var roomID = getURLParameter("id");

    var reqURL = apiURL + "/rooms/employee/deleteRoom/"+roomID;
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
          alert("Cannot delete. Room has active bookings/rentings! \n" + formatList(response.responseJSON));
        }

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
