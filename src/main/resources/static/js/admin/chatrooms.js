$(document).ready(function() {
    getChats('');
});

function getChats(name){
    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", window.location.pathname + "?name=" + name);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var chatrooms = document.getElementById("t_body");
            chatrooms.innerHTML = "";

            data.forEach(function (chat, index) {
                var createDate = getDate(chat.createDate);
                var modifyDate = getDate(chat.modifyDate);

                var row = `<tr>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">${index+1}</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">${chat.memberUsername}</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">`+modifyDate+`</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">`+createDate+`</td>
                    <td class="border-t border-b py-2 px-4 text-sm text-center">
                        <a href='/admins/chatrooms/`+chat.id+`'>
                            <button class="bg-[#78c4ba] text-white py-1 px-2 rounded">채팅하기</button>
                        </a>
                    </td>
                </tr>`;
                chatrooms.innerHTML += row;
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