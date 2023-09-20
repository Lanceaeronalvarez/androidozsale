package au.com.dealsdirect.di.module;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.profiler.ProfilerInterface;

import javax.inject.Singleton;

import au.com.dealsdirect.di.ActivityContext;
import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.service.datacollection.registerservices.FirebaseAnalyticsService;
import au.com.dealsdirect.service.datacollection.registerservices.GenieEventService;
import au.com.dealsdirect.service.event.FirebaseEventServiceInterface;
import au.com.dealsdirect.service.event.GenieEventServiceInterface;
import au.com.dealsdirect.ui.main.MainMvpPresenter;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.ui.main.MainPresenter;
import au.com.dealsdirect.utils.rx.AppSchedulerProvider;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainPresenter(MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GenieEventServiceInterface provideGenieEventService(GenieEventService genieEventService) {
        return genieEventService;
    }

    @Provides
    @PerActivity
    FirebaseEventServiceInterface provideFirebaseEventService(FirebaseAnalyticsService firebaseEventService) {
        return firebaseEventService;
    }

    @Provides
    @PerActivity
    ProfilerInterface provideProfiler() {
        return new Profiler();
    }
}
