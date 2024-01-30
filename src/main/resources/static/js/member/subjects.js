$(document).ready = loadSubjects("");

function loadSubjects(name){
    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", "/members/subjects?name="+name);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var tbody = document.getElementById("t_body");
            tbody.innerHTML = "";

            data.forEach(function (subject) {
                var row = "<tr><td><a href='/members/subjects/"+subject.id+"/hospitals'><button class='w-64 rounded-lg bg-[#f4f4f4] text-center p-2 mt-2'>";
                row += "<div id='subject' class='text-[#121212] text-left ml-3' style='font-size: 0.8rem;'>" + subject.name + "</div>";
                row += "</button></a></td></tr>";
                tbody.innerHTML += row;
            });
        }
    };
}