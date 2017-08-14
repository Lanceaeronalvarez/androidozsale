package au.com.dealsdirect.service.event;
/*
 * Created by CodeineBot on 8/11/17.
 */

public enum UserGroupType {

    MEMBER("member", "M"),
    PRIORITY_MEMBER("priority member", "P"),
    STAFF_MEMBER("staff member", "S"),
    ANONYMOUS("anonymous", "A"),
    FROM_CONTACT_US("from contact us", "U");

    private String type;
    private String code;

    UserGroupType(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
}


