function LoginForm__submit(form) {
    // username 이(가) 올바른지 체크
    form.username.value = form.username.value.trim(); // 입력란의 입력값에 있을지 모르는 좌우공백제거

    if (form.username.value.length == 0) {
        toastWarning('아이디를 입력해주세요.');
        form.username.focus();
        return;
    }

    if (form.username.value.length < 8) {
        toastWarning('아이디를 8자 이상 입력해주세요.');
        form.username.focus();
        return;
    }

    // password 이(가) 올바른지 체크
    form.password.value = form.password.value.trim();

    if (form.password.value.length == 0) {
        form.password.focus();
        toastWarning('비밀번호를 입력해주세요.');
        return;
    }

    if (form.password.value.length < 4) {
        toastWarning('비밀번호를 4자 이상 입력해주세요.');
        form.password.focus();
        return;
    }

    form.submit(); // 폼 발송
}