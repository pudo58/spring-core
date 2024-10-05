package io.github.pudo58.base.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import io.github.pudo58.base.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final GoogleAuthenticator googleAuthenticator;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public String getQRCode(String username) {
        String key = userRepo.findByUsername(username).getSecretKey();
        if (key != null) {
            return GoogleAuthenticatorQRGenerator.getOtpAuthURL(appName, username, new GoogleAuthenticatorKey.Builder(userRepo.findByUsername(username).getSecretKey()).build());
        } else return null;
    }

    @Override
    public boolean verifyCode(String secretKey, Integer code) {
        return googleAuthenticator.authorize(secretKey, code);
    }
}
