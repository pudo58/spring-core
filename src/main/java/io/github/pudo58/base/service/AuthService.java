package io.github.pudo58.base.service;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    String getQRCode(String username);

    boolean verifyCode(String secretKey, Integer otp);

}
