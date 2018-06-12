package au.com.dealsdirect.ui.controller.account;

import com.bluelinelabs.conductor.Router;

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

    void triggerLogin(String option,int position);

    void triggerLogout();

    void initLoginDrawable();

    boolean isChangeInProgress();

    boolean isTablet();

    Router getDisplayRouter();

    int getBackstackSize();

    void updateAdapter(int position);
}
