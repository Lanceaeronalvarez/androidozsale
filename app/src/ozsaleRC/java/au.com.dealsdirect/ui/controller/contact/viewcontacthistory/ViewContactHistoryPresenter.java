package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
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

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactHistory(contactHistoryRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                Log.d("viewcontactshistory", "list is not attached");

                                return;
                            }
                            getMvpView().hideLoading();
                            if (response.getList() != null && !response.getList().isEmpty()) {
                                getMvpView().showContactHistory(response.getList());
                            }

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
    public void replyContact(ReplyContactRequest replyContactRequest) {
        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                        .callReplyContact(replyContactRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            ReplyContactResponse replyReponse = response;
                            //   mViewMyContactContactUs.onLoadEnd();
                            getMvpView().repliedContactSwitchView(replyReponse.getReplyContact());
//                            getMvpView().showContactFirstOrder(myContactOrder);


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
}
