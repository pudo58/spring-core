package io.github.pudo58.exception;

public class MessageResourceException extends RuntimeException {
    private Object[] args;

    public MessageResourceException(String message) {
        super(message);
    }

    public MessageResourceException(String message, Object... args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs(){
        return this.args;
    }
}
