package com.example.getIt.util;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    DUPLICATE_NICKNAME(false, 2000, "닉네임이 중복되었습니다."),
    DUPLICATE_EMAIL(false, 2001, "이메일이 중복되었습니다."),
    /*
    * Empty
    * */
    POST_USERS_EMPTY(false, 4000, "공백 없이 입력해주세요."),

    POST_USERS_INVALID_EMAIL(false, 5000, "이메일 양식이 맞지 않습니다.");
    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
