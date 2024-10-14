package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String name;

    // toString 메서드는 password 필드를 제외하고 오버라이드합니다.
    @Override
    public String toString() {
        return "SignupRequest{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
