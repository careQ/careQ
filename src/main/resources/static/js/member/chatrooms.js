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

            if(data.length == 0){
                chatrooms.classList.add("flex", "flex-col", "items-center", "justify-center");
                chatrooms.innerHTML = "<svg class='w-[30] h-[30] fill-[#808080] mb-2' viewBox='0 0 576 512' xmlns='http://www.w3.org/2000/svg'>" +
                    "<path d='M0 64C0 28.7 28.7 0 64 0H224V128c0 17.7 14.3 32 32 32H384v38.6C310.1 219.5 256 287.4 256 368c0 59.1 29.1 111.3 73.7 143.3c-3.2 .5-6.4 .7-9.7 .7H64c-35.3 0-64-28.7-64-64V64zm384 64H256V0L384 128zm48 96a144 144 0 1 1 0 288 144 144 0 1 1 0-288zm0 240a24 24 0 1 0 0-48 24 24 0 1 0 0 48zM368 321.6V328c0 8.8 7.2 16 16 16s16-7.2 16-16v-6.4c0-5.3 4.3-9.6 9.6-9.6h40.5c7.7 0 13.9 6.2 13.9 13.9c0 5.2-2.9 9.9-7.4 12.3l-32 16.8c-5.3 2.8-8.6 8.2-8.6 14.2V384c0 8.8 7.2 16 16 16s16-7.2 16-16v-5.1l23.5-12.3c15.1-7.9 24.5-23.6 24.5-40.6c0-25.4-20.6-45.9-45.9-45.9H409.6c-23 0-41.6 18.6-41.6 41.6z'></path>" +
                    "</svg><span class='text-sm text-[#808080]'>채팅 내역이 없습니다.</span>";
            }

            data.forEach(function (chat) {
                var row = `<tr>
                                   <div class='flex justify-between text-center py-2 mt-2'>
                                        <div class='flex inline-block text-[#121212] text-left ml-3' style='font-size: 0.8rem;'>${chat.hospitalName} ${chat.subjectName}</div>
                                            <a href='/members/chatrooms/`+chat.id+`' class="flex inline-block mr-2">
                                                <button>
                                                    <svg class="w-6 h-6 fill-[#78c4ba]" viewBox="0 0 576 512" xmlns="http://www.w3.org/2000/svg">
                                                        <path d="M352 96l64 0c17.7 0 32 14.3 32 32l0 256c0 17.7-14.3 32-32 32l-64 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l64 0c53 0 96-43 96-96l0-256c0-53-43-96-96-96l-64 0c-17.7 0-32 14.3-32 32s14.3 32 32 32zm-9.4 182.6c12.5-12.5 12.5-32.8 0-45.3l-128-128c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L242.7 224 32 224c-17.7 0-32 14.3-32 32s14.3 32 32 32l210.7 0-73.4 73.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0l128-128z"></path>
                                                    </svg>
                                                </button>
                                            </a>
                                    </div>
                                    <hr class="w-60" style="color: #F6F6F6;">
                                    </tr>`;
                chatrooms.innerHTML += row;
            });

        }
    };
}