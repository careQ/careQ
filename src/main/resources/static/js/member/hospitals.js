$(document).ready = getHospitals('', '', '');

function getCity(state){
    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", window.location.pathname + "?state=" + state);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var selectcity = document.getElementById("city");
            selectcity.innerHTML = "<option value=''>구군 선택</option>";

            data.forEach(function (city) {
                var row = "<option value='" + city + "'>"+ city + "</option>";
                selectcity.innerHTML += row;
            });

        }
    };
}

function getHospitals(state, city, name){
    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", window.location.pathname +"?state=" + state + "&city=" + city + "&name=" + name);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var tbody = document.getElementById("t_body");
            tbody.innerHTML = "";

            data.forEach(function (hospital) {
                var row = "<tr><td><a href='" + window.location.pathname + "/" + hospital.id+ "'><button class='w-64 rounded-lg bg-[#f4f4f4] text-center p-2 mt-2'>";
                row += "<div id='hospital' class='text-[#121212] text-left ml-3' style='font-size: 0.8rem;'>" + hospital.name + "</div>";
                row += "</button></a></td></tr>";
                tbody.innerHTML += row;
            });

        }
    };
}