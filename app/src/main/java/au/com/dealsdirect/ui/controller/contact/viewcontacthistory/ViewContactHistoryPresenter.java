package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryPresenter<V extends ViewContactHistoryMvpView>
        extends BasePresenter<V> implements ViewContactHistoryMvpPresenter<V>{

    @Inject
    public ViewContactHistoryPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadContactHistory(int contactId) {
        getMvpView().showLoading();

        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.contactNo = contactId;

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactHistory(getContactHistoryRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                Log.d("viewcontactshistory","list is not attached");

                                return;
                            }
                            getMvpView().hideLoading();
                            if (response.getGetContactHistoryResponseBody().getList()!=null){
                                Log.d("viewcontactshistory","list is null empty");
                                List<au.com.dealsdirect.data.network.model.contacthistory.List> myContactItems = response.getGetContactHistoryResponseBody().getList();

                                if(myContactItems!=null && !myContactItems.isEmpty()){

                                    Log.d("viewcontactshistory","list is not empty");
                                    getMvpView().showContactHistory(myContactItems);

                                }else{
                                    Log.d("viewcontactshistory","list is empty");

                                }
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
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
                            }
                        }));
    }

}
