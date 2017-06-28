package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import com.braintreepayments.api.BraintreeFragment;

import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    //Samples
//    void onDrawerOptionAboutClick();
//
//    void onDrawerOptionLogoutClick();
//
//    void onViewInitialized();
//
//    void onCardExhausted();
//
//    void onNavMenuCreated();

    void initServerSettings(Context context, String countryId);

    void callGetAppSettingsSection(Context context, String countryId);

    void fetchBTAuthorization(FetchTokenHandler fetchTokenHandler);

    void createPaymentMethod(BraintreeFragment braintreeFragment, String paymentNonce, String paymentType);

    void callLoginTicket();

    void callLogout();

}
