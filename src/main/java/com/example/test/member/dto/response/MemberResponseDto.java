package com.example.test.member.dto.response;

import com.example.test.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Schema(description = "회원 정보 Response DTO")
@Getter
@AllArgsConstructor
public class MemberResponseDto {

    @Schema(description = "이메일", example = "test@gmail.com")
    private String memberEmail;
    @Schema(description = "이름", example = "주우민")
    private String memberName;
    @Schema(description = "닉네임", example = "zoomin")
    private String memberNickName;
    @Schema(description = "생년월일", example = "1999-01-28")
    private LocalDate memberBirthDate;
    @Schema(description = "성별", example = "남성", allowableValues = {"남성", "여성"})
    private String memberGender;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(
                member.getMemberEmail(),
                member.getMemberName(),
                member.getMemberNickName(),
                member.getMemberBirthDate(),
                member.getMemberGender().getKorean());
    }
}
