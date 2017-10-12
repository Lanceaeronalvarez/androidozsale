package au.com.dealsdirect.contact;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsPresenter;
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
public class ViewContactPresenterTest {
    @Mock
    DataManager mockDataManager;

    @Mock
    ViewContactsMvpView viewContactsMvpView;

    ViewContactsPresenter<ViewContactsMvpView> viewContactsPresenter;
    TestScheduler testScheduler;

    String mockContactsResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"ContactNo\": 3953052,\n" +
            "\t\t\t\"Subject\": \"Technical Issues\",\n" +
            "\t\t\t\"Comments\": 1,\n" +
            "\t\t\t\"InvoiceNo\": 0,\n" +
            "\t\t\t\"SaleName\": null,\n" +
            "\t\t\t\"LastComment\": \"heyy\",\n" +
            "\t\t\t\"LastAnswer\": \"\\/Date(1507533963210)\\/\"\n" +
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
        viewContactsPresenter = new ViewContactsPresenter<>(mockDataManager, testSchedulerProvider, compositeDisposable);
        viewContactsPresenter.onAttach(viewContactsMvpView);
    }

    @Test
    public void loadContactsTest() {
        GetContactsResponse getContactsResponse = new Gson().fromJson(mockContactsResponse, GetContactsResponse.class);

        doReturn("EN").when(mockDataManager).getLanguageId();
        doReturn(Observable.just(getContactsResponse)).when(mockDataManager).callGetContacts(any(String.class));

        viewContactsPresenter.loadContacts();
        testScheduler.triggerActions();

        verify(viewContactsMvpView).showContactItems(getContactsResponse.getD());
    }
}
