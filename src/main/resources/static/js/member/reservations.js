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
    var memberId = document.getElementById('getMemberId').value;
    $(document).ready(function(){connect();})
    $( "#btn-reservation" ).click(function() {
        send(memberId); });
});

var selectedTime = null; // 선택한 시간을 저장할 변수

function selectTime(time, button) {
    var selectedDate = document.getElementById("startDateInput").value;

    // 날짜를 선택하지 않은 경우 경고 표시
    if (!selectedDate) {
        alert("날짜를 선택하세요.");
    } else {
        selectedTime = time;
        console.log("선택한 날짜:", selectedDate);
        console.log("선택한 시간:", selectedTime);

        // 모든 버튼의 배경색을 초기화, 예약하기 버튼은 제외
        var buttons = document.querySelectorAll("button:not(#btn-reservation)");
        buttons.forEach(function (btn) {
            btn.style.backgroundColor = "#FFFFFF";
        });

        // 선택한 버튼의 배경색 변경
        button.style.backgroundColor = "#78C4BA";

        // 선택한 날짜와 시간을 숨김 필드에 설정
        document.getElementById("selectedDate").value = selectedDate;
        document.getElementById("selectedTime").value = selectedTime;
    }
}

// 예약하기 버튼 클릭 시 예약을 처리하는 함수
document.getElementById("btn-reservation").addEventListener("click", function (event) {
    var selectedDate = document.getElementById("startDateInput").value;

    // 날짜와 시간이 선택되지 않은 경우 경고 표시
    if (!selectedDate || !selectedTime) {
        alert("날짜와 시간을 선택하세요.");

        // 폼 제출을 중단
        event.preventDefault();
    } else {
        // 폼을 제출
        document.getElementById("reservation-form").submit();
    }
});

// 오늘 날짜 구하기
var today = new Date();
today.setDate(today.getDate() + 1);

// 날짜를 yyyy-MM-dd 형식으로 변환
var yyyy = today.getFullYear();
var mm = today.getMonth() + 1;
var dd = today.getDate();

if (mm < 10) {
    mm = '0' + mm;
}

if (dd < 10) {
    dd = '0' + dd;
}

var minDate = yyyy + '-' + mm + '-' + dd;

// 최소 날짜를 설정
document.getElementById("startDateInput").setAttribute("min", minDate);