package au.com.dealsdirect.service.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MTC on 2019-05-20.
 */
public class FirebaseInstanceID extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        GNotification.storeRegistrationId(getApplicationContext(), refreshedToken);
    }

}
