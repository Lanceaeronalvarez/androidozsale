package au.com.dealsdirect.returns;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponse;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturns;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsMvpView;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 06/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CurrentReturnsPresenterTest {
    @Mock
    DataManager dataManager;

    @Mock
    CurrentReturnsMvpView mvpView;

    String mockResponseReturns = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"ID\": \"c56c6c71-7648-4096-9c3a-cc1d454b5bf4\",\n" +
            "\t\t\t\"InvoiceNo\": 27341735,\n" +
            "\t\t\t\"OrderNumber\": 24051906,\n" +
            "\t\t\t\"InvoiceNoRef\": 27341735,\n" +
            "\t\t\t\"LastSavedDate\": \"\\/Date(1507528837060)\\/\",\n" +
            "\t\t\t\"ApprovedDate\": null,\n" +
            "\t\t\t\"Ran\": \"\",\n" +
            "\t\t\t\"ReturnStatus\": \"Pending\",\n" +
            "\t\t\t\"Description\": \"Bottoms Bonanza\"\n" +
            "\t\t}],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    String mockResponseReturnDetails = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"ScheduledPayment\": {\n" +
            "\t\t\t\"Items\": [{\n" +
            "\t\t\t\t\"BrandID\": \"a9767a03-c3e9-492c-a995-c57bb3246b79\",\n" +
            "\t\t\t\t\"ItemID\": \"4d2034d5-313b-4892-890b-40f6d1a9e3d6\",\n" +
            "\t\t\t\t\"ImageID\": \"80b3e1cb-61d7-4ca0-8d52-7662fa7ef8a0\",\n" +
            "\t\t\t\t\"File\": \"JP101-Khakigrey-b.JPG\",\n" +
            "\t\t\t\t\"Item\": \"100% Cotton Casual Men\\u0027s Fashion Sweatpants Khaki Grey\",\n" +
            "\t\t\t\t\"Size\": \"Large\",\n" +
            "\t\t\t\t\"Count\": 1,\n" +
            "\t\t\t\t\"Price\": 19.9900,\n" +
            "\t\t\t\t\"SubTotal\": 19.9900\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": 19.9900\n" +
            "\t\t},\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    CurrentReturnsPresenter<CurrentReturnsMvpView> mPresenter;
    TestScheduler testScheduler;
    List<CurrentReturns> currentReturns;
    CurrentReturnResponse currentReturnResponse;
    CurrentReturnResponseBody currentReturnResponseBody;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);

        mPresenter = new CurrentReturnsPresenter<>(dataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mvpView);
    }

    @Test
    public void loadCurrentReturnsTest() {
        //callGetCurrentReturns
        CurrentReturnResponseBody responseBody = new Gson().fromJson(mockResponseReturns, CurrentReturnResponseBody.class);

        doReturn(Observable.just(responseBody)).when(dataManager).callGetCurrentReturns();

        mPresenter.loadCurrentReturns();
        testScheduler.triggerActions();

        verify(mvpView).showCurrentReturns(responseBody);
    }

    @Test
    public void loadReturnDetailsTest() {
        //callGetReturnDetails
        GetReturnDetailRequest request = new GetReturnDetailRequest("c56c6c71-7648-4096-9c3a-cc1d454b5bf4");
        GetReturnDetailsResponse response = new Gson().fromJson(mockResponseReturnDetails, GetReturnDetailsResponse.class);

        doReturn(Observable.just(response)).when(dataManager).callGetReturnDetails(request);

        mPresenter.loadReturnDetails(request);
        testScheduler.triggerActions();

        verify(mvpView).showCurrentReturnDetails(response.getGetReturnDetailsResponseBody());
    }

    @Test
    public void PresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }


}
