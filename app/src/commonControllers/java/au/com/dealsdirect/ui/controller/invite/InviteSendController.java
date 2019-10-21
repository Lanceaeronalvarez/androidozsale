package au.com.dealsdirect.ui.controller.invite;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.InviteType.CANCEL;

/**
 * Created by Paul on 7/3/17.
 */

public class InviteSendController extends BasePullToRefreshController implements InviteMvpView {

    @Inject
    InviteMvpPresenter<InviteMvpView> mPresenter;

    @BindView(R.id.controller_send_invite_root)
    View mRoot;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_left_view)
    View mLeftView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mRightView;

    @BindView(R.id.controller_invite_buttons_layout)
    LinearLayout mSendInvitationContainer;

    @BindView(R.id.controller_invite_sms_button)
    RelativeLayout mMessageSendInvitationLayout;

    @BindView(R.id.controller_invite_email_button)
    RelativeLayout mMailSendInvitationLayout;

    @BindView(R.id.controller_invite_twitter_button)
    RelativeLayout mTwitterSendInvitationLayout;

    @BindView(R.id.controller_invite_facebook_button)
    RelativeLayout mFacebookSendInvitationLayout;

    @BindView(R.id.controller_invite_follow_us_layout)
    RelativeLayout mTwitterFollowUsContainer;

    @BindView(R.id.controller_invite_like_us_layout)
    RelativeLayout mFacebookLikeUsContainer;

    @BindView(R.id.controller_invite_personal_invitation_link_edittext)
    EditText mPersonalInvitationMessageEditText;

    @BindView(R.id.controller_invite_invitation_link_edittext)
    EditText mPersonalInvitationLinkEditText;

    @BindView(R.id.controller_invite_clipboard_button)
    RelativeLayout mClipboardButton;

    @BindView(R.id.controller_invite_clipboard_text)
    TextView mClipboardText;

    @BindView(R.id.invite_friend_clipboard_image)
    ImageView mClipboardImage;

    @BindView(R.id.controller_invite_vouchers_imageview)
    ImageView mImageView;

    @BindView(R.id.controller_invite_invitation_link_layout)
    RelativeLayout mSendInviteLinkLayout;

    GetInviteResponse.Value inviteBody;
    String inviteSubject;
    String inviteLink;
    String inviteMessage;
    String bannerImageUrl;

    ProgressDialog progress;

    private String mInviteMethod = CANCEL;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPersonalInvitationMessageEditText.setEnabled(false);

            mPersonalInvitationLinkEditText.setActivated(true);
            mPersonalInvitationLinkEditText.setPressed(true);
            mPersonalInvitationLinkEditText.setClickable(true);
            mPersonalInvitationLinkEditText.setEnabled(true);

            mClipboardImage.setVisibility(View.GONE);
            mClipboardText.setText("Save Changes");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public InviteSendController(Bundle args) {
        super(args);
    }

    public static InviteSendController newInstance() {
        return new InviteSendController(new BundleBuilder(
                new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container, ToolBarType.ARROW);

        setToolBarVisible(getResource().getBoolean(R.bool.invite_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_invite_send, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.start();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    protected void setUp(View view) {

        assert mActivity != null;
        mActivity.getMainController().showBottomNav();

        mActivity.getMainController().setViewpagerDraggable(false);

        if(mPresenter.isTablet()){
            mLeftView.setVisibility(View.INVISIBLE);
        }

        mTitleText.setText(getString(R.string.account_invite_friend));
        mRightView.setVisibility(View.INVISIBLE);

        mImageView.setImageDrawable(mActivity.getDrawable(R.drawable.invite_friend_vouchers_image));

        String twitterLink = mPresenter.getFollowUsTwitterLink();
        String facebookLink = mPresenter.getFollowUsFbLink();

        progress = new ProgressDialog(mActivity);

        if (twitterLink.isEmpty()) {
            mTwitterFollowUsContainer.setVisibility(View.GONE);
        } else {
            mTwitterFollowUsContainer.setVisibility(View.VISIBLE);
        }

        if (facebookLink.isEmpty()) {
            mFacebookLikeUsContainer.setVisibility(View.GONE);
        } else {
            mFacebookLikeUsContainer.setVisibility(View.VISIBLE);
        }

        mSendInviteLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClipboardImage.setVisibility(View.GONE);
                mClipboardText.setText("Save Changes");
            }
        });

        mTwitterSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = personalInvitation + " " + invitationLink;

                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
//                tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bannerImageUrl != null ? bannerImageUrl : ""));
                tweetIntent.setType("text/plain");

                PackageManager packManager = mActivity.getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for (ResolveInfo resolveInfo : resolvedInfoList) {
                    if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }
                if (resolved) {
                    mInviteMethod = DataCollector.EventParameters.InviteType.TWITTER;
                    startActivity(tweetIntent);
                } else {
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(messageWithInvite)));
                    startActivity(i);
                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "Twitter is not installed on this device");
                }
            }
        });

        mFacebookSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();

                try {

                    ShareLinkContent content =
                            new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(invitationLink))
                                    .setImageUrl(Uri.parse(bannerImageUrl))
                                    .build();
                    ShareDialog shareDialog = new ShareDialog(mActivity);
                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

                    mInviteMethod = DataCollector.EventParameters.InviteType.FACEBOOK;

                } catch (Exception e) {
//                    GDebug.log("facebookSendInvite",e.getMessage());
                }

            }
        });

        mMessageSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());

                String message = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = message + " " + invitationLink;

                mInviteMethod = DataCollector.EventParameters.InviteType.SMS;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mActivity);

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                    sendIntent.putExtra("sms_body", messageWithInvite);

                    if (defaultSmsPackageName != null) {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    startActivity(sendIntent);
                } else {
                    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("sms_body", messageWithInvite);
                    startActivity(smsIntent);
                }
            }
        });


        mMailSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());


                String message = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = message + " " + invitationLink;

                if (invitationLink.isEmpty() || invitationLink.equals("")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String message = mPersonalInvitationMessageEditText.getText().toString();
                            String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                            String messageWithInvite = message + " " + invitationLink;

                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", " ", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, inviteSubject);
                            emailIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                            mInviteMethod = DataCollector.EventParameters.InviteType.EMAIL;
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    }, 2000);

                } else {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", " ", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, inviteSubject);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                }
            }
        });

        mFacebookLikeUsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());

                String link = mPresenter.getFollowUsFbLink();
                if (!link.isEmpty()) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    assert mActivity != null;
                    mActivity.startActivity(intent);
                } else {
                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "facebook link missing");
                }
            }
        });

        mTwitterFollowUsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveInstanceState(getArgs());
                String link = mPresenter.getFollowUsTwitterLink();

                Intent intent;
                if (!link.isEmpty()) {
                    try {
                        mActivity.getPackageManager().getPackageInfo("com.twitter.android", 0);
                        Uri uri = Uri.parse(mActivity.getString(R.string.twitter_user_id));
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        Uri uri = Uri.parse(link);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                    mActivity.startActivity(intent);

                } else {

                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "twitter link missing");
                }


            }
        });

        mClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                mSendInviteLinkLayout.requestFocus();

                if (mClipboardText.equals("Save Changes") || mClipboardText.getText().toString() == "Save Changes") {
                    mPersonalInvitationMessageEditText.setEnabled(true);

                    String editedLink = mPersonalInvitationLinkEditText.getText().toString();
                    SetInviteRequest setInviteLinkRequest = new SetInviteRequest();
                    setInviteLinkRequest.inviteLink = editedLink;


                    showLoading();

                    mPresenter.setInviteLink(setInviteLinkRequest);
                } else {

                    ClipboardManager clipboard = (ClipboardManager) mActivity.getApplicationContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Text", mPersonalInvitationLinkEditText.getText().toString());
                    clipboard.setPrimaryClip(clipData);

                    CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                            "Link Copied to Clipboard");
                }
            }
        });

        mPersonalInvitationLinkEditText.setOnFocusChangeListener((view1, b) -> {
            mPersonalInvitationLinkEditText.setTextColor(getResources().getColor(R.color.text_extra_dark));
        });

        mPersonalInvitationMessageEditText.setOnFocusChangeListener((view1, b) -> {
            mPersonalInvitationLinkEditText.setTextColor(getResources().getColor(R.color.text_extra_light));
        });

        mPresenter.start();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.METHOD, mInviteMethod);
        parameters.put(DataCollector.EventParameters.SOURCE, DataCollector.EventParameters.ViewSource.INVITE);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, InviteSendController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.SHARE_SUCCESS, 1);
        DataCollector.logEvent(Events.Share, parameters);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showInviteLink(GetInviteResponse getInviteLinkBody) {
        mRoot.setVisibility(View.VISIBLE);

        if (getInviteLinkBody.getResponse().getValue() != null) {
            inviteBody = getInviteLinkBody.getResponse().getValue();
        }

        inviteLink = inviteBody.getLink();
        inviteMessage = inviteBody.getInviteMessage();
        inviteSubject = inviteBody.getInviteSubject();
        bannerImageUrl = inviteBody.getBannerUrl();

        mPersonalInvitationLinkEditText.setText(inviteLink);
        mPersonalInvitationMessageEditText.setText(inviteMessage);
        mSendInvitationContainer.setVisibility(View.VISIBLE);
        mPersonalInvitationLinkEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void onInviteLinkSet(SetInviteResponse setInviteLinkResponseBody) {
        Boolean isEditLinkSuccessful = setInviteLinkResponseBody.getValue().getResult();
        String editLinkResponse = setInviteLinkResponseBody.getValue().getMessage();

        if (isEditLinkSuccessful) {
            mPersonalInvitationLinkEditText.setPressed(false);

            mClipboardText.setText("Copy link to clipboard");
            mClipboardImage.setVisibility(View.VISIBLE);

            mSendInviteLinkLayout.requestFocus();

            progress.dismiss();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Edit Successful");

        } else {
            mPersonalInvitationLinkEditText.setText(inviteLink);

            mPersonalInvitationMessageEditText.setEnabled(true);

            mClipboardText.setText("Copy link to clipboard");
            mClipboardImage.setVisibility(View.VISIBLE);

            progress.dismiss();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    editLinkResponse);
        }
    }
}
