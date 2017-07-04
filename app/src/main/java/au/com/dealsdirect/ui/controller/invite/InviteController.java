package au.com.dealsdirect.ui.controller.invite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class InviteController extends BaseController {

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleText;

    @BindView(R.id.controller_invite_friend_button_send_invite)
    Button mSendInviteButton;

    public InviteController (Bundle args) {
        super(args);
    }

    public static InviteController newInstance() {
        return new InviteController(
                new BundleBuilder(new Bundle())
                .build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_invite_friend, container, false);
        getControllerComponent().inject(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Invite Friends");
        mSendInviteButton.setOnClickListener(action -> {
            getRouter().pushController(RouterTransaction.with(InviteSendController.newInstance())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
        });
    }
}
