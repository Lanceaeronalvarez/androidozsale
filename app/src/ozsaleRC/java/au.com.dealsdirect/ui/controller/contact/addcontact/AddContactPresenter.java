package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
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

    @Override
    public void loadContactUsSubjects() {
        getMvpView().showLoading();

        ContactSubjectsRequest contactSubjectsRequest
                = new ContactSubjectsRequest(getDataManager().getCountryId(),getDataManager().getLanguageId());
        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactSubjects(contactSubjectsRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            List<String> myContactSubject = response.getContactSubjectResponse().getList();
                            Log.d("clickable", "size = "+myContactSubject.size());
                            getMvpView().showContactFirstSubject(myContactSubject);


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
    public void loadContactUsOrders() {
        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactOrders()
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            List<ContactOrderList> myContactOrder
                                    = response.getContactOrderResponse().getList();

                            getMvpView().showContactFirstOrder(myContactOrder);


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
    public void createNewContact(CreateContactRequest createContactRequest) {
        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                        .callCreateContact(createContactRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            CreateContactResponse myContactSubject = response;
                            //   mViewMyContactContactUs.onLoadEnd();
                            getMvpView().contactCreatedSwitchView(myContactSubject);
//                            List<ContactOrderList> myContactOrder
//                                    = response.getCreateContact().getList();

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
