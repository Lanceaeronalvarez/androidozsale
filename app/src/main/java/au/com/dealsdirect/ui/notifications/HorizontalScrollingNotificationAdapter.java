package au.com.dealsdirect.ui.notifications;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsResponse;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalScrollingNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static Pattern SPAN_PATTERN = Pattern.compile("<span>(.*?)<\\/span>");

    private List<GetNotificationsResponse> dataSource = new ArrayList<>();

    private RecyclerView recyclerView = null;

    private boolean shouldRepeatCellsToFillWidth = true;

    private boolean willScrollWrapAround = true;

    private GetNotificationsResponse selectedNotification = null;

    public HorizontalScrollingNotificationAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_notification_ticket, parent, false);
        return new NotificationTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int virtualPosition = position % dataSource.size();
        final GetNotificationsResponse item = dataSource.get(virtualPosition);

        SpannableStringBuilder upperText = new SpannableStringBuilder();
        if (item.getDiscountLeft() != null) {
            upperText.append(PriceUtils.getPriceStringValue(item.getDiscountLeft()));
            upperText.append(" OFF");
            upperText.setSpan(new StyleSpan(Typeface.BOLD), 0, upperText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (item.getPromoCode() != null) {
            if (upperText.length() > 0) {
                upperText.append("\n");
            }
            upperText.append(item.getPromoCode());
        } else {
            upperText.setSpan(new RelativeSizeSpan(2f), 0, upperText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//        String escapedSpans = item.getText().replace("<span>", "<span>");
//        escapedSpans = escapedSpans.replace("</span>", "</span>");
        Spanned spanned = HtmlCompat.fromHtml(item.getText(), HtmlCompat.FROM_HTML_MODE_LEGACY);
        Matcher matcher = SPAN_PATTERN.matcher(spanned);
        SpannableStringBuilder lowerText = new SpannableStringBuilder(spanned);
        do {
            if (matcher.find()) {
                int regionEnd = matcher.start() + matcher.end();
                lowerText.replace(matcher.start(0), matcher.end(0), matcher.group(2))
                        .setSpan(
                                new StyleSpan(Typeface.BOLD),
                                matcher.start(),
                                regionEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                matcher.reset(lowerText);
                matcher.region(regionEnd, lowerText.length());
            }
        } while (!matcher.hitEnd());

        NotificationTicketViewHolder notificationTicketViewHolder = (NotificationTicketViewHolder) holder;
        notificationTicketViewHolder.upperText.setText(upperText);
        notificationTicketViewHolder.lowerText.setText(lowerText);

        selectedNotification = item;
    }

    @Override
    public int getItemCount() {
        return (dataSource != null && !dataSource.isEmpty()) ?
                dataSource.size() + getEdgeBufferSize() * 2 : 0;
    }

    public boolean isShouldRepeatCellsToFillWidth() {
        return shouldRepeatCellsToFillWidth;
    }

    public void setShouldRepeatCellsToFillWidth(boolean shouldRepeatCellsToFillWidth) {
        this.shouldRepeatCellsToFillWidth = shouldRepeatCellsToFillWidth;
    }

    public List<GetNotificationsResponse> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<GetNotificationsResponse> dataSource) {
        if (dataSource != null) {
            this.dataSource = new ArrayList<>(dataSource);
            selectedNotification = !dataSource.isEmpty() ? dataSource.get(0) : null;
        } else {
            this.dataSource = new ArrayList<>();
            selectedNotification = null;
        }
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    private int getEdgeBufferSize() {
        if (!willScrollWrapAround) {
            return 0;
        }

        final int datasourceSize = dataSource.size();
        final int cellWidth = (int) recyclerView.getContext().getResources().getDimension(R.dimen.notification_ticket_width);

        if (!shouldRepeatCellsToFillWidth) {
            if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0 &&
                    Math.ceil(recyclerView.getWidth() / (float) cellWidth) > datasourceSize) {
                return 0;
            }
        }

        if (datasourceSize == 0) {
            return 0;
        } else if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0) {
            return (int) (2 * Math.ceil(recyclerView.getWidth() / (float) cellWidth));
        } else {
            return Math.max(3, datasourceSize);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void resetReyclerViewPosition() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(getEdgeBufferSize());
        }
    }

    public void wrapScrollPosition(int speed) {
        if (recyclerView == null || !willScrollWrapAround) {
            return;
        }
        final int x = recyclerView.computeHorizontalScrollOffset();
        final int cellWidth = (int) recyclerView.getContext().getResources().getDimension(R.dimen.notification_ticket_width);

        final int itemSize = getDataSource().size();

        if (speed > 0 && x > cellWidth * (itemSize + getEdgeBufferSize())) {
            recyclerView.scrollBy(-getScrollRange(), 0);
        } else if (speed < 0 && x < cellWidth * getEdgeBufferSize()) {
            recyclerView.scrollBy(getScrollRange(), 0);
        }
    }

    public int getRecyclerViewPosition() {
        return getRecyclerViewPosition(0);
    }

    public int getRecyclerViewPosition(int offset) {
        if (recyclerView == null) {
            return -1;
        }
        int x = recyclerView.computeHorizontalScrollOffset() + offset;
        return getAdapterPositionFromX(x);
    }

    public int getAdapterPositionFromX(int x) {
        final float cellWidth = recyclerView.getContext().getResources().getDimension(R.dimen.notification_ticket_width);
        final int dataSize = getDataSource().size();
        final int index = Math.round(x / cellWidth - getEdgeBufferSize()) % dataSize;
        return index < 0 ? index + dataSize : index;
    }

    private int getScrollRange() {
        final int cellWidth = (int) recyclerView.getContext().getResources().getDimension(R.dimen.notification_ticket_width);
        return cellWidth * getDataSource().size();
    }

    public static class NotificationTicketViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_notification_ticket)
        ViewGroup layout;

        @BindView(R.id.viewholder_notification_ticket_uppertext)
        TextView upperText;

        @BindView(R.id.viewholder_notification_ticket_lowertext)
        TextView lowerText;

        NotificationTicketViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public boolean isWillScrollWrapAround() {
        return willScrollWrapAround;
    }

    public void setWillScrollWrapAround(boolean willScrollWrapAround) {
        this.willScrollWrapAround = willScrollWrapAround;
    }

    public int getCellWidth(Context context) {
        return (int) context.getResources().getDimension(R.dimen.notification_ticket_width);
    }

    public GetNotificationsResponse getSelectedNotification() {
        return selectedNotification;
    }
}