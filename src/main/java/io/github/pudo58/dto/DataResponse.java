package io.github.pudo58.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DataResponse {
    private Object data;
    private String message;
}
