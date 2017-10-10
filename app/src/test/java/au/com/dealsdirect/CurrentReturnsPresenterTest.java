package au.com.dealsdirect;

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
            "\t\t\"Value\": {\n" +
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

        currentReturns = new LinkedList<>();
        CurrentReturns currentReturns1 = new CurrentReturns();

        currentReturns1.setID("0");
        currentReturns1.setDescription("Current Return 1");
        currentReturns1.setApprovedDate("1/11/11");
        currentReturns1.setInvoiceNo(1);
        currentReturns1.setInvoiceNoRef(010110);
        currentReturns1.setOrderNumber(1);
        currentReturns1.setLastSavedDate("1/11/11");
        currentReturns1.setRan("102");
        currentReturns1.setReturnStatus("progress");

        CurrentReturns currentReturns2 = new CurrentReturns();

        currentReturns2.setID("1");
        currentReturns2.setDescription("Current Return 2");
        currentReturns2.setApprovedDate("1/11/11");
        currentReturns2.setInvoiceNo(2);
        currentReturns2.setInvoiceNoRef(010112);
        currentReturns2.setOrderNumber(2);
        currentReturns2.setLastSavedDate("1/11/11");
        currentReturns2.setRan("102");
        currentReturns2.setReturnStatus("progress");

        CurrentReturns currentReturns3 = new CurrentReturns();

        currentReturns3.setID("2");
        currentReturns3.setDescription("Current Return 3");
        currentReturns3.setApprovedDate("1/11/11");
        currentReturns3.setInvoiceNo(3);
        currentReturns3.setInvoiceNoRef(010113);
        currentReturns3.setOrderNumber(03);
        currentReturns3.setLastSavedDate("1/11/11");
        currentReturns3.setRan("103");
        currentReturns3.setReturnStatus("progress");

        CurrentReturns currentReturns4 = new CurrentReturns();

        currentReturns4.setID("3");
        currentReturns4.setDescription("Current Return 1");
        currentReturns4.setApprovedDate("1/11/11");
        currentReturns4.setInvoiceNo(0104);
        currentReturns4.setInvoiceNoRef(010114);
        currentReturns4.setOrderNumber(4);
        currentReturns4.setLastSavedDate("1/11/11");
        currentReturns4.setRan("104");
        currentReturns4.setReturnStatus("progress");

        currentReturns.add(currentReturns1);
        currentReturns.add(currentReturns2);
        currentReturns.add(currentReturns3);
        currentReturns.add(currentReturns4);

        CurrentReturnResponse currentReturnResponse = new CurrentReturnResponse();
        currentReturnResponse.setMessage("ok");
        currentReturnResponse.setIsAuthenticated(true);
        currentReturnResponse.setResult(true);
        currentReturnResponse.setCurrentReturns(currentReturns);

        currentReturnResponseBody  = new CurrentReturnResponseBody();
        currentReturnResponseBody.setCurrentReturnResponse(currentReturnResponse);
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
