package com.example.test.member.entity;

public enum Gender {

    MALE("남성"),
    FEMALE("여성");

    private String korean;

    public String getKorean() {
        return korean;
    }

    Gender(String korean) {
        this.korean = korean;
    }
}
