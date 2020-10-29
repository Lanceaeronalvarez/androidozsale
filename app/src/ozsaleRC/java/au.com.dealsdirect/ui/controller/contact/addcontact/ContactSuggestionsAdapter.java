package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactsubjecttemplates.ContactSubjectTemplatesResponse;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.ui.controller.contact.listener.ContactSuggestionsClickListener;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_INFO = 0;
    private static final int VIEWTYPE_ACTION = 1;
    private static final int VIEWTYPE_ORDERTRACKING = 2;

    private GetOrdersResponse.Order.Invoice.Delivery delivery = null;

    private List<ContactSubjectTemplatesResponse.Suggestion.Mobile> templates = null;

    private boolean shouldHideActions = false;

    private OrderTrackingClickListener orderTrackingClickListener;
    private ContactSuggestionsClickListener contactSuggestionsClickListener;

    public ContactSuggestionsAdapter(ContactSuggestionsClickListener contactSuggestionsClickListener,
                                     OrderTrackingClickListener orderTrackingClickListener) {
        this.contactSuggestionsClickListener = contactSuggestionsClickListener;
        this.orderTrackingClickListener = orderTrackingClickListener;
    }

    public void setOrderTracker(GetOrdersResponse.Order.Invoice.Delivery delivery) {
        this.delivery = delivery;
    }

    public void setTemplates(List<ContactSubjectTemplatesResponse.Suggestion> templates) {
        this.templates = new ArrayList<>();
        if (templates == null) {
            return;
        }
        for (ContactSubjectTemplatesResponse.Suggestion suggestion : templates) {
            if (suggestion.getMobile() != null) {
                this.templates.add(suggestion.getMobile());
            }
        }
    }

    public boolean isShouldHideActions() {
        return shouldHideActions;
    }

    public void setShouldHideActions(boolean shouldHideActions) {
        this.shouldHideActions = shouldHideActions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_INFO:
                return new ContactSuggestionsAdapter.InfoSuggestionViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.contact_us_suggestions_info,
                                parent, false));
            case VIEWTYPE_ACTION:
                return new ContactSuggestionsAdapter.ActionSuggestionViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.contact_us_suggestions_action,
                                parent, false));
            case VIEWTYPE_ORDERTRACKING:
                return new ContactSuggestionsAdapter.OrderTrackingViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.contact_us_suggestions_order_tracking,
                                parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContactSubjectTemplatesResponse.Suggestion.Mobile template = templates.get(position);
        switch (holder.getItemViewType()) {
            case VIEWTYPE_INFO:
                InfoSuggestionViewHolder infoSuggestionViewHolder = (InfoSuggestionViewHolder) holder;
                infoSuggestionViewHolder.setup(template, contactSuggestionsClickListener);
                break;
            case VIEWTYPE_ACTION:
                ActionSuggestionViewHolder actionSuggestionViewHolder = (ActionSuggestionViewHolder) holder;
                actionSuggestionViewHolder.setup(template, contactSuggestionsClickListener);
                actionSuggestionViewHolder.container.setVisibility(shouldHideActions ? View.GONE : View.VISIBLE);
                break;
            case VIEWTYPE_ORDERTRACKING:
                OrderTrackingViewHolder orderTrackingViewHolder = (OrderTrackingViewHolder) holder;
                orderTrackingViewHolder.setup(null, -1, delivery, orderTrackingClickListener);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return templates != null ? templates.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        ContactSubjectTemplatesResponse.Suggestion.Mobile template = templates.get(position);
        switch (template.getType().toLowerCase()) {
            case "action":
                return VIEWTYPE_ACTION;
            case "component":
                switch (template.getSubtype().toLowerCase()) {
                    case "ordertracking":
                        return VIEWTYPE_ORDERTRACKING;
                    default:
                        break;
                }
            default:
                return VIEWTYPE_INFO;
        }
    }

    public static class ActionSuggestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contact_suggestions_container)
        ViewGroup container;

        @BindView(R.id.contact_suggestions_description)
        TextView description;

        @BindView(R.id.contact_suggestions_button)
        Button button;

        public ActionSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setup(ContactSubjectTemplatesResponse.Suggestion.Mobile template,
                   ContactSuggestionsClickListener contactSuggestionsClickListener) {
            description.setText(template.getMessage());
            if (template.getLink() != null) {
                button.setVisibility(View.VISIBLE);
                button.setText(template.getLink().getTitle());
                button.setOnClickListener(v -> {
                    if (contactSuggestionsClickListener != null) {
                        contactSuggestionsClickListener.onClickAction(template.getLink());
                    }
                });
            } else {
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
            }
        }
    }

    public static class InfoSuggestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contact_suggestions_container)
        ViewGroup container;

        @BindView(R.id.contact_suggestions_description)
        TextView description;

        @BindView(R.id.contact_suggestions_button)
        Button button;

        public InfoSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setup(ContactSubjectTemplatesResponse.Suggestion.Mobile template,
                   ContactSuggestionsClickListener contactSuggestionsClickListener) {
            description.setText(template.getMessage());
            if (template.getLink() != null) {
                button.setVisibility(View.VISIBLE);
                button.setText(template.getLink().getTitle());
                button.setOnClickListener(v -> {
                    if (contactSuggestionsClickListener != null) {
                        contactSuggestionsClickListener.onClickInfo(template.getLink());
                    }
                });
            } else {
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
            }
        }
    }

    public static class OrderTrackingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.component_order_tracking)
        OrderTrackingView componentOrderTracking;

        public OrderTrackingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            componentOrderTracking.setBlinkingDisabledOnLastStep(true);
        }

        void setup(String invoiceId, int invoiceNumber, GetOrdersResponse.Order.Invoice.Delivery delivery, OrderTrackingClickListener onClickListener) {
            if (delivery == null) {
                componentOrderTracking.setVisibility(View.GONE);
            } else {
                componentOrderTracking.setVisibility(View.VISIBLE);
                componentOrderTracking.setup(invoiceId, invoiceNumber, delivery, null, onClickListener);
            }
        }
    }
}
