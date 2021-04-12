package au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.data.network.model.returns.step.Step;
import au.com.dealsdirect.ui.controller.returns.returnsteps.ReturnTrackingClickListener;
import au.com.dealsdirect.ui.controller.returns.returnsteps.ReturnTrackingView;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.component_return_tracking)
    ReturnTrackingView componentReturnTracking;

    @BindView(R.id.my_current_returns_header_text_view)
    TextView headerTextView;

    @BindView(R.id.current_return_images)
    RecyclerView currentReturnImagesRecyclerView;

    @BindView(R.id.my_current_return_item_option)
    ImageButton currentReturnItemOption;

    private ImagesRecyclerViewAdapter adapter;

    public CurrentReturnViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        adapter = new ImagesRecyclerViewAdapter();
        currentReturnImagesRecyclerView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        currentReturnImagesRecyclerView.setLayoutManager(layoutManager);
    }

    public ImageButton getCurrentReturnItemOption() {
        return currentReturnItemOption;
    }

    public void setup(CurrentReturn currentReturn, ReturnTrackingClickListener listener) {
        List<Step> steps = new ArrayList<>(currentReturn.getSteps());
        componentReturnTracking.setup(steps, currentReturn.getId(), listener);
        final String invoiceNumber = "Invoice " + currentReturn.getInvoiceNumber();
        final String ran = "RAN: " + currentReturn.getRan();
        String headerText = invoiceNumber;
        if (currentReturn.getRan() != null && !currentReturn.getRan().isEmpty()) {
            headerText += " | " + ran;
        }
        headerTextView.setText(headerText);
        adapter.items = currentReturn.getItems();
        adapter.notifyDataSetChanged();
    }

    private static class ImagesRecyclerViewAdapter extends RecyclerView.Adapter {
        List<CurrentReturn.Item> items = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ImageView imageView = new ImageView(parent.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof ImageView) {
                final ImageView imageView = (ImageView) holder.itemView;
                ImageUtils.loadImage(items.get(position).getImageUrl(), imageView);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageViewHolder(View itemView) {
                super(itemView);
                Resources res = itemView.getContext().getResources();
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        (int) res.getDimension(R.dimen.order_image_width),
                        ViewGroup.LayoutParams.MATCH_PARENT);
                itemView.setLayoutParams(lp);
            }
        }
    }
}
