package io.github.pudo58.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LoginResponse {
    private String token;
    private String qrCode2Fa;
    private Boolean authenticated;
    private String description;
    private Boolean is2Fa;
}
