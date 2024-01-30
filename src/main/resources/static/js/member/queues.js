var stompClient = null;
function connect() {
    var socket = new SockJS('/ws/queue');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/queues/main/subjects/'+window.location.pathname.split('/')[3]
            +'/hospitals/'+window.location.pathname.split('/')[5], function (message) {
            console.log(JSON.parse(message.body));
            if((JSON.parse(message.body).userType == 'ADMIN') && (JSON.parse(message.body).status == 'RELOAD')){
                window.location.reload();
            }
        });
        stompClient.subscribe('/topic/queues/members/'+document.getElementById('getMemberId').value
            +'/subjects/'+window.location.pathname.split('/')[3]
            +'/hospitals/'+window.location.pathname.split('/')[5], function (message) {
            if((JSON.parse(message.body).userType == 'ADMIN')){
                console.log(JSON.parse(message.body));
                changeStatus(JSON.parse(message.body).status);
            }
        });
    });
}
function changeStatus(status){
    if (status == 'ENTER'){
        var statusBtn = document.getElementById("status-btn");
        statusBtn.innerHTML = "";
        statusBtn.innerHTML = `<div class="flex items-center justify-center w-64 mb-6"
                     style="background-color: #4392F9; padding: 5px 15px; border-radius: 5px;">
                    <span class="text-white" style="font-size: 15px;">진료 중</span>
                </div>`;
    } else if (status == 'CANCEL'){
        window.location.reload();
    } else if (status == 'COMPLETE'){
        window.location.href = window.location.origin + "/members";
    }
}

function send(memberId, status) {
    let msg = {
        'userType': 'MEMBER',
        'memberId': memberId,
        'hospitalId': window.location.pathname.split('/')[5],
        'subjectId': window.location.pathname.split('/')[3],
        'status': status
    }

    stompClient.send("/app/queue/main", {}, JSON.stringify(msg));
}

$(function () {
    var memberId = document.getElementById('getMemberId').value;
    $(document).ready(function(){connect();})
    $( "#register-btn" ).click(function() {
        send(memberId, 'WAITING'); });
    $( "#cancel-btn" ).click(function() {
        send(memberId, 'CANCEL'); });
});