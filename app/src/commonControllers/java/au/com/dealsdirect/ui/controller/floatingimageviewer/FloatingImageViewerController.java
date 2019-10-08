package au.com.dealsdirect.ui.controller.floatingimageviewer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.ScalableImageView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class FloatingImageViewerController extends BaseController implements FloatingImageViewerMvpView {

    public static final String KEY_SOURCE_URL = "FloatingImageViewerController.sourceUrl";
    public static final String KEY_SOURCE_DRAWABLE_ID = "FloatingImageViewerController.sourceDrawableId";

    private String sourceUrl;

    private int sourceDrawableId = -1;

    private OnPhotoTapListener mImageClickListener = null;

    @Inject
    FloatingImageViewerMvpPresenter<FloatingImageViewerMvpView> mPresenter;

    @BindView(R.id.controller_floating_image_viewer_imageview)
    ScalableImageView mImageView;

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }


    @Override
    protected void onAttach(@NonNull View view) {
        mActivity.getMainController().hideBottomNav();
        setUp(view);
        super.onAttach(view);
    }


    @Override
    public void onDetach(View view) {
        mActivity.getMainController().showBottomNav();
        mPresenter.onDetach();
        super.onDetach(view);
    }

    public FloatingImageViewerController(Bundle args) {
        super(args);

        sourceUrl = args.getString(KEY_SOURCE_URL);
        sourceDrawableId = args.getInt(KEY_SOURCE_DRAWABLE_ID, -1);
    }

    @Override
    protected void setUp(View view) {
        mImageView.init();
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Drawable drawable = null;
        if (sourceDrawableId >= 0) {
            drawable = mActivity.getResources().getDrawable(sourceDrawableId);
        }

        if (sourceUrl != null && !sourceUrl.isEmpty()) {
            if (drawable == null) {
                ImageUtils.loadImage(sourceUrl, mImageView);
            } else {
                ImageUtils.loadImageWithPlaceholder(
                        sourceUrl,
                        mImageView,
                        drawable,
                        null);
            }
        } else if (sourceDrawableId >= 0) {
            mImageView.setImageDrawable(drawable);
        }

        mImageView.setOnPhotoTapListener(mImageClickListener);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_floating_image_viewer, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @OnClick(R.id.controller_floating_image_viewer_closebutton)
    void onCloseClick() {
        mActivity.onBackPressed();
    }

    public OnPhotoTapListener getImageClickListener() {
        return mImageClickListener;
    }

    public void setImageClickListener(OnPhotoTapListener mImageClickListener) {
        this.mImageClickListener = mImageClickListener;
    }
}
