package au.com.dealsdirect.utils.legacycookie;

import java.util.Date;

public interface Cookie {

    String getName();

    String getValue();

    String getComment();

    String getCommentURL();

    Date getExpiryDate();

    boolean isPersistent();

    String getDomain();

    String getPath();

    int[] getPorts();

    boolean isSecure();

    int getVersion();

    boolean isExpired(final Date date);

}