package au.com.dealsdirect.ui.controller.account;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpView extends MvpView {

    void showAccountItems(List<String> accountItems, int[] accountImages);

    void showMyDetailsController();

    void showMyAddressesController();

    void showMyOrders();

    void showMyVouchers();

    void showMyReturns();

    void showMyPaymentsController();

    void showLanguage();

    void showContactUs();

    void showTutorial();

    void showInviteAFriend();

    void showLegalities(String key, String Title);

    void triggerLogin(String option);

    void triggerLogout();

    void initLoginDrawable();

    boolean isChangeInProgress();

}
