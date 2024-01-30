var stompClient = null;
function connect() {
    var socket = new SockJS('/ws/reservation');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/reservations/main/subjects/'+window.location.pathname.split('/')[3]
            +'/hospitals/'+window.location.pathname.split('/')[5], function (message) {
            console.log(JSON.parse(message.body));
        });
        stompClient.subscribe('/topic/reservations/members/'+document.getElementById('getMemberId').value
            +'/subjects/'+window.location.pathname.split('/')[3]
            +'/hospitals/'+window.location.pathname.split('/')[5], function (message) {
            if((JSON.parse(message.body).userType == 'ADMIN')&&(JSON.parse(message.body).status == 'CONFIRMED')){
                console.log(JSON.parse(message.body));
                window.location.href = window.location.href + "/confirm";
            }
        });
    });
}

function send(memberId) {
    let msg = {
        'userType': 'MEMBER',
        'memberId': memberId,
        'hospitalId': window.location.pathname.split('/')[5],
        'subjectId': window.location.pathname.split('/')[3],
        'status': 'PENDING'
    }

    stompClient.send("/app/reservation/main", {}, JSON.stringify(msg));
}

$(function () {
    $(document).ready(function(){connect();})
    $( "#delete-btn" ).click(function() {
        var memberId = document.getElementById('getMemberId').value;
        send(memberId); });
});