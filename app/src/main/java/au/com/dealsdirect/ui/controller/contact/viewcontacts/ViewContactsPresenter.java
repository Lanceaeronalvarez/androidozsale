package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsPresenter<V extends ViewContactsMvpView> extends BasePresenter<V> implements
        ViewContactsMvpPresenter<V> {

    String testResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"ContactNo\": 3563132,\n" +
            "\t\t\t\"Subject\": \"Where is My Order?\",\n" +
            "\t\t\t\"Comments\": 1,\n" +
            "\t\t\t\"InvoiceNo\": 23883359,\n" +
            "\t\t\t\"SaleName\": \"Fendi Frames \\u0026 Sunglasses\",\n" +
            "\t\t\t\"LastComment\": \"test message sending\",\n" +
            "\t\t\t\"LastAnswer\": \"\\/Date(1483429195450)\\/\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"ContactNo\": 3563130,\n" +
            "\t\t\t\"Subject\": \"Returns Enquiry\",\n" +
            "\t\t\t\"Comments\": 1,\n" +
            "\t\t\t\"InvoiceNo\": 0,\n" +
            "\t\t\t\"SaleName\": null,\n" +
            "\t\t\t\"LastComment\": \"test\",\n" +
            "\t\t\t\"LastAnswer\": \"\\/Date(1483420400463)\\/\"\n" +
            "\t\t}],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    @Inject
    public ViewContactsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadContacts() {
        doApiCallForResponse(getDataManager().callGetContacts(getDataManager().getLanguageId()), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showContactItems(((GetContactsResponse) response).getD());
            }
        });
    }
}
