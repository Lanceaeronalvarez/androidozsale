package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
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

    @Override
    public void setAttachment(SetAttachmentRequest setAttachmentRequest, boolean hasUploadedImage) {
        doApiCallForResponse(getDataManager().setAttachment(setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                SetAttachmentResponse setAttachmentResponse = (SetAttachmentResponse) response;

                if (!hasUploadedImage) {
                    getMvpView().getAttachmentId(setAttachmentResponse);
                } else {
                    getMvpView().showViewContactHistory();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }

    @Override
    public int getImageLimit() {
        return getDataManager().getFileSizeLimit();
    }

    @Override
    public String getUserAgent() {
        return getDataManager().getUserAgent();
    }

    @Override
    public void loadContactHistory(GetContactHistoryRequest contactHistoryRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callGetContactHistory(contactHistoryRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse.ResponseValue responseValue = (GetContactHistoryResponse.ResponseValue) response;
                if (responseValue.getList() != null && !responseValue.getList().isEmpty()) {
                    getMvpView().showContactSuccess(responseValue.getList());
                }
            }
        });
    }

}
