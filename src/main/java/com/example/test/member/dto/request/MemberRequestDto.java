package com.example.test.member.dto.request;

import com.example.test.member.entity.Gender;
import com.example.test.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberRequestDto {

    @Schema(description = "회원 가입 Request DTO")
    @Getter
    public static class SignUp {

        @Schema(description = "이메일", example = "test@gmail.com")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
        @NotNull
        private String memberEmail;
        @Schema(description = "비밀번호", example = "password11")
        @NotNull
        private String memberPassword;
        @Schema(description = "이름", example = "주우민")
        @NotNull
        private String memberName;
        @Schema(description = "닉네임", example = "zoomin")
        @NotNull
        private String memberNickName;
        @Schema(description = "생년월일", example = "1999-01-28")
        @NotNull
        private LocalDate memberBirthDate;
        @Schema(description = "성별", example = "남성", allowableValues = {"남성", "여성"})
        @NotNull
        private String memberGender;
        @Schema(description = "지역", example = "경기도", allowableValues = {"서울", "인천", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도"})
        @NotNull
        private String memberRegion;

        @JsonIgnore
        private Gender setEnumMemberGender(String gender) {
            if (gender.equals(Gender.MALE.getKorean())) {
                return Gender.MALE;
            } else {
                return Gender.FEMALE;
            }
        }

        public Member toEntity(String encryptedPassword) {
            return Member.builder()
                    .memberEmail(memberEmail)
                    .memberPassword(encryptedPassword)
                    .memberName(memberName)
                    .memberNickName(memberNickName)
                    .memberBirthDate(memberBirthDate)
                    .memberGender(setEnumMemberGender(memberGender))
                    .build();
        }
    }

    @Schema(description = "로그인 Request DTO")
    @Getter
    public static class Login {

        @Schema(description = "이메일", example = "test@gmail.com")
        @NotNull
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
        private String memberEmail;

        @Schema(description = "비밀번호", example = "password11")
        @NotNull
        private String memberPassword;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(memberEmail, memberPassword);
        }
    }

    @Getter
    public static class Access {
        private String email;
        private List<String> roles = new ArrayList<>();

        private Access(String email, List<String> authorities) {
            this.email = email;
            roles.addAll(authorities);
        }

        public static Access from(String email, List<String> authorities) {
            return new Access(email, authorities);
        }
    }
}
