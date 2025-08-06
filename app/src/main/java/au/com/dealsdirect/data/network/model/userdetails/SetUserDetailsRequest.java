package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 08/01/2017.
 */

public class SetUserDetailsRequest {

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("date_of_birth")
    @Expose
    private String dateBirth;
    @SerializedName("confirm_password")
    @Expose
    private String confirmPassword;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("last_name")
    @Expose
    private String surname;
    @SerializedName("first_name")
    @Expose
    private String firstname;
    @SerializedName("gender")
    @Expose
    private boolean gender;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("new_password")
    @Expose
    private String newPassword;
    @SerializedName("languageID")
    @Expose
    private String languageID;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }
}
