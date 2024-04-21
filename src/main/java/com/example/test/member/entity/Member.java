package com.example.test.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_email")
    @NotNull
    private String memberEmail;

    @Column(name = "member_password")
    @NotNull
    private String memberPassword;

    @Column(name = "member_name")
    @NotNull
    private String memberName;

    @Column(name = "member_nickname")
    @NotNull
    private String memberNickName;

    @Column(name = "member_birth")
    @NotNull
    private LocalDate memberBirthDate;

    @Column(name = "member_gender")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender memberGender;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();


    @Builder
    public Member(String memberEmail, String memberPassword, String memberName, String memberNickName, LocalDate memberBirthDate, Gender memberGender) {
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberNickName = memberNickName;
        this.memberBirthDate = memberBirthDate;

        this.memberGender = memberGender;
        this.roles.add(Role.ROLE_USER);
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return memberPassword;
    }

    //얘는 나중에 Authentication 가져올때 이거 사용하는지 확인해보자
    @Override
    public String getUsername() {
        return memberEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
