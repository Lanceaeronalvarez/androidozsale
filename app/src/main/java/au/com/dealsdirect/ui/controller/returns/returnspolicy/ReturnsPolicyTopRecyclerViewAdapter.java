package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReturnsPolicyTopRecyclerViewAdapter extends RecyclerView.Adapter<ReturnsPolicyTopRecyclerViewAdapter.ReturnPolicyStepViewHolder> {

    private List<String> titles;
    private List<String> descriptions;
    private List<Integer> drawableIds;

    public ReturnsPolicyTopRecyclerViewAdapter(List<String> titles, List<String> descriptions, List<Integer> drawableIds) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.drawableIds = drawableIds;
    }

    @NonNull
    @Override
    public ReturnPolicyStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_returns_policy_step_item, parent, false);
        return new ReturnPolicyStepViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnPolicyStepViewHolder holder, int position) {
        holder.imageView.setImageDrawable(holder.imageView.getContext().getDrawable(drawableIds.get(position)));
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position) + "\n");
        //The textview seems to truncate the text. The linebreak added at the end of the string fixes that.
    }

    @Override
    public int getItemCount() {
        return Math.min(drawableIds.size(), Math.min(titles.size(), descriptions.size()));
    }

    static class ReturnPolicyStepViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.step_image)
        ImageView imageView;

        @BindView(R.id.step_title)
        TextView title;

        @BindView(R.id.step_description)
        TextView description;

        ReturnPolicyStepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
