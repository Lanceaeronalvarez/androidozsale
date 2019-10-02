package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.collect.Sets;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.CommonUtils;

public class BottomSheetSizesDialog extends BottomSheetDialogFragment {

    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();
    private HashSet<Integer> mIndicesOfSoldOutSizes = new HashSet<>();
    private OnDoneListener mOnDoneListener = null;
    private OnSizeGuideTappedListener mOnSizeGuideTappedListener = null;

    private View mMainLayout = null;

    private int mSelectedSizeIndex = -1;

    TextView mHeader;
    TextView mSizeGuide;
    TagFlowLayout mSizesFlowLayout;
    Button mDoneButton;

    public interface OnDoneListener {
        void onDonePressed(int selectedIndex);
    }

    public interface OnSizeGuideTappedListener {
        void onSizeGuideTapped();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) mMainLayout.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_sizes_content, container, false);

        bindViews(v);

        setupSizeGuide();
        setupFlowLayout();
        setupDoneButton();

        mMainLayout = v;

        return v;
    }

    private void bindViews(View v) {
        mHeader = v.findViewById(R.id.bottom_sheet_sizes_header);

        mSizeGuide = v.findViewById(R.id.bottom_sheet_sizes_size_guide);

        mSizesFlowLayout = v.findViewById(R.id.bottom_sheet_sizes_size_list);

        mDoneButton = v.findViewById(R.id.bottom_sheet_sizes_done_button);

    }

    private void setupSizeGuide() {
        mSizeGuide.setVisibility(mOnSizeGuideTappedListener == null ? View.GONE : View.VISIBLE);
        mSizeGuide.setOnClickListener(v -> {
            if (mOnSizeGuideTappedListener != null) {
                mOnSizeGuideTappedListener.onSizeGuideTapped();
            }
        });
    }

    private void setupFlowLayout() {
        if (mSizesFlowLayout == null) {
            return;
        }

        mSizesFlowLayout.setOnTagClickListener((view, position, parent) -> false);

        TagAdapter mSizesAdapter = new TagAdapter<Pair<String, String>>(mProductSizes) {

            @SuppressWarnings("ConstantConditions")
            @Override
            public View getView(FlowLayout parent, int position, Pair<String, String> data) {
                TextView tv = (TextView) getActivity().getLayoutInflater()
                        .inflate(R.layout.sizes_chips_layout,
                                parent,
                                false);
                tv.setText(data.first);
                if (mIndicesOfSoldOutSizes.contains(position)) {
                    tv.setBackground(getContext().getDrawable(R.drawable.bg_chips_soldout));
                    tv.setTextColor(getContext().getResources().getColor(R.color.bg_chips_soldout_text));
                }

                return tv;
            }
        };

        mSizesFlowLayout.setAdapter(mSizesAdapter);

        mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
            onSelectTag(selectPosSet.isEmpty() ? -1 : selectPosSet.iterator().next());
        });

    }

    private void setupDoneButton() {
        if (mDoneButton == null) {
            return;
        }

        mDoneButton.setOnClickListener(v -> {
            if (mSelectedSizeIndex < 0) {
                bringToAttention();
            } else {
                if (mOnDoneListener != null) {
                    mOnDoneListener.onDonePressed(mSelectedSizeIndex);
                }
                BottomSheetSizesDialog.this.dismiss();
            }
        });
    }

    private void onSelectTag(int index) {
        int selectedIndex = index;

        // if selection is invalid, try reselecting previous index
        if (index < 0) {
            if (mSelectedSizeIndex >= 0) {
                selectedIndex = mSelectedSizeIndex;
            } else {
                return;
            }
        }

        boolean isSizeSoldOut = mIndicesOfSoldOutSizes.contains(index);
        if (isSizeSoldOut) {
            // if current selection is sold out, try selecting previous index
            onSelectTag(-1);
            return;
        }

        // force selection - this prevents deselecting tags
        mSizesFlowLayout.getAdapter().setSelectedList(Sets.newHashSet(selectedIndex));

        if (selectedIndex != mSelectedSizeIndex) {
            mSelectedSizeIndex = selectedIndex;
        }
    }

    private void bringToAttention() {
        CommonUtils.shakeView(mSizesFlowLayout);
        mHeader.setTextColor(getResources().getColor(R.color.red));
    }

    public ArrayList<Pair<String, String>> getProductSizes() {
        return mProductSizes;
    }

    public void setProductSizes(ArrayList<Pair<String, String>> productSizes) {
        mProductSizes = productSizes;
        setupFlowLayout();
    }

    public HashSet<Integer> getIndicesOfSoldOutItems() {
        return mIndicesOfSoldOutSizes;
    }

    public void setIndicesOfSoldOutSizes(HashSet<Integer> indicesOfSoldOutSizes) {
        mIndicesOfSoldOutSizes = indicesOfSoldOutSizes;
        if (mSizesFlowLayout != null) {
            mSizesFlowLayout.getAdapter().notifyDataChanged();
        }
    }

    public OnDoneListener getOnDoneListener() {
        return mOnDoneListener;
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        mOnDoneListener = onDoneListener;
    }

    public OnSizeGuideTappedListener getOnSizeGuideTappedListener() {
        return mOnSizeGuideTappedListener;
    }

    public void setOnSizeGuideTappedListener(OnSizeGuideTappedListener onSizeGuideTappedListener) {
        mOnSizeGuideTappedListener = onSizeGuideTappedListener;
        if (mSizeGuide != null) {
            mSizeGuide.setVisibility(mOnSizeGuideTappedListener == null ? View.GONE : View.VISIBLE);
        }
    }
}
