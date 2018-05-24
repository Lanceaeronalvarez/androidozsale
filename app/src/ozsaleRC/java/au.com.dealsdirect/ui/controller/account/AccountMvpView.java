package au.com.dealsdirect.ui.controller.account;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpView extends MvpView {

    void showAccountItems(List<AccountItem> accountItems);

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

    void showCountry();

    void showLegalities(String key, String option);

    void triggerLogin(String option);

    void triggerLogout();

    void initLoginDrawable();

    boolean isChangeInProgress();

    int getBackstackSize();
}
