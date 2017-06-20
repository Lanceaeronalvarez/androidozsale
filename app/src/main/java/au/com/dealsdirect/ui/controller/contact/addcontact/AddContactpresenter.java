package au.com.dealsdirect.ui.controller.contact.addcontact;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactpresenter<V extends AddContactMvpView> extends BasePresenter<V> implements
        AddContactMvpPresenter<V> {

    @Inject
    public AddContactpresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
