package com.xscoder.pikky.loginSignUp.ModalClasses;

public class UserProfile {
    private int id;
    private String hiquik_id;
    private String first_name;
    private String last_name;
    private String email_id;
    private String phone;
    private String active_status;

    public UserProfile(int id, String hiquik_id, String first_name, String last_name, String email_id, String phone, String active_status, String user_password, String user_profile_base64) {
        this.id = id;
        this.hiquik_id = hiquik_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id = email_id;
        this.phone = phone;
        this.active_status = active_status;
        this.user_password = user_password;
        this.user_profile_base64 = user_profile_base64;
    }

    public String getUser_password() {
        return user_password;
    }

    public UserProfile(String first_name, String last_name, String email_id, String phone, String user_password, String user_profile_base64) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id = email_id;
        this.phone = phone;
        this.user_password = user_password;
        this.user_profile_base64 = user_profile_base64;
    }

    public String getUser_profile_base64() {
        return user_profile_base64;
    }

    private String user_password;
    private String user_profile_base64;

    public UserProfile() {
    }

    public UserProfile(String first_name, String last_name, String email_id, String phone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id = email_id;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getHiquik_id() {
        return hiquik_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getActive_status() {
        return active_status;
    }


}
