package au.com.dealsdirect.ui.controller.notification;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 21/06/2018.
 */

public class NotificationController extends BaseController implements NotificationMvpView {

    @Inject
    NotificationMvpPresenter<NotificationMvpView> mPresenter;

    @BindView(R.id.notification_switch)
    Switch mNotificationSwitch;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    public static NotificationController newInstance() {
        return new NotificationController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public NotificationController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_notifications, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleTextView.setText(getString(R.string.account_notification));

        mNotificationSwitch.setChecked(mPresenter.getIsNotificationsEnabled());
        mNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.setIsNotificationsEnabled(isChecked);
                mActivity.callGCMRegisterSubscriber();
            }
        });
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackPressed(){
        mActivity.onBackPressed();
    }
}
