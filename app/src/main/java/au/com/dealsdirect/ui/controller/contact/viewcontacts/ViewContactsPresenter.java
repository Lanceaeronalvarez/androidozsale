package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsPresenter<V extends ViewContactsMvpView> extends BasePresenter<V> implements
        ViewContactsMvpPresenter<V> {

    @Inject
    public ViewContactsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadContacts() {
        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContacts(getDataManager().getLanguageId())
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {

                                return;
                            }
                            getMvpView().hideLoading();
                            if (response.getD().getList()!=null){
                                Log.d("viewcontacts","list is null empty");

                                if(!response.getD().getList().isEmpty()){

                                    Log.d("viewcontacts","list is not empty");
                                    getMvpView().showContactItems(response.getD());
                                }else{
                                    Log.d("viewcontacts","list is empty");

                                }
                            }else {
//                                Log.d("viewcontacts", "list  = "+response.getMessage() + " , "+response.getResult());
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
