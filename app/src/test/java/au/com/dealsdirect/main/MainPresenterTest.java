package au.com.dealsdirect.main;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetServerSettings;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.ui.main.MainPresenter;
import au.com.dealsdirect.ui.main.MainMvpPresenter;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 10/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    private static final String mMockGetServerSettingsResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"ScheduledPlan\": {\n" +
            "      \"Country\": {\n" +
            "        \"ID\": \"DA\",\n" +
            "        \"Iso\": \"AU\",\n" +
            "        \"TaxName\": \"GST\",\n" +
            "        \"TaxAmount\": 0.1,\n" +
            "        \"TimeOffset\": 0,\n" +
            "        \"Languages\": [\n" +
            "          {\n" +
            "            \"ID\": \"en\",\n" +
            "            \"Name\": \"English\",\n" +
            "            \"Culture\": \"en-au     \"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"CountryISO3Code\": \"AUS\",\n" +
            "        \"LanguageISO3Codes\": [\n" +
            "          {\n" +
            "            \"ID\": \"en\",\n" +
            "            \"Iso3Code\": \"ENG\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"IsDebugMode\": false,\n" +
            "      \"DataBaseName\": \"apacsale\",\n" +
            "      \"SiteShortName\": \"dealsdirect.com.au\",\n" +
            "      \"SiteFullName\": \"dealsdirect.com.au\",\n" +
            "      \"Currency\": \"AUD\",\n" +
            "      \"CurrencySign\": \"$\",\n" +
            "      \"ImageServerURL\": \"https://c1.mysalec.com/\",\n" +
            "      \"AuthenticationCookieName\": \"api.apacsale\",\n" +
            "      \"ServerVersion\": \"4.0.2013.2606\",\n" +
            "      \"ShowMobilePromotion\": false,\n" +
            "      \"ShowiPhoneBanner\": true,\n" +
            "      \"FacebookAppID\": \"1650094968544406\",\n" +
            "      \"FollowUsVisible\": true,\n" +
            "      \"FollowUsFacebookLink\": \"https://www.facebook.com/DealsDirect.com.au\",\n" +
            "      \"FollowUsTwitterLink\": \"https://twitter.com/dealsdirect\",\n" +
            "      \"FollowUsPinterestLink\": \"https://www.pinterest.com/DealsDirect\",\n" +
            "      \"FollowUsInstagramLink\": \"https://www.instagram.com/deals_direct/\",\n" +
            "      \"FollowUsSnapchatLink\": \"\",\n" +
            "      \"UsePopupFormsForAuthentication\": true,\n" +
            "      \"Payment\": {\n" +
            "        \"UsePayPal\": true,\n" +
            "        \"UseAmExpress\": true,\n" +
            "        \"UseMasterPass\": true,\n" +
            "        \"UseIPay\": false,\n" +
            "        \"UseIPayMobile\": false\n" +
            "      },\n" +
            "      \"PickupPointsServiceID\": \"\",\n" +
            "      \"ShowStartingTimer\": 0,\n" +
            "      \"ShowEndingTimer\": 0,\n" +
            "      \"CommercialAnalyticsAccount\": \"UA-71968602-1\",\n" +
            "      \"CommercialAnalyticsDomainName\": \"dealsdirect.com.au\",\n" +
            "      \"CommercialAnalyticsEnabled\": true,\n" +
            "      \"ShowTopBanner\": false,\n" +
            "      \"ShowCheckoutBanner\": false,\n" +
            "      \"ShowSaleBanners\": true,\n" +
            "      \"TopBannersCount\": 5,\n" +
            "      \"ShowLanguageSwitcher\": false,\n" +
            "      \"ShowLanguageSwitcherMobile\": false,\n" +
            "      \"AndroidAppID\": \"au.com.dealsdirect\",\n" +
            "      \"IosAppID\": \"540284126\",\n" +
            "      \"ShowAuthToLeave\": true,\n" +
            "      \"SignUpUrl\": \"signup.aspx\",\n" +
            "      \"ShowCookieInfoBanner\": false,\n" +
            "      \"ShowFiftySaleBanners\": false,\n" +
            "      \"AlternativeCheckoutWorkflow\": false,\n" +
            "      \"UseCustomNumber\": false,\n" +
            "      \"ApplyCheckoutTaxation\": false,\n" +
            "      \"TrackingPixelCheckoutFacebookID\": \"487371238131300\",\n" +
            "      \"NanigansAnaliticsID\": \"456859\",\n" +
            "      \"MarinAnaliticsID\": \"15670s9b54913\",\n" +
            "      \"ShowTopBannerCohorts\": \"\",\n" +
            "      \"GoogleAdwordsID\": \"933838684\",\n" +
            "      \"GoogleAdwordsLabel\": \"wUFeCI3Lr2MQ3P6kvQM;yVXICK_FqGMQ3P6kvQM;CahcCL25kWQQ3P6kvQM\",\n" +
            "      \"GoogleAdwordsFormat\": \"3\",\n" +
            "      \"DoubleClickEnable\": true,\n" +
            "      \"DoubleClickID\": \"5341105\",\n" +
            "      \"IsSEOEnabled\": true\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetAppSettingsResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {\n" +
            "      \"Country\": {\n" +
            "        \"ID\": \"DA\",\n" +
            "        \"Iso\": \"AU\",\n" +
            "        \"TaxName\": \"GST\",\n" +
            "        \"TaxAmount\": 0.1,\n" +
            "        \"TimeOffset\": 0,\n" +
            "        \"Languages\": [\n" +
            "          {\n" +
            "            \"ID\": \"en\",\n" +
            "            \"Name\": \"English\",\n" +
            "            \"Culture\": \"en-au     \"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"CountryISO3Code\": \"AUS\",\n" +
            "        \"LanguageISO3Codes\": [\n" +
            "          {\n" +
            "            \"ID\": \"en\",\n" +
            "            \"Iso3Code\": \"ENG\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"Checkout\": {\n" +
            "        \"AppView\": 2,\n" +
            "        \"SiteView\": 5,\n" +
            "        \"RoktID\": \"14b56bb5d862489ea8a75edf0d160f43\",\n" +
            "        \"PickupPointsServiceID\": \"\",\n" +
            "        \"PickupPointsEnabled\": false,\n" +
            "        \"PickupPointsMapEnabled\": false,\n" +
            "        \"PickupPointsMapToken\": \"AIzaSyA-sZAR7A7YivjE48OVJZ_BrJ3zxEXz1NM\",\n" +
            "        \"TermsAndConditions\": {\n" +
            "          \"Mode\": 0\n" +
            "        },\n" +
            "        \"ShowAuthToLeave\": true,\n" +
            "        \"AddressSuggestionEnabled\": true,\n" +
            "        \"PcaID\": \"COCOS11111\",\n" +
            "        \"PcaAPIID\": \"GP27-KZ29-FF54-PA71\",\n" +
            "        \"PhoneVerificationEnabled\": false,\n" +
            "        \"DeliveryOptionsEnabled\": true,\n" +
            "        \"BillingAddressEnabled\": false\n" +
            "      },\n" +
            "      \"MyAccount\": {\n" +
            "        \"EnableMyPaymentsSite\": true,\n" +
            "        \"EnableMyPaymentsApp\": true,\n" +
            "        \"ShowOurpaySchedulerInMyAccount\": true\n" +
            "      },\n" +
            "      \"Payments\": {\n" +
            "        \"BrainTree\": {\n" +
            "          \"MerchantID\": \"vqgx4mggg8r562vk\",\n" +
            "          \"Environment\": \"PRODUCTION\",\n" +
            "          \"PayPalEnabled\": true,\n" +
            "          \"PayPalWorkflow\": 0\n" +
            "        },\n" +
            "        \"Kount\": {\n" +
            "          \"MerchantID\": \"601380\",\n" +
            "          \"Enabled\": true\n" +
            "        },\n" +
            "        \"MasterPass\": {\n" +
            "          \"Enabled\": true\n" +
            "        },\n" +
            "        \"IPay\": {\n" +
            "          \"Enabled\": false,\n" +
            "          \"MobileEnabled\": false\n" +
            "        },\n" +
            "        \"PayPal\": {\n" +
            "          \"Enabled\": true\n" +
            "        },\n" +
            "        \"AmExpress\": {\n" +
            "          \"Enabled\": true\n" +
            "        },\n" +
            "        \"MyPay\": {\n" +
            "          \"Enabled\": true,\n" +
            "          \"OurPaySelect\": {\n" +
            "            \"Enabled\": true\n" +
            "          }\n" +
            "        },\n" +
            "        \"MyPayWithPaypal\": {\n" +
            "          \"Enabled\": false\n" +
            "        },\n" +
            "        \"VisaCheckout\": {\n" +
            "          \"Enabled\": false,\n" +
            "          \"APIKey\": \"\",\n" +
            "          \"APIUrl\": \"\"\n" +
            "        },\n" +
            "        \"OurPay\": {\n" +
            "          \"Enabled\": true\n" +
            "        }\n" +
            "      },\n" +
            "      \"Search\": {\n" +
            "        \"Enabled\": true,\n" +
            "        \"MaxPrice\": 200,\n" +
            "        \"Default\": true,\n" +
            "        \"AlgoliaIndexVersion\": \"searchApi\"\n" +
            "      },\n" +
            "      \"Shop\": {\n" +
            "        \"MaxMainSales\": 100,\n" +
            "        \"MainSalesOnly\": true\n" +
            "      },\n" +
            "      \"Access\": {\n" +
            "        \"AnonymousEnabled\": true\n" +
            "      },\n" +
            "      \"ProductPage\": {\n" +
            "        \"ViewMode\": 1,\n" +
            "        \"EnableEnteredSaleItemEvent\": true\n" +
            "      }\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetPaymentTokenResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetPaymentMethodNonce = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {\n" +
            "      \"Nonce\": \"testNonce\"\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockCreatePaymentTxnSuccess = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {},\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private final String mMockCreatePaymentTxnFailure = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {},\n" +
            "    \"Result\": false,\n" +
            "    \"Message\": \"error\"\n" +
            "  }\n" +
            "}";

    private final String mMockCreatePaymentMethodSuccess = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {\n" +
            "      \"PaymentMethods\": [\n" +
            "        {\n" +
            "          \"PaymentType\": \"Visa\",\n" +
            "          \"Description\": \"400000******0002\",\n" +
            "          \"Token\": \"5d7fb8\",\n" +
            "          \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/visa.png?environment=sandbox\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"LastPaidToken\": \"5d7fb8\"\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private final String mMockCreatePaymentMethodFailure = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": false,\n" +
            "    \"ScheduledPlan\": {\n" +
            "      \"PaymentMethods\": [\n" +
            "        {\n" +
            "          \"PaymentType\": \"Visa\",\n" +
            "          \"Description\": \"400000******0002\",\n" +
            "          \"Token\": \"5d7fb8\",\n" +
            "          \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/visa.png?environment=sandbox\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"LastPaidToken\": \"5d7fb8\"\n" +
            "    },\n" +
            "    \"Result\": false,\n" +
            "    \"Message\": \"error\"\n" +
            "  }\n" +
            "}";

    private static final String mMockCallLoginTicketResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {},\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetTemplateTextsResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"ScheduledPlan\": {\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Mock
    MainMvpView mMockMainMvpView;
    @Mock
    DataManager mMockDataManager;
    @Mock
    FetchBraintreeClientTokenHandler mMockFetchTokenHandler;
    @Mock
    Context mMockContext;

    private MainMvpPresenter<MainMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @BeforeClass
    public static void onlyOnce() throws Exception {
    }


    @Before
    public void setUp() throws Exception {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new MainPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockMainMvpView);

        doReturn("").when(mMockDataManager).getCountryId();
        doReturn("").when(mMockDataManager).getLanguageId();
    }

    @Test
    public void testCallGetServerSettings() {
        GetServerSettings.ResponseValue responseValue = gson.fromJson(mMockGetServerSettingsResponse, GetServerSettings.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callGetServerSettings("");

        mPresenter.callGetServerSettings();
        mTestScheduler.triggerActions();

        verify(mMockDataManager).setCountryId(responseValue.getCountryId());
        verify(mMockDataManager).setLanguageId(responseValue.getLanguages().get(0).getID());
        verify(mMockDataManager).setLanguages(gson.toJson(responseValue.getLanguages()));
        verify(mMockDataManager).setSiteName(responseValue.getSiteFullname());
        verify(mMockDataManager).setCurrency(responseValue.getCurrency());
        verify(mMockDataManager).setCurrencySign(responseValue.getCurrencySign());
        verify(mMockDataManager).setFollowUsFbLink(responseValue.getFollowUsFacebookLink());
        verify(mMockDataManager).setFollowUsTwitterLink(responseValue.getFollowUsTwitterLink());
        verify(mMockDataManager).setImageServerUrl(responseValue.getImageServerUrl());

    }

    @Test
    public void testCallGetAppSettings() {
        GetAppSettings.ResponseValue responseValue = gson.fromJson(mMockGetAppSettingsResponse, GetAppSettings.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callGetAppSettings("");

        mPresenter.callGetAppSettings();
        mTestScheduler.triggerActions();

        verify(mMockDataManager).setIsPaypalEnabled(responseValue.d.getValue().getPayments().getPayPal().getEnabled());
        verify(mMockDataManager).setIsMasterpassEnabled(responseValue.d.getValue().getPayments().getMasterPass().getEnabled());
        verify(mMockDataManager).setIsAmexEnabled(responseValue.d.getValue().getPayments().getAmExpress().getEnabled());
        verify(mMockDataManager).setIsKountEnabled(responseValue.d.getValue().getPayments().getKount().getEnabled());
        verify(mMockDataManager).setKountMerchantId(responseValue.d.getValue().getPayments().getKount().getMerchantID());
        verify(mMockDataManager).setSearchMaxPrice(responseValue.d.getValue().getSearch().getMaxPrice());
        verify(mMockDataManager).setAccessAnonymousEnabled(responseValue.d.getValue().getAccess().getAnonymousEnabled());
        verify(mMockDataManager).setIsMyPayEnabled(responseValue.d.getValue().getPayments().getMyPay().getEnabled());
    }

    @Test
    public void testFetchBtAuthTrue() {
        GetPaymentToken.ResponseValue responseValue = gson.fromJson(mMockGetPaymentTokenResponse, GetPaymentToken.ResponseValue.class);

        doReturn(true).when(mMockDataManager).isAuthorized();
        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callGetPaymentToken(any(GetPaymentToken.RequestValue.class));

        mPresenter.fetchBraintreeClientToken();
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).onBraintreeAuthorizationFetchSuccess(responseValue.getPaymentToken(),responseValue.getPaymentType());
        verify(mMockMainMvpView).onBraintreeAuthorizationFetchFail();

    }

    @Test
    public void testFetchBtAuthExceptionOccured(){

        doReturn(true).when(mMockDataManager).isAuthorized();
        doReturn(Observable.error(new Exception("error")))
                .when(mMockDataManager).callGetPaymentToken(any(GetPaymentToken.RequestValue.class));

        mPresenter.fetchBraintreeClientToken();
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).onError("error");
        verify(mMockMainMvpView).onBraintreeAuthorizationFetchFail();

    }

    @Test
    public void testCallGetPaymentMethodNonce(){

        JsonObject origJo = new JsonParser().parse(mMockGetPaymentMethodNonce).getAsJsonObject();
        JSONObject jo = null;

        try {
            jo = new JSONObject(origJo.toString());
        } catch (Exception e){

        }

        doReturn(Observable.just(jo))
                .when(mMockDataManager).callGetPaymentMethodNonce(any(GetPaymentMethodNonceRequest.class));

        mPresenter.callGetPaymentMethodNonce("");
        mTestScheduler.triggerActions();

        String nonceString = "";
        try {
            nonceString = jo.getJSONObject("d").getJSONObject("Value").getString("Nonce");
        }catch (Exception e){

        }

        verify(mMockMainMvpView).showGetPaymentMethodNonceSuccess(nonceString);

    }

    @Test
    public void testCreatePaymentTransactionSuccess(){

        CreatePaymentTransaction.ResponseValue responseValue = gson.fromJson(mMockCreatePaymentTxnSuccess, CreatePaymentTransaction.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callCreatePaymentTransaction(any(CreatePaymentTransaction.RequestValue.class));

        mPresenter.createPaymentTransaction("","","","", "");
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).hideLoading();
        verify(mMockMainMvpView).performResetWithAuthFetch();
        verify(mMockMainMvpView).showCreatePaymentTransactionSuccess("",responseValue);

    }

    @Test
    public void testCreatePaymentTransactionFailure(){

        CreatePaymentTransaction.ResponseValue responseValue = gson.fromJson(mMockCreatePaymentTxnFailure, CreatePaymentTransaction.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callCreatePaymentTransaction(any(CreatePaymentTransaction.RequestValue.class));

        mPresenter.createPaymentTransaction("","","","", "");
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).hideLoading();
        verify(mMockMainMvpView).performResetWithAuthFetch();
        verify(mMockMainMvpView).showCreatePaymentTransactionFailure(responseValue.getD().getMessage());

    }

    @Test
    public void testCreatePaymentMethodSuccess(){

        CreatePaymentMethod.ResponseValue responseValue = gson.fromJson(mMockCreatePaymentMethodSuccess,CreatePaymentMethod.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callCreatePaymentMethod(any(CreatePaymentMethod.RequestValue.class));

        mPresenter.createPaymentMethod("","","");
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).hideLoading();
        verify(mMockMainMvpView).performResetWithAuthFetch();
        verify(mMockMainMvpView).showCreatePaymentMethodSuccess(responseValue.getD().getValue().getLastPaymentMethod());
    }

    @Test
    public void testCreatePaymentMethodFailure(){

        CreatePaymentMethod.ResponseValue responseValue = gson.fromJson(mMockCreatePaymentMethodFailure,CreatePaymentMethod.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callCreatePaymentMethod(any(CreatePaymentMethod.RequestValue.class));

        mPresenter.createPaymentMethod("","","");
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).hideLoading();
        verify(mMockMainMvpView).performResetWithAuthFetch();
        verify(mMockMainMvpView).onError(responseValue.getMessage());
    }

    @Test
    public void testCallLoginTicket(){
        doReturn("testLoginTicket")
                .when(mMockDataManager).getLoginTicket();

        LoginEmail.ResponseValue responseValue = gson.fromJson(mMockCallLoginTicketResponse,LoginEmail.ResponseValue.class);

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callLoginTicket(any(LoginTicket.RequestValue.class));

        mPresenter.callLoginTicket(mMockContext, true);
        mTestScheduler.triggerActions();

        verify(mMockDataManager).acknowledgeAuth(responseValue.getTicket());
//        verify(mMockMainMvpView).loginSuccessMethods();

    }

    @Test
    public void testCallLogout(){

        Logout.ResponseValue responseValue = new Logout.ResponseValue();

        doReturn(Observable.just(responseValue))
                .when(mMockDataManager).callLogout(any(Logout.RequestValue.class));

        mPresenter.callLogout(null);
        mTestScheduler.triggerActions();

        verify(mMockMainMvpView).showLoading();
        verify(mMockMainMvpView).hideLoading();
        verify(mMockDataManager).revokeAuth();
        verify(mMockMainMvpView).performBraintreeReset();

    }

    @Test
    public void testCallGetTemplateTexts(){

        GetTemplateTextsResponse response = gson.fromJson(mMockGetTemplateTextsResponse, GetTemplateTextsResponse.class);
        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetTemplateTexts(any(GetTemplateTextsRequest.class));

        mPresenter.callGetTemplateTexts();
        mTestScheduler.triggerActions();

        verify(mMockDataManager).setMyPayTemplateTexts(response.getResponse().getValue());
        verify(mMockMainMvpView).storeTemplateTexts(response.getResponse().getValue());
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
