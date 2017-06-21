package au.com.dealsdirect.data.auth;

import java.io.Serializable;

/**
 * dp Created by Admin on 6/21/17.
 */

public interface AuthHandler extends Serializable{
    void success();
    void error();
}
