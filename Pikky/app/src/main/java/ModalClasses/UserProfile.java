package ModalClasses;

public class UserProfile {
    private int id;
    private String hiquik_id;
    private String first_name;
    private String last_name;
    private String email_id;
    private String phone;
    private String active_status;

    private String profile_url;

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

    public String getProfile_url() {
        return profile_url;
    }
}
