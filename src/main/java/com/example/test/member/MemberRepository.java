package com.example.test.member;

import com.example.test.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByMemberEmail(String memberEmail);

    @EntityGraph(attributePaths = {"roles"})
    Optional<Member> findByMemberEmail(String memberEmail);

}
