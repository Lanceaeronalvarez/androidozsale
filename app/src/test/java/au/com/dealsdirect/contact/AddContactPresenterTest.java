package au.com.dealsdirect.contact;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrders;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjects;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactMvpView;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactPresenter;
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
public class AddContactPresenterTest {
    @Mock
    DataManager addContactDataManager;

    @Mock
    AddContactMvpView addContactView;

    String subjectResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"List\": [\"Where is My Order?\", \"Amending my Order\", \"Received Order is Incorrect\", \"Returns Enquiry\", \"Invite a Friend\", \"Technical Issues\", \"Comments and Suggestions\", \"Search\", \"Other\"],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    String orderResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    String replyResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"__type\": \"OzSale.PublicAPI.ApiResult\",\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    AddContactPresenter<AddContactMvpView> addContactPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        addContactPresenter = new AddContactPresenter<>(addContactDataManager, testSchedulerProvider, compositeDisposable);
        addContactPresenter.onAttach(addContactView);
    }

    @Test
    public void loadContactUsSubjectsTest() {
        ContactSubjects contactSubjectResponse = new Gson().fromJson(subjectResponse, ContactSubjects.class);
        doReturn(Observable.just(contactSubjectResponse)).when(addContactDataManager).callGetContactSubjects(any(ContactSubjectsRequest.class));

        addContactPresenter.loadContactUsSubjects();
        testScheduler.triggerActions();

        verify(addContactView).showLoading();
        verify(addContactView).hideLoading();
        verify(addContactView).showContactFirstSubject(contactSubjectResponse.getContactSubjectResponse().getList());
    }

    @Test
    public void loadContactUsOrdersTest() {
        //callGetContactOrders
        ContactOrders contactOrders = new Gson().fromJson(orderResponse, ContactOrders.class);

        doReturn(Observable.just(contactOrders)).when(addContactDataManager).callGetContactOrders();

        addContactPresenter.loadContactUsOrders();
        testScheduler.triggerActions();

        verify(addContactView).hideLoading();
        verify(addContactView).showContactFirstOrder(contactOrders.getContactOrderResponse().getList());
    }

    @Test
    public void replyContactTest() {
        //callReplyContact
        ReplyContactRequest replyContactRequest = new ReplyContactRequest();
        replyContactRequest.comments = "hey";
        replyContactRequest.contactNo = 1232133;

        ReplyContactResponse replyContactResponse = new Gson().fromJson(replyResponse, ReplyContactResponse.class);
        doReturn(Observable.just(replyContactResponse)).when(addContactDataManager).callReplyContact(replyContactRequest);

        addContactPresenter.replyContact(replyContactRequest);
        testScheduler.triggerActions();

        verify(addContactView).hideLoading();
        verify(addContactView).repliedContactSwitchView(replyContactResponse.getReplyContact());
    }
}
