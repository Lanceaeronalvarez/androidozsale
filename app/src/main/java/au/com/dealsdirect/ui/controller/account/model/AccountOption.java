package au.com.dealsdirect.ui.controller.account.model;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import au.com.dealsdirect.R;

public enum AccountOption {
    MYDETAILS(R.string.account_details, true),
    ADDRESSES(R.string.account_addresses, true),
    ORDERS(R.string.account_orders, true),
    VOUCHERS(R.string.account_vouchers, true),
    RETURNS(R.string.account_returns, true),
    RETURNSPOLICY(R.string.account_returns_policy, false),
    CONTACTUS(R.string.account_contact_us, true),
    PAYMENTS(R.string.account_payments, true),
    INVITEFRIEND(R.string.account_invite_friend, true),
    LANGUAGE(R.string.account_language, false),
    ABOUTUS(R.string.account_about_us, false),
    PRIVACYPOLICY(R.string.account_privacy, false),
    TERMSANDCONDITIONS(R.string.account_tnc, false),
    COUNTRY(R.string.account_country, false),
    NOTIFICATIONS(R.string.account_notification, false),
    TUTORIAL(R.string.account_tutorial, false),
    LOGOUT(R.string.account_logout, false),
    INFORMATION(R.string.account_information, false),
    GCTERMSANDCONDITIONS(R.string.account_gc_tnc, false);

    private static final  Map<Integer, AccountOption> ENUM_MAP;

    private int titleResourceId;
    private boolean needsAuthentication;

    AccountOption(int titleResourceId, boolean needsAuthentication) {
        this.titleResourceId = titleResourceId;
        this.needsAuthentication = needsAuthentication;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public boolean isNeedsAuthentication() {
        return needsAuthentication;
    }

    static {
        Map<Integer, AccountOption> map = new ConcurrentHashMap<Integer, AccountOption>();
        for (AccountOption accountOption : AccountOption.values()) {
            map.put(accountOption.getTitleResourceId(), accountOption);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    @Nullable
    public static AccountOption getFromStringResourceId(int stringResourceId) {
        return ENUM_MAP.get(stringResourceId);
    }
}
