package au.com.dealsdirect.di.component;

import au.com.dealsdirect.di.PerService;
import au.com.dealsdirect.di.module.ServiceModule;
import au.com.dealsdirect.service.SyncService;
import dagger.Component;


@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);

}
