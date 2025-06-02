package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.UserInfoCreationRequest;
import com.example.letmecookbe.dto.request.UserInfoUpdateRequest;
import com.example.letmecookbe.dto.response.UserInfoResponse;
import com.example.letmecookbe.entity.UserInfo;
import com.example.letmecookbe.enums.DietType;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserInfo toUserInfo(UserInfoCreationRequest request);

    @Mapping(source = "account.id", target = "accountId")
    UserInfoResponse toUserInfoResponse(UserInfo userInfo);

    default void updateUserInfo(UserInfoUpdateRequest request, @MappingTarget UserInfo userInfo) {
        if (request.getHeight() > 0) {
            userInfo.setHeight(request.getHeight());
        }
        if (request.getWeight() > 0) {
            userInfo.setWeight(request.getWeight());
        }
        if (request.getSex() != null) {
            userInfo.setSex(request.getSex());
        }
        if (request.getAge() > 0) {
            userInfo.setAge(request.getAge());
        }
        List<DietType> dietTypes = userInfo.getDietTypes();
        // Kiểm tra và xử lý dietTypesToAdd
        if (!request.getDietTypesToAdd().isEmpty()) {
            for (DietType dietType : request.getDietTypesToAdd()) {
                if (dietTypes.contains(dietType)) {
                    throw new AppException(ErrorCode.INVALID_DIET_TYPE);
                }
                dietTypes.add(dietType);
            }
        }
        // Xử lý dietTypesToRemove
        if (!request.getDietTypesToRemove().isEmpty()) {
            dietTypes.removeAll(request.getDietTypesToRemove());
        }
    }
}