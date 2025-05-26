package com.example.letmecookbe.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_EXISTED(1001, "Email already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not exists", HttpStatus.NOT_FOUND),
    OTP_INCORRECT(1003, "OTP incorrect", HttpStatus.BAD_REQUEST),
    NAME_INVALID(1004, "Name must be at least 5 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1006, "Invalid email", HttpStatus.BAD_REQUEST),
    BIRTHDAY_INVALID(1007, "Birthday must be a day in the past", HttpStatus.BAD_REQUEST),
    GENDER_INVALID(1008, "Gender must be either 'male' or 'female'", HttpStatus.BAD_REQUEST),
    ACCOUNT_EMPTY(1009, "List of accounts is empty", HttpStatus.NO_CONTENT),
    NOT_NULL(1010, "Something is blank", HttpStatus.BAD_REQUEST),
    PERSONAL_INFORMATION_EMPTY(1011, "List of accounts is empty", HttpStatus.NO_CONTENT),
    PERINFO_EMPTY(1012, "User has not added personal information", HttpStatus.NOT_FOUND),
    ACCOUNT_BANNED(1013, "Account has been banned", HttpStatus.FORBIDDEN),
    PASSWORD_NOT_CORRECT(1014, "Password not correct", HttpStatus.UNAUTHORIZED),
    SEND_EMAIL_FAILED(1015, "Send email failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CATEGORY_INVALID(1016, "Folder and name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(1017, "This main category already exists", HttpStatus.CONFLICT),
    _CATEGORY_EXISTED(1017, "SUBThis main category already exists", HttpStatus.CONFLICT),
    SUBCATEGORY_BLANK(1018, "Sub-category must not be blank", HttpStatus.BAD_REQUEST),
    LIST_EMPTY(1019, "List is empty", HttpStatus.NO_CONTENT),
    SUB_CATEGORY_EXISTED(1001, "This Sub-category already exists", HttpStatus.BAD_REQUEST),
    MAIN_CATEGORY_NOT_EXIST(1020, "This main does not exist", HttpStatus.NOT_FOUND),
    SUB_CATEGORY_NOT_EXIST(1020, "This sub does not exist", HttpStatus.NOT_FOUND),
    NOTIFICATION_LENGTH(1021, "Title and content must be at least 5 characters", HttpStatus.BAD_REQUEST),
    NOTIFICATION_EXISTED(1022, "Notification already exists", HttpStatus.CONFLICT),
    NOTIFICATION_NOT_EXISTED(1023, "Notification not exists", HttpStatus.NOT_FOUND),
    FEEDBACK_EXISTED(1024, "You have already sent feedback. If you want to change anything, please update the old feedback.", HttpStatus.CONFLICT),
    RECIPE_EMPTY(1025, "Recipe is empty", HttpStatus.BAD_REQUEST),
    RECIPE_NAME_INVALID(1026, "File name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1027, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    DOC_NOT_EXIST(1028, "Document does not exist", HttpStatus.NOT_FOUND),
    COMMENT_NOT_EXIST(1029, "Comment does not exist", HttpStatus.NOT_FOUND),
    NOT_ENOUGH_POINT_TO_DOWNLOAD(1030, "Not enough points to download", HttpStatus.FORBIDDEN),
    INCORRECT_OTP(1031, "Incorrect OTP", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1032, "You do not have permission", HttpStatus.FORBIDDEN),
    USERNAME_EXISTED(1033, "Username already exists", HttpStatus.BAD_REQUEST),
    FAILED_TO_SENT_EMAIL(1034, "Failed to sent email", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_HASHING(1035,"Error hashing input when hash a otp", HttpStatus.INTERNAL_SERVER_ERROR),
    OTP_EXPIRED(1036, "OTP expired", HttpStatus.UNAUTHORIZED),
    FEEDBACK_NOT_FOUND(1037, "Feedback does not exist", HttpStatus.NOT_FOUND),
    PER_INFO_EXISTED(1038, "Per-info already exists", HttpStatus.CONFLICT),
    DRIVE_UPLOAD_FAILED(1039, "Drive upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FEEDBACK_TYPE_INCORRECT(1040,"Incorrect feedback type", HttpStatus.BAD_REQUEST),
    NOTIFICATION_INVALID(1041,"Notification invalid", HttpStatus.BAD_REQUEST),
    INGREDIENT_NOT_FOUND(1042,"Ingredient not found", HttpStatus.NOT_FOUND),
    INGREDIENT_EXISTED(1043,"Ingredient already exists", HttpStatus.CONFLICT),
    INGREDIENT_NOT_EXISTED(1044,"Ingredient not found", HttpStatus.NOT_FOUND),
    RECIPE_NOT_FOUND(1045,"Recipe not found", HttpStatus.NOT_FOUND),

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_KEY(8888, "Invalid key", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

}
