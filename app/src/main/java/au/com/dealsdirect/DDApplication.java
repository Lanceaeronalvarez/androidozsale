package au.com.dealsdirect;
/*
 * Created by CodeineBot on 6/4/17.
 */

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.gu.toolargetool.TooLargeTool;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.di.component.ApplicationComponent;
import au.com.dealsdirect.di.component.DaggerApplicationComponent;
import au.com.dealsdirect.di.module.ApplicationModule;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.CookieUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DDApplication extends Application {

    public static RefWatcher refWatcher;

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //toolargetool instantiate
        TooLargeTool.startLogging(this);

        //Remove legacy cache and database
        removeLegacyData();

        //Initialize Leak Canary
        refWatcher = LeakCanary.install(this);

        //Initialize Application Component
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);

        //Initialize Timber Logger
        AppLogger.init();

        //Initialize Cookie Jar
        CookieUtils.initCookieJar(this);

        //Setup network client
        OkHttpClient customClient;
        //AndroidNetworking.initialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            customClient = NetworkUtils.provideOkHttpClientResponseCaching(this, HttpLoggingInterceptor.Level.BODY);
        } else {
            customClient = NetworkUtils.provideOkHttpClientResponseCaching(this);
        }

        AndroidNetworking.initialize(this, customClient);

        //Initialize version introspection
        IntrospectionUtils.verifyVersion(this);

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
                .setDefaultFontPath("fonts/App-Font-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void removeLegacyData() {
        boolean legacyDataExist = false;

        //Remove legacy database
        String[] dbList = this.databaseList();
        for (String aDbList : dbList) {
            Timber.d("CLEAN_LEGACY", "database: " + aDbList);
            if (aDbList.contains("ozsale-db")) {
                legacyDataExist = true;
                this.deleteDatabase(aDbList);
            }
        }

        //Remove all legacy shared preferences
        if (legacyDataExist) {

            Timber.d("CLEAN_LEGACY", "delete preferences");

            this.getSharedPreferences("MainActivity", Context.MODE_PRIVATE).edit().clear().apply();
            this.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).edit().clear().apply();
            this.getSharedPreferences("RateThisApp", Context.MODE_PRIVATE).edit().clear().apply();

            //Remove Cache folder and image cache
            try {
                trimCache(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                Timber.d("CLEAN_LEGACY", "delete: " + dir.getAbsolutePath());
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
