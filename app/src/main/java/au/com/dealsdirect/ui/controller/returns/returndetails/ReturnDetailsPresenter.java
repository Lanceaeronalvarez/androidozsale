package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedSatisfactionResponse;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ReturnDetailsPresenter<V extends ReturnDetailsMvpView> extends BasePresenter<V> implements ReturnDetailsMvpPresenter<V> {

    private Disposable setReturnReceivedRequestDisposable = null;

    @Inject
    public ReturnDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturnDetails(String returnId) {
        doApiCallForResponse(getDataManager().callGetReturnDetails(returnId), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showCurrentReturnDetails((CurrentReturn) response);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
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
    public void setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest) {
        doApiCallForResponse(getDataManager().setAttachment(returnId, setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                String attachmentId = "";
                if (response instanceof String) {
                    attachmentId = ((String) response).replace("\"", "");
                }
                getMvpView().refreshReturnDetails(attachmentId);

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
    public void sendMessage(String returnId, CreateContactRequest createContactRequest) {

        doApiCallForResponse(getDataManager().callCreateContact(createContactRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                try {
                    setContactNumberForReturns(returnId, Integer.parseInt((String) response));
                } catch (NumberFormatException ignore) {
                    getMvpView().finishedSendMessage("");
                }
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

    private void setContactNumberForReturns(String returnId, int contactNumber) {
        doApiCallForResponse(getDataManager().callSetContactForReturns(returnId, contactNumber), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().finishedSendMessage((String) response);
            }
        });
    }

    @Override
    public int getImageLimit() {
        return getDataManager().getFileSizeLimit();
    }


    @Override
    public void callSetReturnReceived(ReturnReceivedRequest receivedRequest) {
        cancelPreviousSetReturnReceivedRequest();
        setReturnReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetReturnReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    @Override
    public void callSetReturnNotReceived(ReturnReceivedRequest receivedRequest) {
        cancelPreviousSetReturnReceivedRequest();
        setReturnReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetReturnNotReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    private void cancelPreviousSetReturnReceivedRequest() {
        if (setReturnReceivedRequestDisposable != null) {
            getCompositeDisposable().delete(setReturnReceivedRequestDisposable);
            setReturnReceivedRequestDisposable = null;
        }
    }

    @Override
    public void callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest) {
        doApiCallForResponse(getDataManager().callGetReturnReceivedSatisfaction(receivedRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().returnSatisfactionReceived(
                        receivedRequest,
                        response instanceof ReturnReceivedSatisfactionResponse && ((ReturnReceivedSatisfactionResponse) response).getHasRating());
            }
        });
    }
}
