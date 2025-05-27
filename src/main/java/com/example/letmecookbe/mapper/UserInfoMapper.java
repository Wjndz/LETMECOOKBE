package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.UserInfoCreationRequest;
import com.example.letmecookbe.dto.request.UserInfoUpdateRequest;
import com.example.letmecookbe.dto.response.UserInfoResponse;
import com.example.letmecookbe.entity.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserInfo toUserInfo(UserInfoCreationRequest request);

    @Mapping(source = "account.id", target = "accountId")
    UserInfoResponse toUserInfoResponse(UserInfo userInfo);

    void updateUserInfo(UserInfoUpdateRequest request, @MappingTarget UserInfo userInfo);
}