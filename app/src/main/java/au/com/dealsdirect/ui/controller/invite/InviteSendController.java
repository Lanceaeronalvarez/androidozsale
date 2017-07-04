package au.com.dealsdirect.ui.controller.invite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.base.BaseController;
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

    @BindView(R.id.controller_send_invite_container_messages)
    RelativeLayout mMessageSendInvitationLayout;

    @BindView(R.id.controller_send_invite_container_mail)
    RelativeLayout mMailSendInvitationLayout;

    @BindView(R.id.controller_send_invite_container_twitter)
    RelativeLayout mTwitterSendInvitationLayout;

    @BindView(R.id.controller_send_invite_container_facebook)
    RelativeLayout mFacebookSendInvitationLayout;

    @BindView(R.id.invite_friend_twitter_follow_us_button_layout)
    RelativeLayout mTwitterFollowUsContainer;

    @BindView(R.id.invite_friend_facebook_like_us_on_facebook_button_layout)
    RelativeLayout mFacebookLikeUsContainer;

    @BindView(R.id.controller_send_invite_edit_text_personal_invitation)
    EditText mPersonalInvitationMessageEditText;

    @BindView(R.id.controller_send_invite_edit_text_deals_direct_link)
    EditText mPersonalInvitationLinkEditText;

    @BindView(R.id.controller_send_invite_text_invite_friend_link)
    TextView mInviteFriendEditLinkButton;

    GetInviteResponse.Value inviteBody;
    String inviteSubject;
    String inviteLink;
    String inviteMessage;
    String bannerImageUrl;
    String twitter = "com.twitter.android";
    String twitterComposerClass = "com.twitter.android.composer.ComposerActivity";

    ProgressDialog progress;

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

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Invite Friends");
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setOnClickListener(action -> {
            getActivity().onBackPressed();
        });

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

        mInviteFriendEditLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String textValue = mInviteFriendEditLinkButton.getText().toString();
                if (textValue.equals("ok")){
                    mPersonalInvitationMessageEditText.setClickable(true);
                    mPersonalInvitationMessageEditText.setFocusable(true);

                    mInviteFriendEditLinkButton.setText("...");
                    String editedLink = mPersonalInvitationLinkEditText.getText().toString();
                    SetInviteRequest setInviteLinkRequest = new SetInviteRequest();
                    setInviteLinkRequest.inviteLink = editedLink;

                    mPresenter.setInviteLink(setInviteLinkRequest);

                    progress.setTitle("Loading");
                    progress.setMessage("Wait while checking link...");
                    progress.setCancelable(false);
                    progress.show();


                }else{
                    mPersonalInvitationMessageEditText.setClickable(false);
                    mPersonalInvitationMessageEditText.setFocusable(false);

                    mPersonalInvitationLinkEditText.setActivated(true);
                    mPersonalInvitationLinkEditText.setPressed(true);
                    mPersonalInvitationLinkEditText.setClickable(true);
                    mPersonalInvitationLinkEditText.setEnabled(true);
                    mPersonalInvitationLinkEditText.setFocusable(true);
                    mPersonalInvitationLinkEditText.setFocusableInTouchMode(true);
                    mPersonalInvitationLinkEditText.requestFocus();
                    mInviteFriendEditLinkButton.setText("ok");
                }
            }
        });


        mTwitterSendInvitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString()+" , ";
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();
                String messageWithInvite = personalInvitation+invitationLink;

                Intent twitterIntent
                        = getActivity().getPackageManager().getLaunchIntentForPackage(twitter);
                if (twitterIntent != null) {

                    try
                    {
                        // Check if the Twitter app is installed on the phone.
                        getActivity().getPackageManager().getPackageInfo(twitter, 0);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName(twitter, twitterComposerClass);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, messageWithInvite);
                        intent.putExtra(Intent.EXTRA_STREAM, invitationLink);
                        intent.setData(Uri.parse(bannerImageUrl));
                        startActivity(intent);

                    }
                    catch (Exception e)
                    {
//                        CustomAlertDialog.showCustomAlertDialog(
//                                mActivity,
//                                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                                mActivity.getString(R.string.twitter_not_installed));
                    }

                } else {

                    String url = "http://www.twitter.com/intent/tweet?url=YOURURL&text=";
                    String sendUrl = url + personalInvitation;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(sendUrl));
                    startActivity(i);

                }
            }
        });

        mFacebookSendInvitationLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {

                String personalInvitation = mPersonalInvitationMessageEditText.getText().toString();
                String invitationLink = mPersonalInvitationLinkEditText.getText().toString();

                try {

                    Log.d("sendinvitefriend", bannerImageUrl);

//                    ShareLinkContent content =
//                            new ShareLinkContent.Builder()
//                                    .setContentUrl(Uri.parse(invitationLink))
//                                    .setContentTitle(inviteSubject)
//                                    .setImageUrl(Uri.parse(bannerImageUrl))
//                                    .setContentDescription(inviteMessage)
//                                    .setQuote(personalInvitation)
//                                    .build();
//                    ShareDialog shareDialog = new ShareDialog(getActivity());
//                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

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
//                    CustomAlertDialog.showCustomAlertDialog(
//                            mActivity,
//                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                            mActivity.getString(R.string.facebook_link_missing));
                }
            }
        });

        mTwitterFollowUsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = mPresenter.getFollowUsTwitterLink();

                if (!link.isEmpty()) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(intent);

                } else {

//                    CustomAlertDialog.showCustomAlertDialog(
//                            mActivity,
//                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                            mActivity.getString(R.string.twitter_link_missing));
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
        bannerImageUrl = inviteBody.getBannerUrl();

        mPersonalInvitationLinkEditText.setText(inviteLink);
        mSendInvitationContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInviteLinkSet(SetInviteResponse setInviteLinkResponseBody) {
        Boolean isEditLinkSuccessful = setInviteLinkResponseBody.getValue().getResult();

        if (isEditLinkSuccessful) {
            mPersonalInvitationLinkEditText.setEnabled(false);
            mPersonalInvitationLinkEditText.setFocusable(false);
            mPersonalInvitationLinkEditText.setFocusableInTouchMode(false);

            mInviteFriendEditLinkButton.setText("edit");

            progress.dismiss();

//            CustomAlertDialog.showCustomAlertDialog(
//                    mActivity,
//                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mActivity.getString(R.string.edit_successful));

        } else {
            mPersonalInvitationLinkEditText.setText(inviteLink);

            mPersonalInvitationLinkEditText.setEnabled(false);
            mPersonalInvitationLinkEditText.setFocusable(false);
            mPersonalInvitationLinkEditText.setFocusableInTouchMode(false);

            mInviteFriendEditLinkButton.setText("edit");
            progress.dismiss();

//            CustomAlertDialog.showCustomAlertDialog(
//                    mActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    editLinkResponse
//            );
        }
    }
}
