package au.com.dealsdirect.checkout;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentMvpView;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

/**
 * Created by smartwave on 10/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class AddPaymentPresenterTest {

    @Mock
    AddPaymentMvpView mMockAddPaymentView;
    @Mock
    DataManager mMockDataManager;

    private AddPaymentMvpPresenter<AddPaymentMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new AddPaymentPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockAddPaymentView);
    }



    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }

}
