package au.com.dealsdirect.ui.controller.masterpass;
/*
 * Created by CodeineBot on 8/3/17.
 */

import au.com.dealsdirect.ui.base.MvpView;

public interface MasterpassMvpView extends MvpView {

    void loadMasterpassUrl(String url, String host);

    void showError(String message);

    void showPaymentSuccess(String address, String price, String invoice, String delivery);
}
