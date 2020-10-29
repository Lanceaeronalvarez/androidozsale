package au.com.dealsdirect.ui.controller.contact.selectsubject;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ContactSelectSubjectMvpPresenter<V extends ContactSelectSubjectMvpView> extends MvpPresenter<V> {

    void loadContactUsSubjects();
}
