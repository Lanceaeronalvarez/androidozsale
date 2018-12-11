package au.com.dealsdirect.data.network.model.accountdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by smartwave on 02/08/2018.
 */

public class AccountData {

    @SerializedName("accountId")
    @Expose
    private String accountId;
    @SerializedName("ourPay")
    @Expose
    private OurPay ourPay;
    @SerializedName("facebook")
    @Expose
    private Facebook facebook;
    @SerializedName("promoBanner")
    @Expose
    private PromoBanner promoBanner;
    @SerializedName("sorting")
    @Expose
    private Sorting sorting;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public OurPay getOurPay() {
        return ourPay;
    }

    public void setOurPay(OurPay ourPay) {
        this.ourPay = ourPay;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public PromoBanner getPromoBanner() {
        return promoBanner;
    }

    public void setPromoBanner(PromoBanner promoBanner) {
        this.promoBanner = promoBanner;
    }

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
    }

    public static class Facebook {

        @SerializedName("appId")
        @Expose
        private String appId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

    }

    public static class OurPay {

        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

    public static class PromoBanner {

        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

    public static class Sorting {

        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

}
