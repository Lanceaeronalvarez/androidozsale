package au.com.dealsdirect.contact;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryMvpView;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ViewContactHistoryPresenterTest {
    @Mock
    DataManager mockDataManager;

    @Mock
    ViewContactHistoryMvpView viewContactsMvpView;

    ViewContactHistoryPresenter<ViewContactHistoryMvpView> viewContactHistoryPresenter;
    TestScheduler testScheduler;

    String mockContactsResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"IsStaff\": false,\n" +
            "\t\t\t\"Subject\": \"Technical Issues\",\n" +
            "\t\t\t\"InvoiceNo\": 0,\n" +
            "\t\t\t\"Text\": \"heyy\",\n" +
            "\t\t\t\"UserName\": null,\n" +
            "\t\t\t\"Date\": \"\\/Date(1507533963210)\\/\"\n" +
            "\t\t}],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";
    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        viewContactHistoryPresenter = new ViewContactHistoryPresenter<>(mockDataManager, testSchedulerProvider, compositeDisposable);
        viewContactHistoryPresenter.onAttach(viewContactsMvpView);
    }

    @Test
    public void loadContactHistoryTest() {
        GetContactHistoryResponse getContactHistoryResponse = new Gson().fromJson(mockContactsResponse, GetContactHistoryResponse.class);

        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.contactNo = 299299;

        doReturn(Observable.just(getContactHistoryResponse)).when(mockDataManager).callGetContactHistory(any(GetContactHistoryRequest.class));

        viewContactHistoryPresenter.loadContactHistory(getContactHistoryRequest.contactNo);
        testScheduler.triggerActions();

        verify(viewContactsMvpView).showContactHistory(getContactHistoryResponse.getGetContactHistoryResponseBody().getList());

    }

    @Test
    public void callReplyContactTest() {
        //callReplyContact
        ReplyContactRequest request = new ReplyContactRequest();
        request.comments = "yow";
        request.contactNo = 122223;

        ReplyContactResponse replyContactResponse = new ReplyContactResponse();
        doReturn(Observable.just(replyContactResponse)).when(mockDataManager).callReplyContact(request);

        viewContactHistoryPresenter.replyContact(request);
        testScheduler.triggerActions();

        verify(viewContactsMvpView).hideLoading();
        verify(viewContactsMvpView).repliedContactSwitchView(replyContactResponse.getReplyContact());
    }
}
