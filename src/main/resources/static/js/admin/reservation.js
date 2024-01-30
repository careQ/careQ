var stompClient = null;

function connect() {
    var socket = new SockJS('/ws/reservation');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/reservations/main/subjects/'+document.getElementById('subjectId').value
            +'/hospitals/'+document.getElementById('hospitalId').value, function (message) {
            console.log(JSON.parse(message.body));
            window.location.reload();
        });
    });
}

function eachConnect(memberId) {
    stompClient.subscribe('/topic/reservations/members/'+memberId+'/subjects/'+document.getElementById('subjectId').value
        +'/hospitals/'+document.getElementById('hospitalId').value, function (message) {
        console.log(JSON.parse(message.body));
        window.location.reload();
    });
    send(memberId);
}

function send(memberId) {
    let msg = {
        'userType': 'ADMIN',
        'memberId': memberId,
        'hospitalId': document.getElementById('hospitalId').value,
        'subjectId': document.getElementById('subjectId').value,
        'status': 'CONFIRMED'
    }

    stompClient.send("/app/reservation", {}, JSON.stringify(msg));
}

$(function () {
    $(document).ready(function(){connect();})
    $( ".confirm-btn" ).click(function() {
        var memberId = $(this).val();
        eachConnect(memberId);});
});