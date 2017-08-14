package au.com.dealsdirect.data;

import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.pref.PreferencesHelper;

public interface DataManager extends PreferencesHelper, ApiHelper, AuthHelper {

    boolean isTablet();

}
