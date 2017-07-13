
package au.com.dealsdirect.di.component;


import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.di.module.ActivityModule;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.SharedActivity;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SharedActivity activity);

    DataManager getDataManager();

    CompositeDisposable getCompositeDisposable();

    SchedulerProvider getSchedulerProvider();
}
