package au.com.dealsdirect.ui.controller.details;

import java.util.List;

import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.preferencecenter.UpdateEmailSubscriptionResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetEmailSubscriptionTemplatesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/20/17.
 */

public interface DetailsMvpView extends MvpView {
    void loadDetails(GetUserDetailsResponse userDetailsResponse);

    void saveUserDetailsSuccess();

    void saveUserDetailsFailed(String message);

    void onSaveReceiveSales(SaveReceiveSalesResponse saveReceiveSalesResponse);

    void onUpdateEmailSubscriptionPreference();

    void onGetEmailSubscriptionTemplates(List<GetEmailSubscriptionTemplatesResponse> response);

    boolean isActive();

    void onUpdateEmailSubscriptionPreferenceWithResponse(UpdateEmailSubscriptionResponse response);
}
