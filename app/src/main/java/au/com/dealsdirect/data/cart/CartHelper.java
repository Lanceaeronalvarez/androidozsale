package au.com.dealsdirect.data.cart;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

public interface CartHelper {
    CartDetailsMapper getCart();
    void saveCart(CartDetailsMapper cart);

    PaymentMethod getSelectedPaymentMethod();

    void setSelectedPaymentMethod(PaymentMethod paymentMethod);
}
