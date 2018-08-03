package it.emperor.intervaltraining.ui.credits.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.CreditsSelectedEvent;
import it.emperor.intervaltraining.ui.credits.models.Credits;

public class CreditsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Credits> mItems;

    public CreditsAdapter(List<Credits> items) {
        this.mItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CreditsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credits, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CreditsViewHolder realHolder = (CreditsViewHolder) holder;
        Credits credits = mItems.get(position);

        realHolder.title.setText(credits.getTitle());
        realHolder.name.setText(credits.getName());
        realHolder.description.setText(credits.getDescription());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class CreditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;

        CreditsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Credits credits = mItems.get(position);
            EventBus.getDefault().post(new CreditsSelectedEvent(credits.getUrl()));
        }
    }
}
