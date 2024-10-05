package io.github.pudo58.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ControllerAdvice
@RequiredArgsConstructor
public class HandleExceptionAdvice {
    private final MessageSource messageSource;


    @ExceptionHandler(MessageResourceException.class)
    public ResponseEntity<?> handleIllegalArgumentException(MessageResourceException ex) {
        String messageCode = ex.getMessage();
        Object[] args = ex.getArgs();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        if (args != null && args.length > 0) {
            body.put("message", messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale()));
        } else {
            body.put("message", messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale()));
        }
        return ResponseEntity.ok()
                .header(CONTENT_TYPE,"application/json;charset=UTF-8")
                .body(body);
    }
}
