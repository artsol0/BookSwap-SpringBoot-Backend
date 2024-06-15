package com.artsolo.bookswap.configurations;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ErrorDescription errorDescription = new ErrorDescription(
                HttpStatus.FORBIDDEN.value(),
                "You haven't needed authority to perform this action",
                LocalDateTime.now()
        );
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(errorDescription));
    }
}
