package au.com.dealsdirect.ui.controller.address.addnewaddress;

import androidx.appcompat.widget.AppCompatSpinner;

import android.view.View;
import android.widget.EditText;

import com.androidnetworking.error.ANError;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by smartwave on 20/06/2017.
 */

public class AddNewAddressPresenter<V extends AddNewAddressMvpView> extends BasePresenter<V> implements
        AddNewAddressMvpPresenter<V> {

    @Inject
    public AddNewAddressPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void addNewAddress(HashMap<DecorationInfoList, View> viewMap) {
        boolean isValid = true;
        boolean hasSpecialChar = false;
        JsonObject jsonAddress = new JsonObject();
        Iterator it = viewMap.entrySet().iterator();
        String postcode = "postcode";
        String forename = "forename";
        String surname = "surname";
        while (it.hasNext()) {
            try {
                Map.Entry pair = (Map.Entry) it.next();

                DecorationInfoList info = (DecorationInfoList) pair.getKey();

                if (pair.getValue() instanceof EditText) {
                    EditText et = (EditText) pair.getValue();
                    String editTextValue = et.getText().toString();
                    String label = info.getName().toLowerCase();

                    if (label.equalsIgnoreCase("addresslines")) {
                        jsonAddress.addProperty("address_lines", editTextValue);
                    } else {
                        jsonAddress.addProperty(label, editTextValue);
                    }

                    if (!(editTextValue.length() >= info.getMinLength() && editTextValue.length() <= info.getMaxLength()) &&
                            !label.equalsIgnoreCase(postcode)) {
                        if (info.getMinLength() == info.getMaxLength()) {
                            getMvpView().onError(String.format(
                                    et.getContext().getString(R.string.address_error_format_equal),
                                    info.getLabel(),
                                    String.valueOf(info.getMinLength())));
                        } else {
                            getMvpView().onError(String.format(
                                    et.getContext().getString(R.string.address_error_format),
                                    info.getLabel(), String.valueOf(info.getMinLength()),
                                    String.valueOf(info.getMaxLength())));
                        }
                        return;
                    }
                    //Validate Name field
                    if (info.getValidate() != null && label.equalsIgnoreCase(forename) || label.equalsIgnoreCase(surname)) {
                        String str = editTextValue;

                        if (!str.trim().matches("[a-zA-Z ]+") || str.length() < 2) {
                            isValid = false;
                            hasSpecialChar = true;
                            getMvpView().setFieldErrorState(et);
                        }
                    }
                    Timber.d("ADDRESS", "Key: " + label + " ScheduledPlan: " + editTextValue);
                } else if (pair.getValue() instanceof AppCompatSpinner) {
                    AppCompatSpinner spinner = (AppCompatSpinner) pair.getValue();
                    jsonAddress.addProperty(info.getName(), spinner.getSelectedItem().toString());
                    Timber.d("ADDRESS", "Key: " + info.getName() + " ScheduledPlan: " + spinner.getSelectedItem().toString());
                }

                //Validate
                if (info.getType().equalsIgnoreCase("numeric") || info.getType().equalsIgnoreCase("text")) {
                    if (info.getValidate() != null && !info.getName().equalsIgnoreCase(postcode)) {
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

        if (!isValid && isViewAttached()) {
            if (hasSpecialChar) {
                getMvpView().showErrorMessage(String.valueOf(R.string.special_character_error));
            } else {
                getMvpView().showErrorMessage("Please populate all fields");
            }
            return;
        }

        getMvpView().showLoading(LoadingDialogType.DEFAULT);
        doApiCallForResponse(getDataManager()
                .callSetUserDeliveryAddress(jsonAddress), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoading();

                getMvpView().addNewAddressSuccessful();
            }

            @Override
            public void onFailure(Throwable throwable) {
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
        });
    }
}
