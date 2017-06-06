package au.com.dealsdirect.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import au.com.dealsdirect.DDApplication;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.di.ApplicationContext;
import au.com.dealsdirect.di.module.ApplicationModule;
import au.com.dealsdirect.service.SyncService;
import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DDApplication app);

    void inject(SyncService service);

    @ApplicationContext
    Context context();

    Application application();

    DataManager getDataManager();
}