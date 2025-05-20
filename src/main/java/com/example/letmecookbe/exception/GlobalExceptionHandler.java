package com.example.letmecookbe.exception;



import com.example.letmecookbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode code = e.getCode();

        apiResponse.setCode(code.getCode());
        apiResponse.setMessage(code.getMessage());

        return ResponseEntity.status(code.getStatusCode()).body(apiResponse);
    }

}
