package io.github.pudo58.controller.v2;

import io.github.pudo58.base.entity.User;
import io.github.pudo58.base.service.AuthService;
import io.github.pudo58.base.service.JwtService;
import io.github.pudo58.base.service.TokenService;
import io.github.pudo58.base.service.UserService;
import io.github.pudo58.dto.AuthRequest;
import io.github.pudo58.dto.LoginResponse;
import io.github.pudo58.exception.MessageResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public User register(@RequestBody User model) {
        return userService.register(model);
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            if (authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()) {
                throw new MessageResourceException("validate.username.notnull");
            }
            if (authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
                throw new MessageResourceException("validate.password.notnull");
            }
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                if (!Boolean.TRUE.equals(user.getIsEnable2Fa())) {
                    String username = user.getUsername();
                    String token = jwtService.generateToken(username);
                    tokenService.saveToken(username, token);
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(LoginResponse.builder().token(token).authenticated(true).build());
                } else {
                    String qrUrl = authService.getQRCode(authRequest.getUsername());
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(LoginResponse.builder().is2Fa(qrUrl != null).authenticated(false).build());
                }
            } else {
                throw new MessageResourceException("authentication.fail");
            }
        } catch (InternalAuthenticationServiceException | BadCredentialsException ex) {
            throw new MessageResourceException("authentication.fail");
        }
    }

    @GetMapping("/verify2FaCode")
    public ResponseEntity<?> verify2FaCode(@RequestParam String username, @RequestParam Integer otp) {
        if (username == null) {
            throw new MessageResourceException("validate.username.notnull");
        }
        if (otp == null) {
            throw new MessageResourceException("otp.notnull");
        }
        User user = userService.findByUsername(username);
        if (user != null) {
            if (Boolean.TRUE.equals(user.getIsEnable2Fa()) && user.getSecretKey() != null && user.getSecretKey().length() > 0) {
                boolean isValid = authService.verifyCode(user.getSecretKey(), otp);
                if (isValid) {
                    String token = jwtService.generateToken(username);
                    tokenService.saveToken(username, token);
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(LoginResponse.builder().token(token).authenticated(true).build());
                } else {
                    throw new MessageResourceException("otp.incorrect");
                }
            } else {
                throw new MessageResourceException("otp.non.setup");
            }
        } else {
            throw new MessageResourceException("user.not.exist", username);
        }
    }

}
