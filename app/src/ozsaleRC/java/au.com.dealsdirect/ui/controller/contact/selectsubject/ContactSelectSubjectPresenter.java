package au.com.dealsdirect.ui.controller.contact.selectsubject;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
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

        ContactSubjectsRequest contactSubjectsRequest
                = new ContactSubjectsRequest(getDataManager().getCountryId(),getDataManager().getLanguageId());
        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactSubjects(contactSubjectsRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            List<String> myContactSubject = response.getContactSubjectResponse().getList();
                            Log.d("clickable", "size = "+myContactSubject.size());
                            getMvpView().showContactSubjects(myContactSubject);


                        }, throwable -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            getMvpView().onError(throwable.getMessage());

                            // handle load accounts error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }));
    }

    @Override
    public void selectContactSubject(String subject) {
        getMvpView().onContactSubjectItemSelected(subject);
    }
}
