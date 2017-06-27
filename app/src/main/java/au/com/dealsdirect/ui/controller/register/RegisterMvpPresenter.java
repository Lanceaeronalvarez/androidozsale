package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface RegisterMvpPresenter<V extends RegisterMvpView> extends MvpPresenter<V> {

    void registerUser(String firstName, String lastName, String email, String password);

}
