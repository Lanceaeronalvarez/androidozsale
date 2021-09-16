package au.com.dealsdirect.ui.controller.account;

import com.bluelinelabs.conductor.Router;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpView extends MvpView {

    String TAG = "AccountController";

    void showAccountItems(List<AccountItem> accountItems);

    void showMyDetailsController();

    void showMyAddressesController();

    void showMyOrders();

    void showMyVouchers();

    void showMyReturns();

    void showReturnsPolicy();

    void showMyPaymentsController();

    void showMyAccountsOurpay();

    void showMyAccountsSelect();

    void showLanguage();

    void showContactUs();

    void showTutorial();

    void showInviteAFriend();

    void showCountry();

    void showNotification();

    void showLegalities(String key, AccountOption option);

    void showInformationMenu();

    void triggerLogin(AccountOption option);

    void triggerLogout();

    void triggerLogout(boolean showDialog);

    void initLoginDrawable();

    boolean isChangeInProgress();

    Router getDisplayRouter();

    int getBackstackSize();

}
