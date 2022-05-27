package au.com.dealsdirect.ui.controller.klarna;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface KlarnaMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createKlarnaSession();

    void createKlarnaOrder(String authorizationToken);

    boolean isBusy();
}
