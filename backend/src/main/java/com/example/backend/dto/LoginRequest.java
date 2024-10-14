package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;

    // Lombok 어노테이션으로 인해 아래 메서드들은 자동 생성됩니다.
    // public LoginRequest() {}
    // public LoginRequest(String email, String password) { ... }
    // public String getEmail() { ... }
    // public void setEmail(String email) { ... }
    // public String getPassword() { ... }
    // public void setPassword(String password) { ... }
}
