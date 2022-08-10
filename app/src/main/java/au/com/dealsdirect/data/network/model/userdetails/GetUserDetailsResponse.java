package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUserDetailsResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_name")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("receive_invitations")
    @Expose
    private Boolean receiveInvitations;
    @SerializedName("first_name")
    @Expose
    private String forename;
    @SerializedName("last_name")
    @Expose
    private String surname;
    @SerializedName("gender")
    @Expose
    private Boolean gender;
    @SerializedName("Nickname")
    @Expose
    private Object nickname;
    @SerializedName("Userpic")
    @Expose
    private Object userpic;
    @SerializedName("UserPicsPath")
    @Expose
    private String userPicsPath;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("member_preference")
    @Expose
    private String memberPreference;
    @SerializedName("preference_date")
    @Expose
    private String preferenceDate;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getReceiveInvitations() {
        return receiveInvitations;
    }

    public void setReceiveInvitations(Boolean receiveInvitations) {
        this.receiveInvitations = receiveInvitations;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Object getNickname() {
        return nickname;
    }

    public void setNickname(Object nickname) {
        this.nickname = nickname;
    }

    public Object getUserpic() {
        return userpic;
    }

    public void setUserpic(Object userpic) {
        this.userpic = userpic;
    }

    public String getUserPicsPath() {
        return userPicsPath;
    }

    public void setUserPicsPath(String userPicsPath) {
        this.userPicsPath = userPicsPath;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMemberPreference() {
        return memberPreference;
    }

    public void setMemberPreference(String memberPreference) {
        this.memberPreference = memberPreference;
    }

    public String getPreferenceDate() {
        return preferenceDate;
    }

    public void setPreferenceDate(String preferenceDate) {
        this.preferenceDate = preferenceDate;
    }
}
