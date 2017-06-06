package au.com.dealsdirect.di.component;


import au.com.dealsdirect.di.PerController;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.sample.SampleController;
import dagger.Component;

/*
 * Created by Ayi on 05/06/2017.
 */

@PerController
@Component(dependencies = ActivityComponent.class, modules = ControllerModule.class)
public interface ControllerComponent {

    void inject(SampleController controller);
}
