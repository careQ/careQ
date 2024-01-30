var stompClient = null;

function connect() {
    var socket = new SockJS('/ws/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chatrooms/'+window.location.pathname.split('/')[3], function (message) {
            if(JSON.parse(message.body).userType == 'ADMIN'){
                addAdminMessage(JSON.parse(message.body).content);
            }
        });
    });
}

function send() {
    var content = $("#message-input").val();

    if (content != "") {
        let msg = {
            'type': 'TALK',
            'userType': 'MEMBER',
            'chatId': parseInt(window.location.pathname.split('/')[3]),
            'sender': document.getElementById("memberName").value,
            'content': content
        }

        if (content != null) {
            stompClient.send("/app/message", {}, JSON.stringify(msg));
        }

        document.getElementById("message-input").value = '';
        addMemberMessage(content);
    }
}

//관리자 메세지 추가
function addAdminMessage(content) {
    const chatContainer = document.getElementById("chat-container");
    const messageDiv = document.createElement("div");
    messageDiv.className = "flex mb-2 w-56"; // 메시지를 나타내는 messageDiv 요소에 CSS 클래스를 추가
    messageDiv.innerHTML = `
                   <div class="flex mb-2 w-56">
                        <div class="bg-[#78C4BA] p-2 rounded-lg max-w-[70%] text-Xs relative">
                          <p class="text-gray-500 font-semibold mb-1">관리자:</p>
                              <p>${content}</p>
                           </div>
                       </div>`;

    chatContainer.appendChild(messageDiv);
}

// 회원 메시지를 추가
function addMemberMessage(content) {
    const chatContainer = document.getElementById("chat-container");
    const messageDiv = document.createElement("div");
    messageDiv.className = "flex mb-2 w-56 justify-end"; // css 클래스 추가
    messageDiv.innerHTML = `
                  <div class="bg-[#F6F6F6] p-2 rounded-lg max-w-[70%] text-xs relative" style="text-align: right;"> <!--오른쪽 정렬-->
                        <p class="text-gray-500 font-semibold mb-1" style="text-align: right;">회원:</p>
                        <p>${content}</p>
                   </div>`;

    chatContainer.appendChild(messageDiv);
}

// DB 메세지 불러올 때 날짜 구분
var date = '0000 / 00 / 00';
function updateDateValue(newDate) {
    if (newDate != date){
        date = newDate;
        const showDate = document.getElementById("date");
        const inner = document.createElement("p");
        inner.className = "text-xs";
        inner.textContent = date;

        showDate.appendChild(inner);
    }
}

// 메세지 보낼 때 날짜 구분
function addDate(lastDate){
    var d = new Date();

    var year = d.getFullYear();
    var month = ('0' + (d.getMonth() + 1)).slice(-2);
    var day = ('0' + d.getDate()).slice(-2);
    var currentDate = year + " / " + month + " / " + day;

    if ((lastDate != currentDate)||(lastDate == null)){
        const showDate = document.getElementById("todayDate");
        const inner = document.createElement("p");
        inner.className = "text-xs";
        inner.textContent = currentDate;

        showDate.appendChild(inner);
    }
}

$(function () {
    $(document).ready(function(){connect();})
    $( "#send-button" ).click(function() { send(); });
});