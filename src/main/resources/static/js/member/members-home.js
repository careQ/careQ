var stompClient = null;
function connect() {
    var socket = new SockJS('/ws/queue');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnectSuccess);
}

function onConnectSuccess(frame) {
    console.log('Connected:', frame);

    $('.carousel-item').each(function () {
        var subjectId = $(this).find('.subjectId').val();
        var hospitalId = $(this).find('.hospitalId').val();
        onConnect(subjectId, hospitalId);
    });
}

function onConnect(subjectId, hospitalId) {
    console.log('Connected to', subjectId, hospitalId);
    stompClient.subscribe('/topic/queues/main/subjects/' + subjectId + '/hospitals/' + hospitalId, function (message) {
        console.log(JSON.parse(message.body));
        if ((JSON.parse(message.body).userType == 'ADMIN') && (JSON.parse(message.body).status == 'RELOAD')) {
            window.location.reload();
        }
    });
}

$(function () {
    connect();
});