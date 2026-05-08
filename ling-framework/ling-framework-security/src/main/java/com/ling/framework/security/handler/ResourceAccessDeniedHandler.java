package com.ling.framework.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class ResourceAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        System.out.println(request.getRequestURI()+ " 无权限: " + accessDeniedException.getMessage());
        System.out.println("异常类 = " + accessDeniedException.getClass());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter printWriter = response.getWriter();
        printWriter.append("{\"code\":\"" + HttpServletResponse.SC_FORBIDDEN + "\",\"msg\":\"").append(accessDeniedException.getMessage()).append("\"}");

    }
}
