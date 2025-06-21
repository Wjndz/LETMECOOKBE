package com.example.letmecookbe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountStatusResponse {
    private String status;
    private boolean canLogin;
    private boolean canRegister;
    private String message;
}
