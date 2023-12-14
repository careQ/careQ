package com.reve.careQ.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class TempPasswordMail {

    private final JavaMailSender javaMailSender;
    private String ePw;

    public void sendSimpleMessage(String to, String pw) {
        this.ePw = pw;

        MimeMessage message = createMessage(to);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            handleMailException(e, "이메일 전송 에러");
        }
    }

    private MimeMessage createMessage(String to) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject("careQ 임시 비밀번호");
            String content = buildEmailContent();
            message.setText(content, "utf-8", "html");
            message.setFrom(new InternetAddress("careq2427@gmail.com", "careQ_Official"));
        } catch (UnsupportedEncodingException | MessagingException e) {
            handleMailException(e, "이메일 생성 에러");
        }

        return message;
    }

    private String buildEmailContent() {
        return "<div style='margin:100px;'>" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>" +
                "<h3 style='color:#78C4BA;'>재설정된 임시 비밀번호입니다.</h3>" +
                "<div style='font-size:130%'>" +
                "임시 비밀번호 : <strong>" + ePw + "</strong><div><br/> " +
                "</div>";
    }

    private void handleMailException(Exception e, String errorMessage) {
        e.printStackTrace();
        throw new EmailException(errorMessage);
    }
}
