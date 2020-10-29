package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryPresenter<V extends ViewContactHistoryMvpView>
        extends BasePresenter<V> implements ViewContactHistoryMvpPresenter<V> {

    @Inject
    public ViewContactHistoryPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadContactHistory(GetContactHistoryRequest contactHistoryRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callGetContactHistory(contactHistoryRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse responseValue = (GetContactHistoryResponse) response;
                if (responseValue != null && responseValue.getMessages() != null && !responseValue.getMessages().isEmpty()) {
                    getMvpView().showContactHistory(responseValue);
                }
            }
        });
    }

    @Override
    public void replyContact(ReplyContactRequest replyContactRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callReplyContact(replyContactRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                String replyReponse = (String) response;
                getMvpView().repliedContactSwitchView(replyReponse);
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
    public void setAttachment(SetAttachmentForContactRequest setAttachmentRequest, boolean hasUploadedImage) {
        doApiCallForResponse(getDataManager().setAttachmentForContact(setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (!hasUploadedImage) {
                    String attachmentId = "";
                    if (response instanceof SetAttachmentResponse) {
                        SetAttachmentResponse setAttachmentResponse = (SetAttachmentResponse) response;
                        attachmentId = setAttachmentResponse.getD().getValue();
                    } else if (response instanceof String) {
                        attachmentId = ((String) response).replace("\"", "");
                    }
                    getMvpView().setAttachmentId(attachmentId);
                } else {
                    getMvpView().refreshViewContactMessage();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }
}
