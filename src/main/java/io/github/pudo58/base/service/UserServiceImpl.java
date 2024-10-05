package io.github.pudo58.base.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import io.github.pudo58.base.entity.User;
import io.github.pudo58.base.repo.UserRepo;
import io.github.pudo58.dto.DataResponse;
import io.github.pudo58.exception.MessageResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final MessageSource messageSource;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public String getSecretKeyByUsername(String username) {
        return userRepo.findByUsername(username).getSecretKey();
    }

    @Override
    public ResponseEntity<?> enable2Fa() {
        User user = User.getContext();
        String secretKey = googleAuthenticator.createCredentials().getKey();
        if (user == null) {
            throw new MessageResourceException("error.unauthorized");
        }
        if (Boolean.TRUE.equals(user.getIsEnable2Fa()) && user.getSecretKey() != null && user.getSecretKey().length() > 0) {
            throw new MessageResourceException("otp.setuped");
        } else {
            user.setIsEnable2Fa(Boolean.TRUE);
            user.setSecretKey(secretKey);
            userRepo.save(user);
            String qrCode = GoogleAuthenticatorQRGenerator.getOtpAuthURL(appName, user.getUsername(), new GoogleAuthenticatorKey.Builder(secretKey).build());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(DataResponse.builder().data(qrCode).message(messageSource.getMessage("otp.setup.successful", null, LocaleContextHolder.getLocale())).build());
        }
    }
}
