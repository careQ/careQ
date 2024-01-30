function searchChatroom(form) {

    form.subjectName.value = form.subjectName.value.trim();
    form.hospitalName.value = form.hospitalName.value.trim();

    if (form.subjectName.value.length == 0) {
        toastWarning('진료과목을 입력해주세요.');
        form.subjectName.focus();
        return;
    }

    if (form.hospitalName.value.length == 0) {
        toastWarning('병원명을 입력해주세요.');
        form.hospitalName.focus();
        return;
    }

    form.submit(); // 폼 발송
}