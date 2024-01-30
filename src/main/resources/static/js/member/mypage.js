function checkPassword(password){
    // 비밀번호가 올바른지 체크
    password = password.trim();

    if (password.length == 0) {
        $('#password').focus();
        toastWarning('비밀번호를 입력해주세요.');
        return;
    }
    if (password.length < 4) {
        toastWarning('비밀번호를 4자 이상 입력해주세요.');
        $('#password').focus();
        return;
    }

    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", "/members/mypage?password="+password);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var pwInfo = document.getElementById("pwInfo");

            if (data == true){
                pwInfo.innerHTML = "";
                $(".change").removeAttr("disabled");
                $(".currentpw").prop("disabled", true);
            } else{
                $(".change").prop("disabled", true);
                pwInfo.innerHTML =`<div class="text-[#F83758] text-xs">비밀번호가 올바르지 않습니다.</div>
                            <div class="flex flex-row font-normal text-[#F83758] text-xs">비밀번호를&nbsp;
                            <div class="font-semibold">다시 입력</div>해주세요.</div>`;
            }
        }
    };
}

function changeUsername(username, password){
    // 아이디가 올바른지 체크
    username = username.trim();

    if (username.length == 0) {
        toastWarning('아이디를 입력해주세요.');
        $('#username').focus();
        return;
    }
    if (username.length < 8) {
        toastWarning('아이디를 8자 이상 입력해주세요.');
        $('#username').focus();
        return;
    }

    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", "/members/mypage?username="+ username + "&password="+ password);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var usernameInfo = document.getElementById("usernameInfo");
            usernameInfo.innerHTML = "";

            if (data == true){
                usernameInfo.innerHTML =`
                            <div class="flex flex-row font-semibold text-[#4392F9] text-xs"> ${username}
                            <div class="font-normal">으로 변경되었습니다.</div></div>`;
                $('.input').val('');
                $(".currentpw").removeAttr("disabled");
                $(".change").prop("disabled", true);
            } else if (data == false && username === $('#memberUsername').val() ) {
                usernameInfo.innerHTML =`<div class="flex flex-row font-normal text-[#F83758] text-xs">현재 아이디와&nbsp;
                            <div class="font-semibold">동일</div>합니다.</div>
                            <div class="text-[#F83758] text-xs">새로운 아이디를 다시 입력해주세요.</div>`;
            } else {
                usernameInfo.innerHTML =`<div class="flex flex-row font-normal text-[#F83758] text-xs">아이디가&nbsp;
                            <div class="font-semibold">중복</div>됩니다.</div>
                            <div class="text-[#F83758] text-xs">새로운 아이디를 다시 입력해주세요.</div>`;
            }
        };
    }
}

function changePassword(newpassword){
    // 비밀번호가 올바른지 체크
    newpassword = newpassword.trim();

    if (newpassword.length == 0) {
        $('#newpassword').focus();
        toastWarning('비밀번호를 입력해주세요.');
        return;
    }
    if (newpassword.length < 4) {
        toastWarning('비밀번호를 4자 이상 입력해주세요.');
        $('#newpassword').focus();
        return;
    }
    const xhttp = new XMLHttpRequest();

    xhttp.open("GET", "/members/mypage?newpassword="+ newpassword);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var data = JSON.parse(this.response);

            var newpwInfo = document.getElementById("newpwInfo");
            newpwInfo.innerHTML = "";

            if (data == true){
                newpwInfo.innerHTML =`
                            <div class="flex flex-row font-normal text-[#4392F9] text-xs">비밀번호가 변경되었습니다.</div>`;
                $('.input').val('');
                $(".currentpw").removeAttr("disabled");
                $(".change").prop("disabled", true);
            } else if (data == false) {
                newpwInfo.innerHTML =`<div class="flex flex-row font-normal text-[#F83758] text-xs">현재 비밀번호와&nbsp;
                            <div class="font-semibold">동일</div>합니다.</div>
                            <div class="text-[#F83758] text-xs">새로운 비밀번호를 다시 입력해주세요.</div>`;
            }
        };
    }
}