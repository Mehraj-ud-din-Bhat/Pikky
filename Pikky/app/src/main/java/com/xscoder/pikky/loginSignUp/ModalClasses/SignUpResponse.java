package com.xscoder.pikky.loginSignUp.ModalClasses;

public class SignUpResponse {
    String message;
    int status;
    boolean registered;

    public SignUpResponse() {
    }

    public SignUpResponse(String message, int status, boolean resgistered) {
        this.message = message;
        this.status = status;
        this.registered = resgistered;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public boolean isResgistered() {
        return registered;
    }
}
