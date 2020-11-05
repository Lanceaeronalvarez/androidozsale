package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequestOld;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponseOld;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ReturnDetailsPresenter<V extends ReturnDetailsMvpView> extends BasePresenter<V> implements ReturnDetailsMvpPresenter<V> {

    @Inject
    public ReturnDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturnDetails(String returnId) {

        GetReturnDetailRequest getReturnDetailRequest = new GetReturnDetailRequest(returnId);

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetReturnDetails(getReturnDetailRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getReturnDetail -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().showCurrentReturnDetails(getReturnDetail.getGetReturnDetailsResponseBody());

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
    public void loadReturnContacts(GetContactHistoryRequest request) {
        doApiCallForResponse(getDataManager().callGetContactHistory(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse responseValue = null;
                if (response instanceof GetContactHistoryResponse) {
                    responseValue = (GetContactHistoryResponse) response;
                } else if (response instanceof String && !((String) response).isEmpty()) {
                    responseValue = JsonUtils.convertStringToObject((String) response, GetContactHistoryResponse.class);
                }
                if (responseValue != null && responseValue.getMessages() != null && !responseValue.getMessages().isEmpty()) {
                    getMvpView().showContactMessageReturn(responseValue);
                }
            }
        });
    }

    @Override
    public void setAttachment(SetAttachmentRequest setAttachmentRequest) {
        doApiCallForResponse(getDataManager().setAttachment(setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                SetAttachmentResponse setAttachmentResponse = (SetAttachmentResponse) response;
                getMvpView().refreshReturnDetails(setAttachmentResponse);

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }

    @Override
    public String getUserAgent() {
        return getDataManager().getUserAgent();
    }

    @Override
    public String getUserCookies() {
        return getDataManager().getCookies().toString();
    }

    @Override
    public String getEventUserId() {
        return getDataManager().getEventUserId();
    }

    @Override
    public void sendMessage(CreateContactRequestOld createContactRequest) {

        doApiCallForResponse(getDataManager().callCreateContactOld(createContactRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                CreateContactResponseOld myContactSubject = (CreateContactResponseOld) response;
                getMvpView().finishedSendMessage(myContactSubject.getCreateContact().getMessage());
            }
        });
    }

    @Override
    public void replyMessage(ReplyContactRequest replyContactRequest) {

        doApiCallForResponse(getDataManager().callReplyContact(replyContactRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                String replyReponse = (String) response;
                getMvpView().finishedSendMessage(replyReponse);
            }
        });

    }

    @Override
    public int getImageLimit() {
        return getDataManager().getFileSizeLimit();
    }


}
