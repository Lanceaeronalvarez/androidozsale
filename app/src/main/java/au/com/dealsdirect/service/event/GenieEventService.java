package au.com.dealsdirect.service.event;
/*
 * Created by CodeineBot on 8/9/17.
 */

import android.os.Build;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class GenieEventService {

    public static final String TAG = "GenieEventService";

    private DataManager mDataManager;

    private SchedulerProvider mSchedulerProvider;

    private CompositeDisposable mCompositeDisposable;

    public GenieEventService(DataManager dataManager,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = compositeDisposable;
    }

    public void callSearchEvent(SearchEventRequest request) {

        request = includeFrontEndInfo(request);
        request = includeVisitorInfo(request);

        getCompositeDisposable().add(getDataManager()
                .callSearchEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    //Log.d(TAG, responseValue);
                }));
    }

    public void callEventUser() {
        getCompositeDisposable().add(getDataManager()
                .callEventUser()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    AppLogger.d(String.format("%s: %s", TAG, responseValue));

                    //Save user Id to data manager
                    if (!responseValue.isEmpty()) {
                        getDataManager().setEventUserId(responseValue.replace("\"", ""));
                    }
                }));
    }

    private SearchEventRequest includeFrontEndInfo(SearchEventRequest request) {
        SearchEventRequest.FrontEndInfo frontEndInfo = new SearchEventRequest.FrontEndInfo();

        if (getDataManager().isTablet()) {
            frontEndInfo.setFrontEnd(FrontEndType.TABLET);
        } else {
            frontEndInfo.setFrontEnd(FrontEndType.PHONE);
        }

        frontEndInfo.setOsVersion(Build.VERSION.RELEASE);
        frontEndInfo.setUiVersion(BuildConfig.VERSION_NAME);

        request.setFrontEndInfo(frontEndInfo);

        return request;
    }

    private SearchEventRequest includeVisitorInfo(SearchEventRequest request) {

        SearchEventRequest.VisitorInfo visitorInfo = new SearchEventRequest.VisitorInfo();
        visitorInfo.setVisitorId(""); //Set to empty, is this supported for mobile?
        visitorInfo.getUserCohorts().addAll(getCohorts());
        visitorInfo.setUserGroup(getUserGroup());
        visitorInfo.setCompany(getDataManager().getCountryId());
        visitorInfo.setRegion(RegionType.getType(getDataManager().getCountryId()));
        visitorInfo.setUserId(getDataManager().getEventUserId());

        request.setVisitorInfo(visitorInfo);

        return request;
    }

    private ArrayList<String> getCohorts() {
        ArrayList<String> cohorts = new ArrayList<>();
        List<HttpCookie> cookieList = HttpCookie.parse(getDataManager().getCookies().toString());

        try {
            for (HttpCookie c : cookieList) {
                if (c.getName().equalsIgnoreCase("ut")) {
                    String ut[] = c.getValue().split("&");

                    for (int i = 0; i < ut.length; i++) {
                        cohorts.add(ut[i].split("=")[1]);
                        AppLogger.d(String.format("%s: cohorts: %s", TAG, ut[i].split("=")[1]));
                    }

                    break;
                }
            }
        } catch (Exception ignored) {
            AppLogger.e(ignored.getMessage());
        }
        return cohorts;
    }

    private String getUserGroup() {
        String userGroup = "";
        List<HttpCookie> cookieList = HttpCookie.parse(getDataManager().getCookies().toString());

        for (HttpCookie c : cookieList) {
            if (c.getName().equalsIgnoreCase("us")) {
                String cookieValue = c.getValue();
                userGroup = (String) cookieValue.subSequence(cookieValue.indexOf("=") + 1, cookieValue.indexOf("&"));
                break;
            }
        }

        AppLogger.d(String.format("%s: userGroup: %s", TAG, userGroup));

        return userGroup;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

}
