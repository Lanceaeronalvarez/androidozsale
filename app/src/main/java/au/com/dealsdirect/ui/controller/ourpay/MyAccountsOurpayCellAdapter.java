package au.com.dealsdirect.ui.controller.ourpay;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydata.ProcessOurpayInstallmentRequest;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAccountsOurpayCellAdapter extends RecyclerView.Adapter {
    private final static int VIEW_TYPE_SECTION_HEADER = -1;
    final static int VIEW_TYPE_PAYMENT_PLANS = 0;
    final static int VIEW_TYPE_SCHEDULED_PAYMENTS = 1;

    private static MyAccountsOurpayMvpPresenter mPresenter;
    private static MyAccountsOurpayListener mMyAccountsOurpayListener;

    private static class OrderedDataItem {

        private OrderedDataItem() {
        }

        static class Section extends OrderedDataItem {
            CharSequence mName;
            int mIndex;

            Section(CharSequence name, int sectionIndex) {
                mName = name;
                mIndex = sectionIndex;
            }
        }

        static class Row extends OrderedDataItem {
            MyAccountsOurpayDataSource.Item mItem;
            IndexPath mIndexPath;

            Row(MyAccountsOurpayDataSource.Item item, IndexPath indexPath) {
                mItem = item;
                mIndexPath = indexPath;
            }

        }
    }

    public interface OnItemViewTouchEventListener {
        int UNDEFINED = 0;
        int PAYMENT_PLANS_CHEVRON = 1;
        int PAYMENT_PLANS_DOWN_ARROW = 2;

        boolean onViewTouched(WeakReference<MyAccountsOurpayCellAdapter> adapter,
                              int viewType,
                              IndexPath indexPath,
                              MotionEvent event);
    }

    private OnItemViewTouchEventListener mOnItemViewTouchEventListener;

    public static class IndexPath {
        private int mSection;

        public int getSection() {
            return mSection;
        }

        public void setSection(int section) {
            mSection = section;
        }

        private int mRow;

        public int getRow() {
            return mRow;
        }

        public void setRow(int row) {
            mRow = row;
        }

        IndexPath(int section, int row) {
            mSection = section;
            mRow = row;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof IndexPath) {
                return mSection == ((IndexPath) obj).mSection &&
                        mRow == ((IndexPath) obj).mRow;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mSection, mRow);
        }
    }

    public static class RowMode {
        final static int NORMAL = 0;
        final static int EXPANDED_REFUND = 1;
        final static int EXPANDED_INSTALLMENTS = 1 << 1;

        private int mRawValue;

        int getRawValue() {
            return mRawValue;
        }

        RowMode() {
            mRawValue = 0;
        }


        RowMode(int rawValue) {
            mRawValue = rawValue;
        }

        RowMode add(int rawValue) {
            mRawValue = mRawValue | rawValue;
            return this;
        }

        RowMode add(RowMode rowMode) {
            return add(rowMode.mRawValue);
        }

        RowMode subtract(int rawValue) {
            mRawValue = mRawValue & ~rawValue;
            return this;
        }

        RowMode subtract(RowMode rowMode) {
            return subtract(rowMode.mRawValue);
        }

        boolean contains(int rawValue) {
            return (mRawValue & rawValue) != 0;
        }

        boolean contains(RowMode rowMode) {
            return contains(rowMode.mRawValue);
        }

        RowMode toggle(int rawValue) {
            if (contains(rawValue)) {
                return subtract(rawValue);
            } else {
                return add(rawValue);
            }
        }

        RowMode toggle(RowMode rowMode) {
            return toggle(rowMode.mRawValue);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RowMode) {
                return mRawValue == ((RowMode) obj).mRawValue;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mRawValue);
        }
    }

    private HashMap<IndexPath, RowMode> mRowModes = new HashMap<>();

    HashMap<IndexPath, RowMode> getRowModes() {
        return mRowModes;
    }

    private MyAccountsOurpayDataSource mDataSource;

    private ArrayList<OrderedDataItem> mOrderedDataItems = new ArrayList<>();

    private HashMap<IndexPath, Integer> mOrderedDataItemsIndexPathMap = new HashMap<>();

    private int mViewType;

    public int getViewType() {
        return mViewType;
    }

    private String mId;

    public String getId() {
        return mId;
    }

    MyAccountsOurpayCellAdapter(MyAccountsOurpayMvpPresenter presenter,
                                MyAccountsOurpayListener myAccountsOurpayListener,
                                String id,
                                int viewType,
                                MyAccountsOurpayDataSource dataSource,
                                OnItemViewTouchEventListener listener) {

        mPresenter = presenter;
        mMyAccountsOurpayListener = myAccountsOurpayListener;
        mId = id;
        mViewType = viewType;
        mDataSource = dataSource;
        mOnItemViewTouchEventListener = listener;

        reloadData();
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.myAccountOurpaySectionHeader)
        TextView mTitle;

        int getSuggestedRowHeight() {
            // replace with XML constant
            return (int) (56 * itemView.getContext().getResources().getDisplayMetrics().density);
        }

        SectionHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void populate(CharSequence title) {
            mTitle.setText(title);
        }
    }

    static class PaymentPlanViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.myAccountOurpayPaymentPlanView)
        LinearLayout mMainView;

        @BindView(R.id.myAccountOurpayPaymentPlanTitle)
        TextView mTitle;

        @BindView(R.id.myAccountOurpayPaymentPlanName)
        TextView mName;

        @BindView(R.id.myAccountOurpayPaymentPlanPaid)
        TextView mPaid;

        @BindView(R.id.myAccountOurpayPaymentPlanRefund)
        TextView mRefund;

        @BindView(R.id.myAccountOurpayPaymentPlanAdjustedSchedule)
        TextView mAdjustedSchedule;

        @BindView(R.id.myAccountOurpayPaymentPlanRefundToCard)
        TextView mRefundToCard;

        @BindView(R.id.myAccountOurpayPaymentPlanBalance)
        TextView mBalance;

        @BindView(R.id.myAccountOurpayPaymentPlanSeparatorAbovePaymentMethod)
        View mSeparatorAbovePaymentMethod;

        @BindView(R.id.myAccountOurpayPaymentPlanPaymentMethodIcon)
        ImageView mPaymentMethodIcon;

        @BindView(R.id.myAccountOurpayPaymentPlanObfuscatedNumber)
        TextView mObfuscatedNumber;

        @BindView(R.id.myAccountOurpayPaymentPlanSeparatorBelowPaymentMethod)
        View mSeparatorBelowPaymentMethod;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentsView)
        LinearLayout mInstallmentsView;

        @BindView(R.id.myAccountOurpayPaymentPlanChevron)
        ImageView mChevron;

        @BindView(R.id.myAccountOurpayPaymentPlanDownArrow)
        ImageView mDownArrow;

        ArrayList<InstallmentViewHolder> mInstallmentViewHolders = new ArrayList<>();

        IndexPath mIndexPath = null;

        WeakReference<MyAccountsOurpayCellAdapter> mAdapter;

        void setNumberOfInstallments(int number) {
            if (number <= 0) {
                return;
            }

            int oldCount = mInstallmentViewHolders.size();

            if (number == oldCount) {
                return;
            }

            InstallmentViewHolder firstViewHolder = null;
            InstallmentViewHolder lastViewHolder = null;

            if (oldCount > 0) {
                firstViewHolder = mInstallmentViewHolders.get(0);
                lastViewHolder = mInstallmentViewHolders.get(oldCount - 1);
            }

            while (mInstallmentsView.getChildCount() < number) {
                InstallmentViewHolder newInstallmentView = InstallmentViewHolder
                        .newInstance(mInstallmentsView);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                newInstallmentView.mView.setLayoutParams(param);
                mInstallmentsView.addView(newInstallmentView.mView);
                mInstallmentViewHolders.add(newInstallmentView);

                newInstallmentView.mNumber.setText(String.valueOf(mInstallmentViewHolders.size()));
            }

            while (mInstallmentsView.getChildCount() > number) {
                mInstallmentsView.removeViewAt(mInstallmentsView.getChildCount() - 1);
                mInstallmentViewHolders.remove(mInstallmentViewHolders.size() - 1);
            }

            if (firstViewHolder == null) {
                firstViewHolder = mInstallmentViewHolders.get(0);
            }
            firstViewHolder.mLeftLine.setVisibility(View.GONE);

            if (lastViewHolder != null) {
                lastViewHolder.mRightLine.setVisibility(View.VISIBLE);
            }

            mInstallmentViewHolders.get(number - 1).mRightLine.setVisibility(View.GONE);
        }

        int getNumberOfInstallments() {
            return mInstallmentsView.getChildCount();
        }

        int getSuggestedRowHeight() {
            mMainView.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int height = mMainView.getMeasuredHeight();
            height += itemView.getResources()
                    .getDimension(R.dimen.ourpay_payment_plan_cell_vertical_margins);
            return height;
        }

        @SuppressLint("ClickableViewAccessibility")
        PaymentPlanViewHolder(View view,
                              MyAccountsOurpayCellAdapter adapter,
                              OnItemViewTouchEventListener listener) {
            super(view);
            ButterKnife.bind(this, view);

            if (adapter != null) {
                mAdapter = new WeakReference<>(adapter);
            }

            if (listener != null) {
                view.setOnTouchListener((v, event) -> {
                    float horizontalPadding = v.getContext().getResources()
                            .getDimension(R.dimen.ourpay_cell_button_horizontal_padding);
                    float verticalPadding = v.getContext().getResources()
                            .getDimension(R.dimen.ourpay_cell_button_vertical_padding);

                    Rect rootRect = new Rect();
                    v.getGlobalVisibleRect(rootRect);

                    if (mChevron != null && mChevron.getVisibility() == View.VISIBLE) {

                        Rect chevronRect = new Rect();
                        mChevron.getGlobalVisibleRect(chevronRect);
                        chevronRect.left += -rootRect.left - horizontalPadding;
                        chevronRect.right += -rootRect.left + horizontalPadding;
                        chevronRect.top += -rootRect.top - verticalPadding;
                        chevronRect.bottom += -rootRect.top + verticalPadding;

                        if (chevronRect.contains((int) event.getX(), (int) event.getY())) {
                            return listener
                                    .onViewTouched(
                                            mAdapter,
                                            OnItemViewTouchEventListener.PAYMENT_PLANS_CHEVRON,
                                            mIndexPath,
                                            event);
                        }
                    }

                    if (mDownArrow != null && mDownArrow.getVisibility() == View.VISIBLE) {

                        Rect downArrowRect = new Rect();
                        mDownArrow.getGlobalVisibleRect(downArrowRect);
                        downArrowRect.left += -rootRect.left - horizontalPadding;
                        downArrowRect.right += -rootRect.left + horizontalPadding;
                        downArrowRect.top += -rootRect.top - verticalPadding;
                        downArrowRect.bottom += -rootRect.top + verticalPadding;

                        if (downArrowRect.contains((int) event.getX(), (int) event.getY())) {
                            return listener
                                    .onViewTouched(
                                            mAdapter,
                                            OnItemViewTouchEventListener.PAYMENT_PLANS_DOWN_ARROW,
                                            mIndexPath,
                                            event);
                        }

                    }

                    return listener
                            .onViewTouched(
                                    mAdapter,
                                    OnItemViewTouchEventListener.UNDEFINED,
                                    mIndexPath,
                                    event);
                });
            }
        }

        void populate(MyAccountsOurpayDataSource.Item.PaymentPlan data) {

            mTitle.setText(data.getTotal());
            mName.setText(data.getName());
            mPaid.setText(data.getPaid());
            mRefund.setText(data.getRefund());

            // Visibility will be determined in setupRowMode()
            mAdjustedSchedule.setText(data.getAdjustedSchedule());

            // Visibility will be determined in setupRowMode()
            mRefundToCard.setText(data.getRefundToCard());

            mBalance.setText(data.getBalance());

            if (data.getObfuscatedCardNumber() != null && data.getObfuscatedCardNumber().length() > 0) {
                mSeparatorAbovePaymentMethod.setVisibility(View.VISIBLE);
                mObfuscatedNumber.setVisibility(View.VISIBLE);
                mObfuscatedNumber.setText(data.getObfuscatedCardNumber());

                if (data.getPaymentMethodIconImage(itemView.getContext()) != null) {
                    mPaymentMethodIcon.setVisibility(View.VISIBLE);
                    mPaymentMethodIcon.setImageDrawable(data.getPaymentMethodIconImage(itemView.getContext()));
                } else {
                    mPaymentMethodIcon.setVisibility(View.GONE);
                    mPaymentMethodIcon.setImageDrawable(null);
                }
            } else {
                mSeparatorAbovePaymentMethod.setVisibility(View.GONE);
                mObfuscatedNumber.setVisibility(View.GONE);
                mPaymentMethodIcon.setVisibility(View.GONE);
            }

            List<MyAccountsOurpayDataSource.Item.PaymentPlan.Installment> installments =
                    data.getInstallments();
            if (installments != null) {
                setNumberOfInstallments(installments.size());

                // Might need to call toggleInstallmentViewsVisibilityIfNotEmpty() here, but
                // it doesn't take into account the rowMode, so just make sure to call
                // setupRowMode() after calling populate()

                for (int i = 0; i < installments.size(); i += 1) {
                    MyAccountsOurpayDataSource.Item.PaymentPlan.Installment installmentData = installments.get(i);
                    mInstallmentViewHolders.get(i).populate(installmentData, i);
                }
            } else {
                setNumberOfInstallments(0);
                // hides it now that it's empty
                toggleInstallmentViewsVisibilityIfNotEmpty();
            }

            if (getNumberOfInstallments() > 0) {
                mChevron.setVisibility(View.VISIBLE);
            } else {
                mChevron.setVisibility(View.GONE);
            }

            if (canExpandRefundViews(data)) {
                mDownArrow.setVisibility(View.VISIBLE);
            } else {
                mDownArrow.setVisibility(View.GONE);
            }
        }

        void setupForRowMode(RowMode rowMode) {
            if (rowMode != null && rowMode.contains(RowMode.EXPANDED_REFUND)) {
                toggleTextViewVisibilityIfNotEmpty(mAdjustedSchedule);
                toggleTextViewVisibilityIfNotEmpty(mRefundToCard);
            } else {
                mAdjustedSchedule.setVisibility(View.GONE);
                mRefundToCard.setVisibility(View.GONE);
            }

            if (rowMode != null && rowMode.contains(RowMode.EXPANDED_INSTALLMENTS)) {
                toggleInstallmentViewsVisibilityIfNotEmpty();
            } else {
                mSeparatorBelowPaymentMethod.setVisibility(View.GONE);
                mInstallmentsView.setVisibility(View.GONE);
            }
        }

        private void toggleTextViewVisibilityIfNotEmpty(TextView textView) {
            CharSequence text = textView.getText();
            if (text != null && text.length() > 0) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void toggleInstallmentViewsVisibilityIfNotEmpty() {
            if (getNumberOfInstallments() > 0) {
                mSeparatorBelowPaymentMethod.setVisibility(View.VISIBLE);
                mInstallmentsView.setVisibility(View.VISIBLE);
                mChevron.setImageDrawable(mChevron.getContext()
                        .getResources().getDrawable(R.drawable.ic_myaccount_ourpay_arrow_up));
            } else {
                mSeparatorBelowPaymentMethod.setVisibility(View.GONE);
                mInstallmentsView.setVisibility(View.GONE);
                mChevron.setImageDrawable(mChevron.getContext()
                        .getResources().getDrawable(R.drawable.ic_myaccount_ourpay_arrow_down));
            }
        }

        private boolean canExpandRefundViews(MyAccountsOurpayDataSource.Item.PaymentPlan data) {
            return (data.getAdjustedSchedule() != null && data.getAdjustedSchedule().length() > 0) ||
                    (data.getRefundToCard() != null && data.getRefundToCard().length() > 0);
        }
    }

    static class InstallmentViewHolder {
        View mView;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentLeftLine)
        View mLeftLine;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentRightLine)
        View mRightLine;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentDate)
        TextView mDate;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentValue)
        TextView mValue;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentNumber)
        TextView mNumber;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentSuccessful)
        View mSuccessful;

        @BindView(R.id.myAccountOurpayPaymentPlanInstallmentFailure)
        View mFailure;

        InstallmentViewHolder(View view) {
            mView = view;
            ButterKnife.bind(this, view);
        }

        static InstallmentViewHolder newInstance(ViewGroup parent) {
            return new InstallmentViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.myaccount_ourpay_payment_plan_installment,
                            parent,
                            false));
        }

        void populate(MyAccountsOurpayDataSource.Item.PaymentPlan.Installment data, int index) {
            switch (data.getState()) {
                case Pending:
                    mNumber.setVisibility(View.VISIBLE);
                    mSuccessful.setVisibility(View.GONE);
                    mFailure.setVisibility(View.GONE);

                    mNumber.setText(String.valueOf(index + 1));
                    break;
                case Success:
                    mNumber.setVisibility(View.GONE);
                    mSuccessful.setVisibility(View.VISIBLE);
                    mFailure.setVisibility(View.GONE);
                    break;
                case Fail:
                    mNumber.setVisibility(View.GONE);
                    mSuccessful.setVisibility(View.GONE);
                    mFailure.setVisibility(View.VISIBLE);
                    break;
            }

            mValue.setText(data.getAmount());
            mDate.setText(data.getDate());
        }
    }

    static class ScheduledPaymentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.myAccountOurpayScheduledPaymentView)
        LinearLayout mMainView;

        @BindView(R.id.myAccountOurpayScheduledPaymentBigDate)
        TextView mBigDate;

        @BindView(R.id.myAccountOurpayScheduledPaymentSmallDate)
        TextView mSmallDate;

        @BindView(R.id.myAccountOurpayScheduledPaymentTitle)
        TextView mTitle;

        @BindView(R.id.myAccountOurpayScheduledPaymentName)
        TextView mName;

        @BindView(R.id.myAccountOurpayScheduledPaymentPaymentMethodIcon)
        ImageView mPaymentMethodIcon;

        @BindView(R.id.myAccountOurpayScheduledPaymentObfuscatedNumber)
        TextView mObfuscatedNumber;

        @BindView(R.id.myAccountOurpayScheduledPaymentSeparatorAbovePaymentMethod)
        View mSeparatorAbovePaymentMethod;

        @BindView(R.id.myAccountOurpayScheduledPaymentPayButton)
        Button mPayButton;

        int getSuggestedRowHeight() {
            // replace with XML constant
            return (int) (100 * itemView.getContext().getResources().getDisplayMetrics().density);
        }

        ScheduledPaymentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void populate(MyAccountsOurpayDataSource.Item.ScheduledPayment data) {
            mBigDate.setText(data.getBigDate());
            mSmallDate.setText(data.getSmallDate());
            mTitle.setText(data.getTotal());
            mName.setText(data.getName());
            mPaymentMethodIcon.setImageDrawable(data.getPaymentMethodIconImage(itemView.getContext()));
            mObfuscatedNumber.setText(data.getObfuscatedCardNumber());

            mPayButton.setOnClickListener(v -> {
                mMyAccountsOurpayListener.showLoadingDialog();
                ProcessOurpayInstallmentRequest processOurpayInstallmentRequest = new ProcessOurpayInstallmentRequest();
                processOurpayInstallmentRequest.setInstallmentId(String.valueOf(data.getTransactionId()));
                processOurpayInstallmentRequest.setBillingAgreementId(String.valueOf(data.getBillingAgreementId()));
                mPresenter.processOurpayInstallment(processOurpayInstallmentRequest);
            });
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;

        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                vh = new SectionHeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.myaccount_ourpay_section_header, parent, false));
                break;
            case VIEW_TYPE_PAYMENT_PLANS:
                vh = new PaymentPlanViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.myaccount_ourpay_payment_plan_cell, parent, false),
                        this,
                        mOnItemViewTouchEventListener);
                break;
            case VIEW_TYPE_SCHEDULED_PAYMENTS:
                vh = new ScheduledPaymentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.myaccount_ourpay_scheduled_payment_cell, parent, false));
                break;
        }

        assert (vh != null);

        return vh;
    }

    private <T extends OrderedDataItem> T getOrderedItem(Class<T> itemClass, int position) {
        OrderedDataItem item = mOrderedDataItems.get(position);
        if (itemClass.isInstance(item)) {
            return itemClass.cast((item));
        } else {
            return null;
        }
    }

    public IndexPath getIndexPathFromPosition(int position) {
        OrderedDataItem.Row row = getOrderedItem(OrderedDataItem.Row.class, position);
        if (row != null) {
            return row.mIndexPath;
        } else {
            return null;
        }
    }

    public Integer getPositionFromIndexPath(IndexPath indexPath) {
        return mOrderedDataItemsIndexPathMap.get(indexPath);
    }

    private <T extends MyAccountsOurpayDataSource.Item> T getPlan(Class<T> itemClass, int position) {
        OrderedDataItem.Row row = getOrderedItem(OrderedDataItem.Row.class, position);
        if (row != null && itemClass.isInstance(row.mItem)) {
            return itemClass.cast(row.mItem);
        } else {
            return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IndexPath indexPath = getIndexPathFromPosition(position);
        RowMode rowMode = mRowModes.get(indexPath);

        int height = 0;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SECTION_HEADER:
                SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;

                OrderedDataItem.Section sectionItem =
                        getOrderedItem(OrderedDataItem.Section.class, position);

                if (sectionItem != null) {
                    sectionHeaderViewHolder.populate(sectionItem.mName);
                }

                height = sectionHeaderViewHolder.getSuggestedRowHeight();
                break;
            case VIEW_TYPE_PAYMENT_PLANS:
                PaymentPlanViewHolder paymentPlanViewHolder = (PaymentPlanViewHolder) holder;

                paymentPlanViewHolder.populate(getPlan(
                        MyAccountsOurpayDataSource.Item.PaymentPlan.class, position));

                paymentPlanViewHolder.mIndexPath = getIndexPathFromPosition(position);

                paymentPlanViewHolder.setupForRowMode(rowMode);

                height = paymentPlanViewHolder.getSuggestedRowHeight();
                break;
            case VIEW_TYPE_SCHEDULED_PAYMENTS:
                ScheduledPaymentViewHolder scheduledPaymentViewHolder =
                        (ScheduledPaymentViewHolder) holder;

                scheduledPaymentViewHolder.populate(getPlan(
                        MyAccountsOurpayDataSource.Item.ScheduledPayment.class, position));

                height = ((ScheduledPaymentViewHolder) holder).getSuggestedRowHeight();

                if (mId.equalsIgnoreCase("PastPayments")){
                    ((ScheduledPaymentViewHolder) holder).mPayButton.setVisibility(View.GONE);
                }
                break;
        }
        holder.itemView.getLayoutParams().height = height;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof PaymentPlanViewHolder) {
            ((PaymentPlanViewHolder) holder).mIndexPath = null;
        }
    }

    public int getItemViewType(int position) {
        if (mOrderedDataItems.get(position) instanceof OrderedDataItem.Section) {
            return VIEW_TYPE_SECTION_HEADER;
        } else {
            return mViewType;
        }
    }

    @Override
    public int getItemCount() {
        return mOrderedDataItems.size();
    }

    public void notifyItemChanged(IndexPath indexPath) {
        Integer index = mOrderedDataItemsIndexPathMap.get(indexPath);
        if (index != null) {
            notifyItemChanged(index);
        }
    }

    void reloadData() {
        if (mDataSource == null) {
            return;
        }

        mOrderedDataItems.clear();

        mOrderedDataItemsIndexPathMap.clear();

        List<MyAccountsOurpayDataSource.Group<? extends MyAccountsOurpayDataSource.Item>> groups =
                mDataSource.getData(this);

        if (groups != null) {

            // Need to map data source into a single list for O(1) access times
            for (int section = 0; section < groups.size(); section += 1) {
                MyAccountsOurpayDataSource.Group<? extends MyAccountsOurpayDataSource.Item> group =
                        groups.get(section);

                mOrderedDataItems.add(new OrderedDataItem.Section(group.getName(), section));

                List<? extends MyAccountsOurpayDataSource.Item> items = group.getItems();

                for (int row = 0; row < items.size(); row += 1) {
                    IndexPath indexPath = new IndexPath(section, row);
                    mOrderedDataItemsIndexPathMap.put(indexPath, mOrderedDataItems.size());
                    mOrderedDataItems.add(new OrderedDataItem.Row(items.get(row),
                            indexPath));
                }
            }

        }

        notifyDataSetChanged();
    }
}
