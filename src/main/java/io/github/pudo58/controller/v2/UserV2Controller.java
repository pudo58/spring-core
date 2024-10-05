package io.github.pudo58.controller.v2;

import io.github.pudo58.base.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
public class UserV2Controller {
    private final UserService userService;

    @PostMapping("/enable2Fa")
    public ResponseEntity<?> enable2Fa() {
        return userService.enable2Fa();
    }
}
