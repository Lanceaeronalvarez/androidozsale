package au.com.dealsdirect.ui.controller.contact.selectsubject;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ContactSelectSubjectPresenter<V extends ContactSelectSubjectMvpView> extends BasePresenter<V> implements ContactSelectSubjectMvpPresenter<V> {

    @Inject
    public ContactSelectSubjectPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadContactUsSubjects() {
        getMvpView().showLoading(LoadingDialogType.DEFAULT);

        doApiCallForResponse(getDataManager().callGetContactSubjects(!getDataManager().isAuthorized()), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showContactSubjects((List<ContactSubjectResponse>) response);
            }
        });
    }
}
