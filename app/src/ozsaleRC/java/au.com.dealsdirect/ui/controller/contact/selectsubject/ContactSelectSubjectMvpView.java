package au.com.dealsdirect.ui.controller.contact.selectsubject;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;

public interface ContactSelectSubjectMvpView extends MvpView {

    void showContactSubjects(List<String> contactSubjectList);

    void onContactSubjectItemSelected(String selectedSubject);
}
