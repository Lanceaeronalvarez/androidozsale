package au.com.dealsdirect.data;

//import au.com.dealsdirect.data.auth.Auth;
import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import io.reactivex.Observable;

public interface DataManager extends PreferencesHelper, ApiHelper, AuthHelper {

    void updateApiHeader(Long userId, String accessToken);

    void setUserAsLoggedOut();

    Observable<Boolean> seedDatabaseQuestions();

    Observable<Boolean> seedDatabaseOptions();

    boolean isTablet();

    void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath);

    enum LoggedInMode {

        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3);

        private final int mType;

        LoggedInMode(int type) {
            mType = type;
        }

        public int getType() {
            return mType;
        }
    }
}
