package au.com.dealsdirect.di.module;

import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.sample.SampleMvpPresenter;
import au.com.dealsdirect.ui.sample.SampleMvpView;
import au.com.dealsdirect.ui.sample.SamplePresenter;
import dagger.Module;
import dagger.Provides;

/*
 * Created by Ayi on 05/06/2017.
 */

@Module
public class ControllerModule {

    private Controller mController;

    public ControllerModule(Controller controller) {
        this.mController = controller;
    }

    @Provides
    SampleMvpPresenter<SampleMvpView> provideSamplePresenter(SamplePresenter<SampleMvpView> presenter) {
        return presenter;
    }

}
