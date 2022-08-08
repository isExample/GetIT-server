package com.example.getIt.jwt.exception;

import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한이 없이 접근하려 할때 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String exception = (String) request.getAttribute("exception");
        BaseResponseStatus status;
        if(exception == null) {
            status = BaseResponseStatus.NULL_JWT;
            setResponse(response, status);
            return;
        }

        /**
         * 토큰이 만료된 경우 예외처리
         */
        if(exception.equals("ExpiredJwtException")) {
            status = BaseResponseStatus.EXPIRED_JWT_TOKEN;
            setResponse(response, status);
            return;
        }
        if(exception.equals("MalformedJwtException")) {
            status = BaseResponseStatus.WRONG_JWT_SIGN_TOKEN;
            setResponse(response, status);
            return;
        }
        if(exception.equals("UnsupportedJwtException")) {
            status = BaseResponseStatus.UNSUPPORTED_JWT_TOKEN;
            setResponse(response, status);
            return;
        }
        if(exception.equals("IllegalArgumentException")) {
            status = BaseResponseStatus.WRONG_JWT_TOKEN;
            setResponse(response, status);
            return;
        }
    }
    private void setResponse(HttpServletResponse response, BaseResponseStatus status) throws IOException {

        BaseResponse baseResponse = new BaseResponse(status);
        response.setContentType("application/json;charset=UTF-8");
        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(baseResponse));
        writer.flush();
    }
}