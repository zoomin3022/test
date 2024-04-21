package com.example.test.member;

import com.example.test.auth.exception.AuthException;
import com.example.test.auth.exception.AuthExceptionType;
import com.example.test.member.dto.response.MemberResponseDto;
import com.example.test.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDto getMyInfo(String email) {
        final Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AuthException(AuthExceptionType.ACCOUNT_NOT_EXISTS));

        return MemberResponseDto.of(member);

    }
}
