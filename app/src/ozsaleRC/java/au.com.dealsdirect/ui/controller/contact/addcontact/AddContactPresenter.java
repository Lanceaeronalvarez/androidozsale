package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactPresenter<V extends AddContactMvpView> extends BasePresenter<V> implements
        AddContactMvpPresenter<V> {

    @Inject
    public AddContactPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createNewContact(CreateContactRequest createContactRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callCreateContact(createContactRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                CreateContactResponse myContactSubject = (CreateContactResponse) response;
                getMvpView().contactCreatedSwitchView(myContactSubject);
            }
        });
    }

}
