$(document).ready(function() {
    getCharts('');
});

function getCharts(name){
    const xhttp = new XMLHttpRequest();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    xhttp.open("GET", window.location.pathname + "?name=" + name);
    xhttp.setRequestHeader(header, token);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var registerCharts = document.getElementById("t_body");
            registerCharts.innerHTML = "";

            data.forEach(function (registerChart, index) {
                var time = getDate(registerChart.time);

                var row = `<tr>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">${index+1}</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">${registerChart.memberUsername}</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">`+time+`</td>`;

                if (registerChart.status == 'WAITING'){
                    row += `<td class="border-t border-b py-2 px-4 text-sm text-center">
                            <div class="flex justify-center">
                                <div class="text-[#4392F9] py-1 px-2 rounded" style="background-color: white; border: 1px solid #4392F9; width: 90px;">대기 중</div>
                            </div>
                        </td>
                        <td class="border-t border-b py-2 px-4 text-sm text-center">
                        <div class="flex flex-row justify-center">
                            <button class="start-btn text-white py-1 px-2 rounded mr-2" data-member-id="${registerChart.memberId}" style="background-color: #4392F9; width: 90px;">진료 시작</button>
                            <button class="cancel-btn text-white py-1 px-2 rounded" data-member-id="${registerChart.memberId}" style="background-color: #858587; width: 90px;">줄서기 취소</button>
                        </div>
                    </td>
                </tr>`;
                } else if (registerChart.status == 'ENTER') {
                    row += `<td class="border-t border-b py-2 px-4 text-sm text-center">
                                        <div class="flex justify-center">
                                            <div class="text-[#F83758] py-1 px-2 rounded" style="background-color: white; border: 1px solid #F83758; width: 90px;">진료 중</div>
                                        </div>
                                     </td>
                        <td class="border-t border-b py-2 px-4 text-sm text-center">
                        <div class="flex justify-center">
                            <button class="complete-btn text-white py-1 px-2 rounded" data-member-id="${registerChart.memberId}" style="background-color: #F83758; width: 90px;">진료 완료</button>
                        </div>
                    </td>
                </tr>`;
                }
                registerCharts.innerHTML += row;
            });
        }
    };
}

function getDate(date){
    var d = new Date(date);

    var year = d.getFullYear();
    var month = ('0' + (d.getMonth() + 1)).slice(-2);
    var day = ('0' + d.getDate()).slice(-2);

    var hours = ('0' + d.getHours()).slice(-2);
    var minutes = ('0' + d.getMinutes()).slice(-2);
    var seconds = ('0' + d.getSeconds()).slice(-2);
    return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
}

function toggleModal() {
    const modal = document.getElementById('modalContainer');
    modal.classList.toggle('hidden');
    document.getElementById('onsiteUser').value = '';

}

$(function () {
    $( "#modalOpenButton" ).click(function() { toggleModal(); });
    $( "#modalCloseButton" ).click(function() { toggleModal(); });
});

var stompClient = null;

function connect() {
    var socket = new SockJS('/ws/queue');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/queues/main/subjects/'+document.getElementById('subjectId').value
            +'/hospitals/'+document.getElementById('hospitalId').value, function (message) {
            console.log(JSON.parse(message.body));
            getCharts('');
        });
    });
}

function eachConnect(memberId, status) {
    console.log('eachConnected: ');
    stompClient.subscribe('/topic/queues/members/'+memberId+'/subjects/'+document.getElementById('subjectId').value
        +'/hospitals/'+document.getElementById('hospitalId').value, function (message) {
        console.log(JSON.parse(message.body));
        getCharts('');
    });
    send(memberId, status);
}

function send(memberId, status) {
    let msg = {
        'userType': 'ADMIN',
        'memberId': memberId,
        'hospitalId': document.getElementById('hospitalId').value,
        'subjectId': document.getElementById('subjectId').value,
        'status': status
    }

    stompClient.send("/app/queue", {}, JSON.stringify(msg));
}

function updateWaiting() {
    let msg = {
        'userType': 'ADMIN',
        'hospitalId': document.getElementById('hospitalId').value,
        'subjectId': document.getElementById('subjectId').value,
        'status': 'RELOAD'
    }

    stompClient.send("/app/queue/main", {}, JSON.stringify(msg));
}

$(function () {
    $(document).ready(function(){connect();})
    $(document).on("click", ".start-btn", function() {
        var memberId = $(this).data('member-id');
        eachConnect(memberId, "ENTER");
    });
    $(document).on("click", ".cancel-btn", function() {
        var memberId = $(this).data('member-id');
        eachConnect(memberId, "CANCEL");
        setTimeout(function () {
            updateWaiting();
        }, 1000);
    });
    $(document).on("click", ".complete-btn", function() {
        var memberId = $(this).data('member-id');
        eachConnect(memberId, "COMPLETE");
        setTimeout(function () {
            updateWaiting();
        }, 1000);
    });
});