function checkForm(form){
    // username 이(가) 올바른지 체크
    form.username.value = form.username.value.trim(); // 입력란의 입력값에 있을지 모르는 좌우공백제거

    if (form.username.value == 0) {
        toastWarning('아이디를 입력해주세요.');
        form.username.focus();
        return;
    }

    if (form.username.value.length < 8) {
        toastWarning('아이디를 8자 이상 입력해주세요.');
        form.username.focus();
        return;
    }

    // 이메일이 올바른지 체크
    form.email.value = form.email.value.trim();
    const emailRegExp = new RegExp(/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i);

    if (!emailRegExp.test(form.email.value)) {
        toastWarning('이메일 형식에 맞춰 다시 입력해주세요.');
        form.email.focus();
        return;
    }

    form.submit();
}