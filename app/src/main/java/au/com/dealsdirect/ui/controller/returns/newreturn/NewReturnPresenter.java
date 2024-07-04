package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class NewReturnPresenter<V extends NewReturnMvpView> extends BasePresenter<V> implements NewReturnMvpPresenter<V> {

    @Inject
    public NewReturnPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void addNewReturnOrderRequest(CreateReturnRequest createReturnRequest) {
        getMvpView().showLoading(LoadingDialogType.DEFAULT);
        doApiCallForResponse(getDataManager().callCreateReturnRequest(createReturnRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                CreateReturnRequestResponse getNewReturnCreateResponse = (CreateReturnRequestResponse) response;
                getMvpView().finishCreateReturnRequest(getNewReturnCreateResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().finishCreateReturnRequest(null);
            }
        });

    }

    @Override
    public void getReturnOrderDetail(int invoiceNo) {
        doApiCallForResponse(getDataManager()
                .callGetNewReturnOrderDetail(Integer.toString(invoiceNo)), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().loadReturnOrderDetail((List<NewReturnItem>) response);
            }
        });
    }

    @Override
    public void setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest, boolean hasUploadedImage) {
        doApiCallForResponse(getDataManager().setAttachment(returnId, setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                final String setAttachmentResponse = ((String) response).replace("\"", "");

                if (!hasUploadedImage) {
                    getMvpView().getAttachmentId(setAttachmentResponse);
                } else {
                    getMvpView().finishReturnRequestTransaction();
                }

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
    public String getEventUser() {
        return getDataManager().getEventUserId();
    }

    @Override
    public int getImageLimit() {
        return getDataManager().getFileSizeLimit();
    }
}
