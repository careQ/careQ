function JoinFormDto__submit(form) {
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
    form.password.value = form.password.value.trim(); // 입력란의 입력값에 있을지 모르는 좌우공백제거

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

    form.passwordValidation.value = form.passwordValidation.value.trim();

    if (form.passwordValidation.value.length == 0) {
        form.passwordValidation.focus();
        toastWarning('비밀번호 확인을 입력해주세요.');
        return;
    }

    if (form.passwordValidation.value !== form.password.value) {
        form.passwordValidation.focus();
        toastWarning('비밀번호가 일치하지 않습니다.');
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

    form.submit(); // 폼 발송
}