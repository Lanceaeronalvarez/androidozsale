package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
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

        doApiCallForResponse(getDataManager().callGetContactHistory(contactHistoryRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse.ResponseValue responseValue = (GetContactHistoryResponse.ResponseValue) response;
                if (responseValue.getList() != null && !responseValue.getList().isEmpty()) {
                    getMvpView().showContactHistory(responseValue.getList());
                }
            }
        });
    }

    @Override
    public void replyContact(ReplyContactRequest replyContactRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callReplyContact(replyContactRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                ReplyContactResponse replyReponse = (ReplyContactResponse) response;
                getMvpView().repliedContactSwitchView(replyReponse.getReplyContact());
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
    public void setAttachment(SetAttachmentRequest setAttachmentRequest, boolean hasUploadedImage) {
        doApiCallForResponse(getDataManager().setAttachment(setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                SetAttachmentResponse setAttachmentResponse = (SetAttachmentResponse) response;

                if (!hasUploadedImage) {
                    getMvpView().getAttachmentId(setAttachmentResponse);
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
