package com.example.blogdemo.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;

@RestController
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {
        Object statusValue = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusValue == null ? 500 : Integer.parseInt(statusValue.toString());
        HttpStatus status = HttpStatus.resolve(statusCode);
        String message = status == null ? "Request failed" : status.getReasonPhrase();

        return ResponseEntity.status(statusCode)
                .body(ApiResponse.error(statusCode, message));
    }
}
