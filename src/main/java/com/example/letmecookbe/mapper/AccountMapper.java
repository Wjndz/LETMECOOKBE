package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.AccountCreationRequest;
import com.example.letmecookbe.dto.response.AccountResponse;
import com.example.letmecookbe.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "roles", ignore = true)
    Account toAccount(AccountCreationRequest request);
    AccountResponse toAccountResponse(Account account);
}
