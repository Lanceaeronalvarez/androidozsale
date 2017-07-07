package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.text.DateFormatSymbols;

/**
 * Created by smartwave on 07/01/2017.
 */

public class GetUserDetailsResponse {

    private Response d;

    public Response getResponse() {
        return d;
    }

    public static class Response extends LegacyBaseResponseValue {
        public Value Value;

        public Value getValue() {
            return Value;
        }
    }

    public static class Value {
        @SerializedName("ID")
        @Expose
        private String id;
        @SerializedName("Username")
        @Expose
        private String username;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("ReceiveInvitations")
        @Expose
        private Boolean receiveInvitations;
        @SerializedName("Forename")
        @Expose
        private String forename;
        @SerializedName("Surname")
        @Expose
        private String surname;
        @SerializedName("Gender")
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
        @SerializedName("DateOfBirth")
        @Expose
        private DateOfBirth dateOfBirth;

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

        public DateOfBirth getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(DateOfBirth dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }
    }

    public static class DateOfBirth {
        @SerializedName("Day")
        @Expose
        private Integer day;
        @SerializedName("Month")
        @Expose
        private Integer month;
        @SerializedName("Year")
        @Expose
        private Integer year;

        public static String[] months = new DateFormatSymbols().getMonths();

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        @Override
        public String toString() {
            if(months != null) {
                return months[month - 1] + " "
                        + day + ", "
                        + year;
            } else {
                return "";
            }
        }
    }
}
