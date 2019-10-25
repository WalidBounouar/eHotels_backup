var values = [];

function continueSearch() {
    var clientName = $("#inputClientName").val();

    var filteredVals = [];

    for(var i = 0; i < values.length; i++){

      if(clientName === ""){
        checked = true;
      } else {
        checked = values[i].firstName.toLowerCase().includes(clientName.toLowerCase()) || values[i].lastName.toLowerCase().includes(clientName.toLowerCase())
                || values[i].middleName.toLowerCase().includes(clientName.toLowerCase());
      }

      if(checked){
         filteredVals.push(values[i]);
      }
    }
    renderSearch(filteredVals);
}

function redirect(event) {
    window.location.replace(event.data.path);
}

function changeEdit(id) {
    $("#edit-"+id).off('click');
    $("#edit-"+id).text("Edit");
    $("#edit-"+id).click({id_param: id}, editClient);
}

function changeClickToAdd(id) {
    $("#add-remove-btn-"+id).off('click');
    $("#add-remove-btn-"+id).text("Delete");
    $("#add-remove-btn-"+id).click({id_param: id}, deleteClient);
}

function editClient(event){
  event.stopPropagation();
  window.location.replace("/editclient.html?id="+event.data.id_param);
}

function deleteClient(event){
  event.stopPropagation();

  var session = findSessionCookie();

  // COMBAK: Probably more elegant way to handle.
  // COMBAK: Need to test this
  if(session == "") {
      window.location.replace("/index.html");
  }

  var reqURL = apiURL + "/users/employee/deleteClient/" + event.data.id_param;
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
      alert("Client deleted successfully!");
      window.location.replace("/viewclients.html");
  });

  addRequest.fail(function (response) {
      console.log("fail");
	  
	  if(response.status === 401) {
		  alert("Can't delete. Client is responsible for booking/renting: \n"
			  + formatList(response.responseJSON));
       	  return;
	  }

      alert(response);
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

function loadClients(){
  var session = findSessionCookie();

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
    var headingValue = "Clients:";
    $("#search-terms").text(headingValue)

    //feedback in case of no results
    if(results.length == 0) {
        $("#list-items-div").append('<p> No results found </p>');
        return;
    }

    for (var i = 0; i < results.length; i++) {
        //set up all variable first
        var currentResult = results[i];
        var id = currentResult.id;
        var anchor = "/editclient.html?id=" + id;
        var clientName = currentResult.firstName + " " + currentResult.middleName + " " + currentResult.lastName;
        var zip = currentResult.zip;

        var address = currentResult.streetNumber + " " + currentResult.streetName + ", " + currentResult.city + ", " + currentResult.state;

        //generate html
        var html = `
            <div id="wrapper-`+id+`" class="container listitem-container">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="row listitem-row">
                            <a href="`+anchor+`">
                            <h2>`+clientName + `</h2>
                            </a>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Address:&nbsp;</span> <span>`+address+`</span>
                        </div>
                        <div class="row listitem-row">
                            <span class="listitem-label">Zip:&nbsp;</span> <span>`+zip+`</span>
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
        output = output + "- " + element.chainName + ", " + element.state + ", " + element.city + ", " + element.zip 
			+ ", room " + element.roomNumber + ", start: " + element.startDate.split(" ")[0] + "\n";
    });

    return output;

}
