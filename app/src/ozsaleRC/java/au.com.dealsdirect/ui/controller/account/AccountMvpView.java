package au.com.dealsdirect.ui.controller.account;

import com.bluelinelabs.conductor.Router;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpView extends MvpView {

    String TAG = "AccountController";

    void showAccountItems(List<AccountItem> accountItems);

    void showMyDetailsController();

    void showMyAddressesController();

    void showChangeDeliveryAddressController(boolean calledFromOrder, String orderID);

    void showReturnDetails(String returnID, String productName, boolean isFromOrders);

    void addNewReturns(int invoiceNumber, boolean calledFromOrder, String productId);

    void showMyOrders();

    void showMyVouchers();

    void showMyReturns();

    void showMyPaymentsController();

    void showMyAccountsOurpay();

    void showMyAccountsSelect();

    void showLanguage();

    void showContactUs();

    void showTutorial();

    void showInviteAFriend();

    void showCountry();

    void showNotification();

    void showLegalities(String key, String option);

    void triggerLogin(String option);

    void triggerLogout();

    void triggerLogout(boolean showDialog);

    void initLoginDrawable();

    boolean isChangeInProgress();

    Router getDisplayRouter();

    int getBackstackSize();

}
