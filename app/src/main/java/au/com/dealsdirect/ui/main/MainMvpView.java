package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.base.MvpView;

public interface MainMvpView extends MvpView {


    void showCategoryController();

    void showShopController();

    void showAccountController();

    void showContactController();

    void showInviteController();

    void showCheckoutController();

    void showController(Controller controller);


    //Samples
//    void openLoginActivity();
//
//    void showAboutFragment();
//
//    void refreshQuestionnaire(List<Question> questionList);
//
//    void reloadQuestionnaire(List<Question> questionList);
//
//    void updateUserName(String currentUserName);
//
//    void updateUserEmail(String currentUserEmail);
//
//    void updateUserProfilePic(String currentUserProfilePicUrl);
//
//    void updateAppVersion();
}
