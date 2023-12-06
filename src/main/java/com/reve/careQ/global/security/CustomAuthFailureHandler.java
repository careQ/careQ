package com.reve.careQ.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = determineErrorMessage(exception);
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        setDefaultFailureUrl("/members/login?error=true&exception=" + errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }

    private String determineErrorMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (isLinkedAccountException(exception)) {
            return "이미 %s와(과) 연동된 계정이 존재합니다. 해당 계정으로 로그인 해주세요.".formatted(exception.getMessage());
        } else if (exception instanceof InternalAuthenticationServiceException) {
            return "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            return "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            return "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof OAuth2AuthenticationException) {
            return "이미 %s와(과) 연동된 계정이 존재합니다. 해당 계정으로 로그인 해주세요.".formatted(exception.getMessage());
        } else {
            return "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }
    }

    private boolean isLinkedAccountException(AuthenticationException exception) {
        return exception.getCause() != null && exception.getCause().getMessage().matches("LinkedAccount");
    }
}
