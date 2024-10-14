package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.config.jwt.JwtTokenProvider;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.SignupRequest;
import com.example.backend.dto.TokenRefreshRequest;
import com.example.backend.dto.TokenResponse;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.RefreshTokenService;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
            UserService userService, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = userService.createUser(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName());

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createAccessToken(authentication);

        //User user = (User) authentication.getPrincipal();
        // 여기서 UserDetails를 사용하여 사용자 정보를 가져옵니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // 필요하면 이메일을 통해 도메인 사용자 객체를 가져옵니다.
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(new TokenResponse(jwt, refreshToken.getRefreshToken(), 
                jwtTokenProvider.getAccessTokenValidityInSeconds()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User user = userService.getUserById(userId);
                    String token = jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                    return ResponseEntity.ok(new TokenResponse(token, requestRefreshToken, 
                        jwtTokenProvider.getAccessTokenValidityInSeconds()));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token, @RequestBody TokenRefreshRequest request) {
        String accessToken = token.substring(7); // "Bearer " 제거
        
        if (jwtTokenProvider.validateToken(accessToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(accessToken);
            refreshTokenService.deleteByUsername(username);
            // 여기에 액세스 토큰을 블랙리스트에 추가하는 로직을 구현할 수 있습니다.
            return ResponseEntity.ok("Log out successful!");
        } else {
            return ResponseEntity.badRequest().body("Invalid access token");
        }
    }

    // 토큰 유효성 검사 엔드포인트 추가
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        if (jwtTokenProvider.validateToken(accessToken)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
