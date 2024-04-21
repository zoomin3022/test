package com.example.test.member;

import com.example.test.global.ErrorResponse;
import com.example.test.member.dto.request.MemberRequestDto;
import com.example.test.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "members", description = "회원 API")
//swagger
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Operation(summary = "회원 정보 조회", description = "자신의 회원 정보를 조회")
    @Parameters({
            @Parameter(name = "access", hidden = true)
    })
    //swagger
    @GetMapping("/members")
    public ResponseEntity<MemberResponseDto> getMyInfo(@AuthenticationPrincipal MemberRequestDto.Access access) {
        return ResponseEntity.ok(memberService.getMyInfo(access.getEmail()));
    }
}
