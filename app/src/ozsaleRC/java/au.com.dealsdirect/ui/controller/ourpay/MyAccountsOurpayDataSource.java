package au.com.dealsdirect.ui.controller.ourpay;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.List;

public interface MyAccountsOurpayDataSource {
    <T extends Item> List<Group<? extends Item>> getData(MyAccountsOurpayCellAdapter adapter);

    interface Summary {
        CharSequence getActivePlans();

        CharSequence getBalanceOwing();

        CharSequence getRemainingCredit();
    }

    interface Group<T extends Item> {
        CharSequence getName();

        List<T> getItems();
    }

    interface Item {
        interface PaymentPlan extends Item {
            CharSequence getName();

            CharSequence getTotal();

            CharSequence getPaid();

            CharSequence getRefund();

            CharSequence getAdjustedSchedule();

            CharSequence getRefundToCard();

            CharSequence getBalance();

            CharSequence getObfuscatedCardNumber();

            Drawable getPaymentMethodIconImage(Context context);

            List<PaymentPlan.Installment> getInstallments();

            interface Installment {
                PaymentPlan.Installment.State getState();

                CharSequence getDate();

                CharSequence getAmount();

                enum State {
                    Pending, Success, Fail;

                    static State fromString(String string) {
                        switch (string.toLowerCase()) {
                            case "success":
                            case "successful":
                            case "succeeded":
                                return Success;
                            case "fail":
                            case "failure":
                            case "failed":
                                return Fail;
                            default:
                                return Pending;
                        }
                    }
                }
            }
        }

        interface ScheduledPayment extends Item {
            CharSequence getName();

            CharSequence getTotal();

            CharSequence getBigDate();

            CharSequence getSmallDate();

            CharSequence getObfuscatedCardNumber();

            Drawable getPaymentMethodIconImage(Context context);

            CharSequence getTransactionId();

            CharSequence getBillingAgreementId();
        }
    }
}
