package au.com.dealsdirect.ui.controller.invite;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by Paul on 7/3/17.
 */

public class InviteSendController extends BaseController implements InviteMvpView {

    @Inject
    InviteMvpPresenter<InviteMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageView mArrowImage;

    @BindView(R.id.controller_send_invite_container)
    LinearLayout mSendInvitationContainer;

    @BindView(R.id.invite_friends_sms_button)
    RelativeLayout mMessageSendInvitationLayout;

    @BindView(R.id.invite_friends_email_button)
    RelativeLayout mMailSendInvitationLayout;

    @BindView(R.id.invite_friend_twitter_button)
    RelativeLayout mTwitterSendInvitationLayout;

    @BindView(R.id.invite_friend_facebook_button)
    RelativeLayout mFacebookSendInvitationLayout;

    @BindView(R.id.invite_friend_twitter_follow_us_button_layout)
    RelativeLayout mTwitterFollowUsContainer;

    @BindView(R.id.invite_friend_facebook_like_us_on_facebook_button_layout)
    RelativeLayout mFacebookLikeUsContainer;

    @BindView(R.id.controller_send_invite_edit_text_personal_invitation)
    EditText mPersonalInvitationMessageEditText;

    @BindView(R.id.controller_send_invite_edit_text_deals_direct_link)
    EditText mPersonalInvitationLinkEditText;

    @BindView(R.id.invite_friend_clipboard_button)
    RelativeLayout mClipboardButton;

    @BindView(R.id.invite_friend_clipboard_text)
    TextView mClipboardText;

    @BindView(R.id.invite_friend_clipboard_image)
    ImageView mClipboardImage;

    @BindView(R.id.controller_invite_image_vouchers)
    ImageView mImageView;

    @BindView(R.id.controller_send_invite_layout_select_order_option)
    RelativeLayout mSendInviteLinkLayout;

    GetInviteResponse.Value inviteBody;
    String inviteSubject;
    String inviteLink;
    String inviteMessage;
    String bannerImageUrl;

    ProgressDialog progress;

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

        }};

    public InviteSendController(Bundle args) {
        super(args);
    }

    public static InviteSendController newInstance() {
        return new InviteSendController(new BundleBuilder(
                new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_invite_send, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Invite Friends");
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setVisibility(View.INVISIBLE);


        Glide.with(getActivity())
                .load(R.drawable.invite_friend_vouchers_medium)
                .into(mImageView);

        String twitterLink = mPresenter.getFollowUsTwitterLink();
        String facebookLink = mPresenter.getFollowUsFbLink();

        progress = new ProgressDialog(getActivity());

        if (twitterLink.isEmpty()){
            mTwitterFollowUsContainer.setVisibility(View.GONE);
        }else{
            mTwitterFollowUsContainer.setVisibility(View.VISIBLE);
        }

        if (facebookLink.isEmpty()){
            mFacebookLikeUsContainer.setVisibility(View.GONE);
        }else{
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

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString() + " , ";
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = personalInvitation + invitationLink;


                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bannerImageUrl));
                tweetIntent.setType("text/plain");

                PackageManager packManager = getActivity().getPackageManager();
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
                    startActivity(tweetIntent);
                } else {
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(messageWithInvite)));
                    startActivity(i);
                    CustomAlertDialog.showCustomAlertDialog(
                            getActivity(),
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "Twitter is not installed on this device");
                }
            }
        });

        mFacebookSendInvitationLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();

                try {

                    ShareLinkContent content =
                            new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(invitationLink))
                                    .setContentTitle(inviteSubject)
                                    .setImageUrl(Uri.parse(bannerImageUrl))
                                    .setContentDescription(inviteMessage)
                                    .setQuote(personalInvitation)
                                    .build();
                    ShareDialog shareDialog = new ShareDialog(getActivity());
                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

                } catch (Exception e) {
//                    GDebug.log("facebookSendInvite",e.getMessage());
                }

            }
        });

        mMessageSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String message = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = message +", "+invitationLink;

                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("sms_body",messageWithInvite);
                startActivity(smsIntent);
            }
        });


        mMailSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {


                String message = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = message +", "+invitationLink;

                if(invitationLink.isEmpty() || invitationLink.equals("")){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String message = mPersonalInvitationMessageEditText.getText().toString();
                            String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                            String messageWithInvite = message +", "+invitationLink;

                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", " ", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, inviteSubject);
                            emailIntent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
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

                String link = mPresenter.getFollowUsFbLink();
                if (!link.isEmpty()) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(intent);
                } else {
                    CustomAlertDialog.showCustomAlertDialog(
                            getActivity(),
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "facebook link missing");
                }
            }
        });

        mTwitterFollowUsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = mPresenter.getFollowUsTwitterLink();

                Intent intent;
                if (!link.isEmpty()) {
                    try {
                        getActivity().getPackageManager().getPackageInfo("com.twitter.android",0);
                        Uri uri = Uri.parse("twitter://user?user_id=37405859");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        Uri uri = Uri.parse(link);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                    getActivity().startActivity(intent);

                } else {

                    CustomAlertDialog.showCustomAlertDialog(
                            getActivity(),
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

                if(mClipboardText.equals("Save Changes") || mClipboardText.getText().toString() == "Save Changes") {
                    mPersonalInvitationMessageEditText.setEnabled(true);

                    String editedLink = mPersonalInvitationLinkEditText.getText().toString();
                    SetInviteRequest setInviteLinkRequest = new SetInviteRequest();
                    setInviteLinkRequest.inviteLink = editedLink;


                    showLoading();

                    mPresenter.setInviteLink(setInviteLinkRequest);
                } else {

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getApplicationContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Text", mPersonalInvitationLinkEditText.getText().toString());
                    clipboard.setPrimaryClip(clipData);

                    CustomAlertDialog.showCustomAlertDialog(getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                            "Link Copied to Clipboard");
                }
            }
        });


        mPresenter.start();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mPresenter.onDetach();
    }

    @Override
    public void showInviteLink(GetInviteResponse getInviteLinkBody) {
        if (getInviteLinkBody.getResponse().getValue() != null) {
            inviteBody = getInviteLinkBody.getResponse().getValue();
        }

        inviteLink = inviteBody.getLink();
        inviteMessage = inviteBody.getInviteMessage();
        inviteSubject = inviteBody.getInviteSubject();
        bannerImageUrl = "https://ibb.co/mipKAQ";

        mPersonalInvitationLinkEditText.setText(inviteLink);
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
                    getActivity(),
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Edit Successful");

        } else {
            mPersonalInvitationLinkEditText.setFocusable(false);
            mPersonalInvitationLinkEditText.setFocusableInTouchMode(false);

            mPersonalInvitationLinkEditText.setText(inviteLink);

            mClipboardText.setText("Copy link to clipboard");
            mClipboardImage.setVisibility(View.VISIBLE);


            progress.dismiss();

            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(),
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    editLinkResponse
            );
        }
    }
}
