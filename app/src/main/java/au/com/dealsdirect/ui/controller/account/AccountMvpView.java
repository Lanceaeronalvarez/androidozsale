package au.com.dealsdirect.ui.controller.account;

import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;

public interface AccountMvpView extends MvpView {

    String TAG = "AccountController";

    void showMyDetailsController();

    void showMyAddressesController();

    void showMyOrders();

    void showMyVouchers();

    void showMyReturns();

    void showReturnsPolicy();

    void showMyPaymentsController();

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
