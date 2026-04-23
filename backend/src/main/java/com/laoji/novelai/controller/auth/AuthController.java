package com.laoji.novelai.controller.auth;

import com.laoji.novelai.dto.auth.LoginRequest;
import com.laoji.novelai.dto.auth.LoginResponse;
import com.laoji.novelai.entity.auth.Permission;
import com.laoji.novelai.entity.auth.Role;
import com.laoji.novelai.entity.auth.User;
import com.laoji.novelai.repository.auth.UserRepository;
import com.laoji.novelai.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        Set<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(
            user.getUsername(),
            user.getId(),
            String.join(",", roles)
        );

        LoginResponse response = LoginResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .avatarUrl(user.getAvatarUrl())
            .roles(roles)
            .permissions(permissions)
            .build();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        Set<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());

        LoginResponse response = LoginResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .avatarUrl(user.getAvatarUrl())
            .roles(roles)
            .permissions(permissions)
            .build();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取用户信息成功");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登出成功");
        return ResponseEntity.ok(result);
    }
}