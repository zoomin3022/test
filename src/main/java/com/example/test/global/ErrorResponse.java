package com.example.test.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "오류시 반환 DTO")
@Getter
public class ErrorResponse {

    @Schema(description = "오류 코드 (오류 코드표 참조)", example = "M001")
    private String code;
    @Schema(description = "오류 메시지", example = "이미 가입된 이메일 입니다.")
    private String message;

    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
