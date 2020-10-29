package au.com.dealsdirect.ui.controller.contact.selectorder;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ContactSelectOrderMvpPresenter<V extends ContactSelectOrderMvpView> extends MvpPresenter<V> {

    void loadContactUsOrders();

    void selectContactOrder(ContactOrderResponse contactOrderResponse);

}
