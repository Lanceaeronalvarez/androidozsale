package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.error.ANError;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by smartwave on 20/06/2017.
 */

public class AddNewAddressPresenter <V extends AddNewAddressMvpView> extends BasePresenter<V> implements
        AddNewAddressMvpPresenter<V> {

    @Inject
    public AddNewAddressPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void addNewAddress(HashMap<DecorationInfoList, View> viewMap) {
        boolean isValid = true;
        JsonObject jsonAddress = new JsonObject();
        Iterator it = viewMap.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry pair = (Map.Entry) it.next();

                DecorationInfoList info = (DecorationInfoList) pair.getKey();

                if (pair.getValue() instanceof EditText) {
                    EditText et = (EditText) pair.getValue();
                    jsonAddress.addProperty(info.getName(), et.getText().toString());
                    Timber.d("ADDRESS", "Key: " + info.getName() + " Value: " + et.getText().toString());
                } else if (pair.getValue() instanceof AppCompatSpinner) {
                    AppCompatSpinner spinner = (AppCompatSpinner) pair.getValue();
                    jsonAddress.addProperty(info.getName(), spinner.getSelectedItem().toString());
                    Timber.d("ADDRESS", "Key: " + info.getName() + " Value: " + spinner.getSelectedItem().toString());
                }

                //Validate
                if (info.getType().equalsIgnoreCase("numeric") || info.getType().equalsIgnoreCase("text")) {
                    if (info.getValidate() != null) {
                        EditText et = (EditText) pair.getValue();
                        String str = et.getText().toString();
                        if (str.isEmpty()) {
                            isValid = false;
                            getMvpView().setFieldErrorState(et);
                        }
                    }
                }

                if (pair.getKey() == null) {
                    it.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!isValid) {
            getMvpView().showErrorMessage("Please populate all fields");
            return;
        }

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .callSetUserDeliveryAddress(new AddAddress.RequestValues(jsonAddress))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<AddAddress.ResponseValue>() {
                    @Override
                    public void accept(@NonNull AddAddress.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (!responseValue.d.isAuthenticated()) {
//                            RxBus.instance().post(Auth.EVENT_NOT_AUTHENTICATED);
                            return;
                        }

                        if (!responseValue.d.getResult()) {
                            getMvpView().showErrorMessage(responseValue.d.getMessage());
                            getMvpView().onError(responseValue.d.getMessage());
                        } else {
                            getMvpView().addNewAddressSuccessful();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                })
        );
    }
}
