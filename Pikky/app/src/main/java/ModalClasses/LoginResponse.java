package ModalClasses;

public class LoginResponse {
    private int status;
    private String Message;
    private boolean userExists;
    private boolean passwordMatch;
    private UserProfile userProfile;

    public LoginResponse(int status, String message, boolean userExists, boolean passwordMatch, UserProfile userProfile) {
        this.status = status;
        Message = message;
        this.userExists = userExists;
        this.passwordMatch = passwordMatch;
        this.userProfile = userProfile;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return Message;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public boolean isPasswordMatch() {
        return passwordMatch;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}
