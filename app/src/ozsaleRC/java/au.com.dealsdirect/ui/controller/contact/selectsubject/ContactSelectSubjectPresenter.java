package au.com.dealsdirect.ui.controller.contact.selectsubject;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjects;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ContactSelectSubjectPresenter<V extends ContactSelectSubjectMvpView> extends BasePresenter<V> implements ContactSelectSubjectMvpPresenter<V> {

    @Inject
    public ContactSelectSubjectPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void loadContactUsSubjects() {
        getMvpView().showLoading();

        ContactSubjectsRequest contactSubjectsRequest = new ContactSubjectsRequest(getDataManager().getCountryId(),getDataManager().getLanguageId());

        doApiCallForResponse(getDataManager().callGetContactSubjects(contactSubjectsRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                ContactSubjects subjects = (ContactSubjects) response;
                getMvpView().showContactSubjects(subjects.getContactSubjectResponse().getList());
            }
        });
    }

    @Override
    public void selectContactSubject(String subject) {
        getMvpView().onContactSubjectItemSelected(subject);
    }
}
