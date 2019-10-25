// COMBAK: change when deployed
var apiURL = "http://localhost:8080/ehotels";

// COMBAK: Not safe, but for small project lets ignore
var ehotelsCookieName = "ehotels_cookie";
var ehotelsPermLevel = "ehotels_perm";

function findSessionCookie() {
    var cookies = document.cookie.split(";");
    var session = "";
    for (var i = 0; i < cookies.length; i++) {
        var currentCookie = cookies[i].split("=");
        if(currentCookie[0].trim() == ehotelsCookieName) {
            session = currentCookie[1];
            return session;
        }
    }
    return session;
}

function findPermCookie() {
    var cookies = document.cookie.split(";");
    var perm = "";
    for (var i = 0; i < cookies.length; i++) {
        var currentCookie = cookies[i].split("=");
        if(currentCookie[0].trim() == ehotelsPermLevel) {
            perm = currentCookie[1];
            return perm;
        }
    }
    return perm;
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

function prepareSidebar() {
    document.body.style.display = "none";
    var session = findSessionCookie();
    var permlevel = findPermCookie();

    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }

    if(permlevel === "EMPLOYEE"){
      var navLink = `<a id="rentings-sidenav" class="nav-link sidenav-link" href="searchrentings.html">Search Rentings <i class="fas fa-search"></i></a>
                     <a id="hotels-sidenav" class="nav-link sidenav-link" href="viewhotels.html">View Hotels <i class="fas fa-list-ul"></i></a>
                     <a id="rooms-sidenav" class="nav-link sidenav-link" href="viewrooms.html">View Rooms <i class="fas fa-list-ul"></i></a>
                     <a id="clients-sidenav" class="nav-link sidenav-link" href="viewclients.html">View Clients <i class="fas fa-list-ul"></i></a>
                     <a id="freerooms-sidenav" class="nav-link sidenav-link" href="freerooms.html">View Free Rooms <i class="fas fa-list-ul"></i></a>
                     <a id="rooms-sidenav" class="nav-link sidenav-link" href="capacityrooms.html">View Capacity Per Room <i class="fas fa-list-ul"></i></a>
                     <a id="employees-sidenav" class="nav-link sidenav-link" href="viewemployees.html">View Employees <i class="fas fa-list-ul"></i></a>`
      $("#sidenav").append(navLink);
      $("#list-sidenav").attr("href", "searchbookings.html");
      $("#list-sidenav").html("Search Bookings&nbsp;<i class=\"fas fa-list-ul\"></i>");
    }

    var reqURL = apiURL + "/users/whoAmI";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "GET",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
    }

    var profileRequest = $.ajax(settings);

    profileRequest.done(function (response) {
        console.log("success");
        $("#sidenav-username").text(response.firstName);
        var pictureSrc = "media/" + getProfilePicName(response.photo);
        $("#sidenav-userpic").attr("src", pictureSrc);
        document.body.style.display = "block";
    });

    profileRequest.fail(function (response) {
        // COMBAK: DO something
        console.log("fail");
        console.log(response);
        errorMsg = response.responseJSON.error;
        if(errorMsg == "Invalid Session Token") {
            destroySession();
            window.location.replace("/index.html");
        }
    });
}

function getProfilePicName(index) {
    if(index == 1) {
        return "1_userpic.png";
    } else if (index == 2) {
        return "2_userpic.png";
    } else {
        return "0_userpic.png";
    }
}

function convertDates(start, end){
  var date1 = start.split(" ")[0];
  var date2 = end.split(" ")[0];

  var tmp1 = date1.split("-");
  var tmp2 = date2.split("-");

  var formated1 = tmp1[1] + "/" + tmp1[2] + "/" + tmp1[0];
  var formated2 = tmp2[1] + "/" + tmp2[2] + "/" + tmp2[0];

  return {formated1, formated2};
}

//actually it sets cookie expiration to a past date
function logout() {
    document.cookie = ehotelsCookieName + "=somethingWrong; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    document.cookie = ehotelsPermLevel + "=somethingWrong; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    window.location.replace("/index.html");
    // COMBAK: mayve find a way to confirm logout
}

function destroySession() {
    document.cookie = ehotelsCookieName + "=somethingWrong; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    document.cookie = ehotelsPermLevel + "=somethingWrong; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    window.location.replace("/index.html");
    // COMBAK: mayve find a way to confirm logout
}

function validateSession() {
    var session = findSessionCookie();

    console.log(session);
    // COMBAK: Probably more elegant way to handle.
    // COMBAK: Need to test this
    if(session == "") {
        window.location.replace("/index.html");
    }
}

function checkSession() {
    var session = findSessionCookie();

    var reqURL = apiURL + "/login";
    var settings = {
        "async": true,
        "crossDomain": true,
        "url": reqURL,
        "method": "GET",
        "headers": {authorization: session},
        "dataType": 'json',
        "contentType": 'application/json',
    }

    var profileRequest = $.ajax(settings);

    profileRequest.done(function (response) {
      window.location.replace("/mylist.html");
    });

    profileRequest.fail(function (response) {
      document.cookie = ehotelsCookieName + "=somethingWrong; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    });
}
