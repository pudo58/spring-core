package io.github.pudo58.base.service;

import io.github.pudo58.base.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User findByUsername(String username);

    User register(User user);

    String getSecretKeyByUsername(String username);

    ResponseEntity<?> enable2Fa();
}
