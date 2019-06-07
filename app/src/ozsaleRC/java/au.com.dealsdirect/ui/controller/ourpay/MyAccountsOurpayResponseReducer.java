package au.com.dealsdirect.ui.controller.ourpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PlannedTransaction;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.Value;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.ScheduledPlan;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.ourpay.MyAccountsOurpayDataSource.Item.ScheduledPayment;
import au.com.dealsdirect.utils.DateUtils;

public class MyAccountsOurpayResponseReducer {
    private Context mContext;

    public static MyAccountsOurpayResponseReducer getInstance(Context context) {
        return new MyAccountsOurpayResponseReducer(context);
    }

    public MyAccountsOurpayResponseReducer(Context context) {
        mContext = context;
    }

    private static class Group<T extends MyAccountsOurpayDataSource.Item> implements MyAccountsOurpayDataSource.Group<T> {
        String mName;
        ArrayList<T> mItems = new ArrayList<>();

        @Override
        public CharSequence getName() {
            return mName;
        }

        @Override
        public List<T> getItems() {
            return mItems;
        }

        @Override
        public int hashCode() {
            return mName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Group) {
                return mName.equals(obj);
            }

            return false;
        }
    }


    public static MyAccountsOurpayDataSource.Summary createSummary(GetPaymentPlansResponse response) {
        return new MyAccountsOurpayDataSource.Summary() {
            Value mValue = response.getValue();

            @Override
            public CharSequence getActivePlans() {
                if (mValue == null) {
                    return null;
                }
                return mValue.getActivePlansCount().toString();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public CharSequence getBalanceOwing() {
                if (mValue == null) {
                    return null;
                }
                return String.format("%s%.2f", Settings.getSelectedCountry().currencySign,
                        mValue.getRemainingBalance());
            }

            @SuppressLint("DefaultLocale")
            @Override
            public CharSequence getRemainingCredit() {
                if (mValue == null) {
                    return null;
                }
                return String.format("%s%.2f", Settings.getSelectedCountry().currencySign,
                        mValue.getRemainingCredit());
            }
        };
    }

    public List<MyAccountsOurpayDataSource.Group<MyAccountsOurpayDataSource.Item.PaymentPlan>> createPaymentPlans(GetPaymentPlansResponse response) {
        if (response == null) {
            return null;
        }

        ArrayList<PaymentPlan> rawPaymentPlans =
                new ArrayList<>(response.getValue().getPaymentPlans());

        if (rawPaymentPlans.size() == 0) {
            return new ArrayList<>();
        }


        // sort first
        Collections.sort(rawPaymentPlans, (o1, o2) -> {
            List<PlannedTransaction> t1 = o1.getPlannedTransactions();
            List<PlannedTransaction> t2 = o2.getPlannedTransactions();

            Calendar cal1 = null;
            Calendar cal2 = null;

            if (t1 != null && t1.size() > 0) {
                cal1 = DateUtils.convertApiEpochtoDateObject(t1.get(0).getPlannedDate());
            }

            if (t2 != null && t2.size() > 0) {
                cal2 = DateUtils.convertApiEpochtoDateObject(t2.get(0).getPlannedDate());
            }

            if (cal2 == null) {
                return cal1 == null ? 0 : -1;
            }

            if (cal1 == null) {
                return 1;
            }

            return cal2.compareTo(cal1);
        });

        // split them up into groups
        return createGroups(
                mContext,
                MyAccountsOurpayDataSource.Item.PaymentPlan.class,
                PaymentPlan.class,
                rawPaymentPlans,
                (context, source) -> {
                    List<PlannedTransaction> plannedTransaction =
                            source.getPlannedTransactions();
                    if (!(plannedTransaction == null || plannedTransaction.size() == 0)) {
                        return plannedTransaction.get(0).getPlannedDate();
                    } else {
                        return null;
                    }
                },
                new DataExtractor[]{
                        (context, source) -> {
                            ArrayList<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> installments = new ArrayList<>();
                            ArrayList<PlannedTransaction> plannedTransactions = new ArrayList<>(
                                    ((PaymentPlan) source).getPlannedTransactions());
                            Collections.sort(plannedTransactions, (o1, o2) -> o1.getNumber().compareTo(o2.getNumber()));

                            for (int j = 0; j < plannedTransactions.size(); j++) {
                                installments.add(new PresenterPaymentPlan.Installment(mContext, plannedTransactions.get(j)));
                            }
                            return installments;
                        }},
                (context, itemType, source, args) -> {
                    List<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> installments = null;
                    if (args.length > 0 && args[0] instanceof List) {
                        installments = (List) args[0];
                    }
                    return itemType.cast(
                            new PresenterPaymentPlan(mContext,
                                    (PaymentPlan) source,
                                    installments));
                });
    }

    public List<MyAccountsOurpayDataSource.Group<ScheduledPayment>> createScheduledPayments(GetScheduledPlansResponse response) {
        if (response == null) {
            return null;
        }

        ArrayList<ScheduledPlan> rawScheduledPayments =
                new ArrayList<>(response.getScheduledPayment());

        if (rawScheduledPayments.size() == 0) {
            return new ArrayList<>();
        }

        // sort first
        Collections.sort(rawScheduledPayments, (o1, o2) -> {
            Calendar cal1 = DateUtils.convertApiEpochtoDateObject(o1.getPlannedDate());
            Calendar cal2 = DateUtils.convertApiEpochtoDateObject(o2.getPlannedDate());

            if (cal1 == null) {
                return cal2 == null ? 0 : -1;
            }

            if (cal2 == null) {
                return 1;
            }

            return cal1.compareTo(cal2);
        });

        // split them up into groups
        return createGroups(
                mContext,
                ScheduledPayment.class,
                ScheduledPlan.class,
                rawScheduledPayments,
                (context, source) -> source.getPlannedDate(),
                null,
                (context, itemType, source, args) -> itemType.cast(
                        new PresenterScheduledPayment(mContext, (ScheduledPlan) source)));
    }

    public List<MyAccountsOurpayDataSource.Group<ScheduledPayment>> createPastPayments(GetPastPaymentsResponse response) {
        if (response == null) {
            return null;
        }

        ArrayList<PastPayment> rawPastPayments =
                new ArrayList<>(response.getPastPayment());

        if (rawPastPayments.size() == 0) {
            return new ArrayList<>();
        }

        // sort first
        Collections.sort(rawPastPayments, (o1, o2) -> {
            Calendar cal1 = DateUtils.convertApiEpochtoDateObject(o1.getPlannedDate());
            Calendar cal2 = DateUtils.convertApiEpochtoDateObject(o2.getPlannedDate());

            if (cal2 == null) {
                return cal1 == null ? 0 : -1;
            }

            if (cal1 == null) {
                return 1;
            }

            return cal2.compareTo(cal1);
        });

        // split them up into groups
        return createGroups(
                mContext,
                ScheduledPayment.class,
                PastPayment.class,
                rawPastPayments,
                (context, source) -> source.getPlannedDate(),
                null,
                (context, itemType, source, args) -> itemType.cast(new PresenterPastPayment(context,
                        (PastPayment) source)));
    }

    private interface ApiStringExtractor<T> {
        String getApiString(Context context, T source);
    }

    private interface DataExtractor {
        Object getData(Context context, Object source);
    }

    private interface ItemConstructor<T> {
        T newInstance(Context context, Class<T> itemType, Object source, Object... args);
    }

    private static <T extends MyAccountsOurpayDataSource.Item, U> List<MyAccountsOurpayDataSource.Group<T>> createGroups(Context context,
                                                                                                                         Class<T> itemType, Class<U> sourceType,
                                                                                                                         List<U> source,
                                                                                                                         ApiStringExtractor<U> apiStringExtractor,
                                                                                                                         DataExtractor[] dataExtractors,
                                                                                                                         ItemConstructor<T> itemConstructor) {
        ArrayList<MyAccountsOurpayDataSource.Group<T>> groups = new ArrayList<>();
        ArrayList<Object> constructorArguments = new ArrayList<>();

        // if source is sorted, keys would be sorted too
        ArrayList<String> groupNames = new ArrayList<>();
        HashMap<String, Group<T>> mappedGroups = new HashMap<>();

        // split them up into groups
        Group<T> currentGroup = null;
        SimpleDateFormat groupsDateFormat = new SimpleDateFormat(context.getResources()
                .getString(R.string.ourpay_section_header_date_format), Locale.ENGLISH);
        for (int i = 0; i < source.size(); i += 1) {
            U rawItem = source.get(i);

            String apiDateString = apiStringExtractor.getApiString(context, rawItem);

            if (apiDateString != null) {
                Calendar cal = DateUtils.convertApiEpochtoDateObject(apiDateString);
                if (cal != null) {
                    String groupName = groupsDateFormat.format(Objects.requireNonNull(cal).getTime());
                    currentGroup = mappedGroups.get(groupName);

                    if (currentGroup == null) {
                        currentGroup = new Group<>();
                        currentGroup.mName = groupName;
                        mappedGroups.put(groupName, currentGroup);
                        //hopefully, the items were already sorted at this point
                        groupNames.add(groupName);
                    }
                }
            }

            if (currentGroup != null) {
                if (dataExtractors == null) {
                    currentGroup.mItems.add(itemConstructor.newInstance(context, itemType, rawItem));
                } else {
                    constructorArguments.clear();

                    for (DataExtractor dataExtractor : dataExtractors) {
                        constructorArguments.add(dataExtractor.getData(context, rawItem));
                    }

                    currentGroup.mItems.add(itemConstructor
                            .newInstance(context, itemType, rawItem, constructorArguments.toArray()));
                }
            }
        }

        for (String groupName : groupNames) {
            groups.add(mappedGroups.get(groupName));
        }

        return groups;
    }

    private static class PresenterPaymentPlan implements MyAccountsOurpayDataSource.Item.PaymentPlan {
        private au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan mSource;
        private List<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> mInstallments;

        private Context mContext;

        PresenterPaymentPlan(Context context,
                             au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan source,
                             List<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> installments) {
            mContext = context;
            mSource = source;
            if (installments == null || installments.size() == 0) {
                mInstallments = null;
            } else {
                mInstallments = installments;
            }
        }

        @Override
        public CharSequence getName() {
            return "#" + mSource.getOrderNo();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getTotal() {
            return String.format("%s %s%.2f",
                    mContext.getResources().getString(R.string.ourpay_order),
                    Settings.getSelectedCountry().currencySign, mSource.getTotalAmount());
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getPaid() {
            String htmlString = String.format("<b>%s</b> %s%.2f",
                    mContext.getResources().getString(R.string.ourpay_paid),
                    Settings.getSelectedCountry().currencySign, mSource.getTotalAmount());

            return Html.fromHtml(htmlString);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getRefund() {
            String htmlString = String.format("<b>%s</b> %s%d.00",
                    mContext.getResources().getString(R.string.ourpay_refund),
                    Settings.getSelectedCountry().currencySign, mSource.getRefundAmount());

            return Html.fromHtml(htmlString);
        }

        @Override
        public CharSequence getAdjustedSchedule() {
            // API has not implemented this yet
            return null;
        }

        @Override
        public CharSequence getRefundToCard() {
            // API has not implemented this yet
            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getBalance() {
            String htmlString = String.format("<b>%s</b> %s%.2f",
                    mContext.getResources().getString(R.string.ourpay_balance),
                    Settings.getSelectedCountry().currencySign, mSource.getOrderBalance());

            return Html.fromHtml(htmlString);
        }

        @Override
        public CharSequence getObfuscatedCardNumber() {
            // Not applicable
            return null;
        }

        @Override
        public Drawable getPaymentMethodIconImage(Context context) {
            // Not applicable
            return null;
        }

        @Override
        public List<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> getInstallments() {
            return mInstallments;
        }

        private static class Installment implements MyAccountsOurpayDataSource.Item.PaymentPlan.Installment {
            private SimpleDateFormat mInstallmentDateFormat;
            private PlannedTransaction mSource;

            private Context mContext;

            Installment(Context context, PlannedTransaction source) {
                mContext = context;
                mSource = source;

                mInstallmentDateFormat = new SimpleDateFormat(
                        mContext.getResources().getString(R.string.ourpay_installment_date_format),
                        Locale.ENGLISH);
            }

            @Override
            public State getState() {
                return State.fromString(mSource.getState());
            }

            @Override
            public CharSequence getDate() {
                return mInstallmentDateFormat.format(
                        Objects.requireNonNull(DateUtils.convertApiEpochtoDateObject(
                                mSource.getPlannedDate())).getTime());
            }

            @SuppressLint("DefaultLocale")
            @Override
            public CharSequence getAmount() {
                return String.format("%s%.2f",
                        Settings.getSelectedCountry().currencySign,
                        mSource.getAmount());
            }
        }
    }

    private static class PresenterScheduledPayment implements ScheduledPayment {
        private SimpleDateFormat mBigDateFormat;
        private SimpleDateFormat mSmallDateFormat;

        private Context mContext;
        private ScheduledPlan mSource;

        PresenterScheduledPayment(Context context, ScheduledPlan source) {
            mContext = context;
            mSource = source;

            mBigDateFormat = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.ourpay_big_date_format),
                    Locale.ENGLISH);
            mSmallDateFormat = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.ourpay_small_date_format),
                    Locale.ENGLISH);
        }

        @Override
        public CharSequence getName() {
            return "#" + mSource.getOrderNo();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getTotal() {
            return String.format("%s %s%.2f",
                    mContext.getResources().getString(R.string.ourpay_order),
                    Settings.getSelectedCountry().currencySign,
                    mSource.getAmount());
        }

        @Override
        public CharSequence getBigDate() {
            return mBigDateFormat.format(
                    Objects.requireNonNull(DateUtils.convertApiEpochtoDateObject(
                            mSource.getPlannedDate())).getTime());
        }

        @Override
        public CharSequence getSmallDate() {
            return mSmallDateFormat.format(
                    Objects.requireNonNull(DateUtils.convertApiEpochtoDateObject(
                            mSource.getPlannedDate())).getTime());
        }

        @Override
        public CharSequence getObfuscatedCardNumber() {
            return mSource.getMaskedNumber();
        }

        @Override
        public Drawable getPaymentMethodIconImage(Context context) {
            Integer id = PAYMENT_METHOD_IMAGE_IDS.get(mSource.getPaymentMethod());
            if (id == null) {
                return null;
            } else {
                return context.getResources().getDrawable(id);
            }
        }
    }

    private static class PresenterPastPayment implements ScheduledPayment {
        private SimpleDateFormat mBigDateFormat;
        private SimpleDateFormat mSmallDateFormat;

        private PastPayment mSource;

        private Context mContext;

        PresenterPastPayment(Context context,
                             PastPayment source) {
            mContext = context;
            mSource = source;

            mBigDateFormat = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.ourpay_big_date_format),
                    Locale.ENGLISH);
            mSmallDateFormat = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.ourpay_small_date_format),
                    Locale.ENGLISH);
        }

        @Override
        public CharSequence getName() {
            return "#" + mSource.getOrderNo();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public CharSequence getTotal() {
            return String.format("%s %s%.2f",
                    mContext.getResources().getString(R.string.ourpay_order),
                    Settings.getSelectedCountry().currencySign,
                    mSource.getAmount());
        }

        @Override
        public CharSequence getBigDate() {
            return mBigDateFormat.format(
                    Objects.requireNonNull(DateUtils.convertApiEpochtoDateObject(
                            mSource.getPlannedDate())).getTime());
        }

        @Override
        public CharSequence getSmallDate() {
            return mSmallDateFormat.format(
                    Objects.requireNonNull(DateUtils.convertApiEpochtoDateObject(
                            mSource.getPlannedDate())).getTime());
        }

        @Override
        public CharSequence getObfuscatedCardNumber() {
            return mSource.getMaskedNumber();
        }

        @Override
        public Drawable getPaymentMethodIconImage(Context context) {
            Integer id = PAYMENT_METHOD_IMAGE_IDS.get(mSource.getPaymentMethod());
            if (id == null) {
                return null;
            } else {
                return context.getResources().getDrawable(id);
            }
        }
    }

    private static HashMap<String, Integer> PAYMENT_METHOD_IMAGE_IDS = new HashMap<String, Integer>() {{
        put("PayPal", R.drawable.paypal);
        put("Visa", R.drawable.visa);
        put("MasterCard", R.drawable.mastercard);
        put("American_Express", R.drawable.amex);
        put("MasterPass", R.drawable.masterpass);
        put("VisaCheckout", R.drawable.visa_checkout);
    }};
}
