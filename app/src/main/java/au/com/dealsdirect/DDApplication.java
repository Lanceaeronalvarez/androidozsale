package au.com.dealsdirect;
/*
 * Created by CodeineBot on 6/4/17.
 */

import android.app.Application;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.di.component.ApplicationComponent;
import au.com.dealsdirect.di.component.DaggerApplicationComponent;
import au.com.dealsdirect.di.module.ApplicationModule;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DDApplication extends Application {

    public static RefWatcher refWatcher;

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

        AppLogger.init();

        OkHttpClient customClient = null;

//        AndroidNetworking.initialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            customClient = NetworkUtils.provideOkHttpClientResponseCaching(this, HttpLoggingInterceptor.Level.HEADERS);
        }else{
            customClient = NetworkUtils.provideOkHttpClientResponseCaching(this);
        }

        AndroidNetworking.initialize(this,customClient);

        initFonts();
    }


    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    private void initFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
