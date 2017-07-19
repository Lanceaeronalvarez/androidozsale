package au.com.dealsdirect.service.fcm;

import android.app.IntentService;
import android.content.Intent;

import au.com.dealsdirect.DDApplication;
import au.com.dealsdirect.di.component.DaggerServiceComponent;
import au.com.dealsdirect.di.component.ServiceComponent;
import au.com.dealsdirect.di.module.ServiceModule;

/*
 * Created by smartwave on 19/07/2017.
 */

public class BaseIntentService extends IntentService {

    private ServiceComponent mServiceComponent;

    public BaseIntentService(String name) {
        super(name);

        mServiceComponent = DaggerServiceComponent.builder()
                .applicationComponent(((DDApplication) getApplication()).getComponent())
                .serviceModule(new ServiceModule(this))
                .build();

        mServiceComponent.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
