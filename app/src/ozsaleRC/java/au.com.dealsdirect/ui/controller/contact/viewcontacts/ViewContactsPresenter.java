package au.com.dealsdirect.ui.controller.contact.viewcontacts;

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

    @Override
    public void selectContact(GetContactsResponse.ContactList contactList) {
        getMvpView().onContactClicked(contactList);
    }
}
