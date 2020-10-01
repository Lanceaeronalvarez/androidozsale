package au.com.dealsdirect.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import au.com.dealsdirect.data.AppDataManager;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.auth.Auth;
import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.cachedresponses.AppCachedResponseHelper;
import au.com.dealsdirect.data.cachedresponses.CachedResponseHelper;
import au.com.dealsdirect.data.network.ApiHeader;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.network.AppApiHelper;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.data.templatetexts.AppTemplateTextsHelper;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.data.wishlist.AppWishlistHelper;
import au.com.dealsdirect.data.wishlist.WishlistHelper;
import au.com.dealsdirect.di.ApiInfo;
import au.com.dealsdirect.di.ApplicationContext;
import au.com.dealsdirect.di.DatabaseInfo;
import au.com.dealsdirect.di.PreferenceInfo;
import au.com.dealsdirect.utils.AppConstants;
import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @ApiInfo
    String provideApiKey() {
        return ""; //BuildConfig.API_KEY;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    //
//    @Provides
//    @Singleton
//    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
//        return appDbHelper;
//    }
//
    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    AuthHelper provideAuthHelper(Auth auth) {
        return auth;
    }

    @Provides
    @Singleton
    ApiHeader provideApiHeader(@ApiInfo String apiKey, PreferencesHelper preferencesHelper) {
        return new ApiHeader(apiKey, preferencesHelper);
    }

    @Provides
    @Singleton
    WishlistHelper provideWishlistHelper(AppWishlistHelper wishlistHelper) {
        return wishlistHelper;
    }

    @Provides
    @Singleton
    CachedResponseHelper provideCachedResponseHelper(AppCachedResponseHelper cachedResponseHelper) {
        return cachedResponseHelper;
    }

    @Provides
    @Singleton
    TemplateTextsHelper provideTemplateTextsHelper(AppTemplateTextsHelper templateTextsHelper) {
        return templateTextsHelper;
    }

//    @Provides
//    @Singleton
//    CalligraphyConfig provideCalligraphyDefaultConfig() {
//        return new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/source-sans-pro/SourceSansPro-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build();
//    }
}
