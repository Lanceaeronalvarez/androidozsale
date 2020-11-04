package au.com.dealsdirect.ui.controller.contact.addcontact;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactsubjecttemplates.ContactSubjectTemplatesResponse;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactPresenter<V extends AddContactMvpView> extends BasePresenter<V> implements
        AddContactMvpPresenter<V> {

    @Inject
    public AddContactPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createNewContact(CreateContactRequest createContactRequest) {
        getMvpView().showLoading();

        final AppApiCallback callback = new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().contactCreatedSwitchView((String) response);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().contactCreatedSwitchView(null);
            }
        };

        if (getDataManager().isAuthorized()) {
            doApiCallForResponse(getDataManager().callCreateContact(createContactRequest), callback);
        } else {
            doApiCallForResponse(getDataManager().callCreateContactPublic(createContactRequest), callback);
        }
    }

    @Override
    public void setAttachment(SetAttachmentForContactRequest setAttachmentRequest, boolean hasUploadedImage) {
        doApiCallForResponse(getDataManager().setAttachmentForContact(setAttachmentRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (!hasUploadedImage) {
                    String attachmentId = "";
                    if (response instanceof SetAttachmentResponse) {
                        SetAttachmentResponse setAttachmentResponse = (SetAttachmentResponse) response;
                        attachmentId = setAttachmentResponse.getD().getValue();
                    } else if (response instanceof String) {
                        attachmentId = ((String) response).replace("\"", "");
                    }
                    getMvpView().setAttachmentId(attachmentId);
                } else {
                    getMvpView().showViewContactHistory();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }

    @Override
    public int getImageLimit() {
        return getDataManager().getFileSizeLimit();
    }

    @Override
    public String getUserAgent() {
        return getDataManager().getUserAgent();
    }

    @Override
    public void loadContactHistory(GetContactHistoryRequest contactHistoryRequest) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callGetContactHistory(contactHistoryRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse responseValue = (GetContactHistoryResponse) response;
                if (responseValue != null && responseValue.getMessages() != null && !responseValue.getMessages().isEmpty()) {
                    getMvpView().showContactSuccess(responseValue);
                }
            }
        });
    }

    @Override
    public void getContactSubjectsTemplates(String id) {
        doApiCallForResponse(getDataManager().callGetContactSubjectsTemplates(id), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (response instanceof String) {
                    String source = (String) response;
                    if (source.isEmpty()) {
                        getMvpView().showContactSuggestions(null);
                    } else {
                        getMvpView().showContactSuggestions(
                                JsonUtils.convertStringToObject(source, ContactSubjectTemplatesResponse.class));
                    }
                }
            }
        });
    }

    @Override
    public void getOrderTrackingDetails(int orderNumber, int invoiceNumber) {
        doApiCallForResponse(getDataManager().callGetOrderTracking(orderNumber, invoiceNumber), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (response instanceof GetOrdersResponse.Order.Invoice.Delivery) {
                    getMvpView().showOrderTracker((GetOrdersResponse.Order.Invoice.Delivery) response);
                }
            }
        });
    }

    @Override
    public void changeDeliveryAddress(ChangeDeliveryAddressRequest changeDeliveryAddressRequest, Integer orderNumber) {
        if (changeDeliveryAddressRequest.getInvoiceId() == null) {
            if (orderNumber != null) {
                doApiCallForResponse(getDataManager().callGetOrderDetails(orderNumber), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        String invoiceId = null;

                        GetOrdersResponse.Order getOrdersResponse = (GetOrdersResponse.Order) response;
                        for (GetOrdersResponse.Order.Invoice invoice : getOrdersResponse.getInvoices()) {
                            if (invoice.getNumber() == changeDeliveryAddressRequest.getInvoiceNumber()) {
                                invoiceId = invoice.getId();
                                break;
                            }
                        }

                        if (invoiceId != null) {
                            ChangeDeliveryAddressRequest newRequest = new ChangeDeliveryAddressRequest();
                            newRequest.setAddressId(changeDeliveryAddressRequest.getAddressId());
                            newRequest.setInvoiceId(invoiceId);
                            newRequest.setInvoiceNumber(changeDeliveryAddressRequest.getInvoiceNumber());
                            doApiCallForResponse(getDataManager().callChangeDeliveryAddress(newRequest), new AppApiCallback() {
                                @Override
                                public void onSuccess(Object response) {
                                    super.onSuccess(response);
                                    getMvpView().showDeliveryAddressChanged(true, getOrdersResponse);
                                }
                            });
                        } else {
                            getMvpView().showDeliveryAddressChanged(false, getOrdersResponse);
                        }

                    }
                });
            } else {
                getMvpView().showDeliveryAddressChanged(false, null);
            }
            return;
        }

        doApiCallForResponse(getDataManager().callChangeDeliveryAddress(changeDeliveryAddressRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showDeliveryAddressChanged(true, null);
            }
        });
    }
}
