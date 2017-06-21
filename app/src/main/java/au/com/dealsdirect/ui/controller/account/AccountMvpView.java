package au.com.dealsdirect.ui.controller.account;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpView extends MvpView {

    void showAccountItems(List<String> accountItems);

    void showMyDetailsController();

    void showMyAddressesController();

    void showMyOrders();

    void showMyVouchers();

    void showMyReturns();

    void showViewContactUsController();

    void showLanguage();

    void triggerLogin(String option);
}
