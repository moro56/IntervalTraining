package it.emperor.intervaltraining.ui.training.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.ColorChangedEvent;
import it.emperor.intervaltraining.utility.Constants;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private int mSelected = 0;

    public ColorAdapter(int colorType) {
        selectColor(colorType);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false));
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_red_selected : R.drawable.circle_red);
                break;
            case 1:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_orange_selected : R.drawable.circle_orange);
                break;
            case 2:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_purple_selected : R.drawable.circle_purple);
                break;
            case 3:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_green_selected : R.drawable.circle_green);
                break;
            case 4:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_blue_selected : R.drawable.circle_blue);
                break;
            case 5:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_cyan_selected : R.drawable.circle_cyan);
                break;
            case 6:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_brown_selected : R.drawable.circle_brown);
                break;
            case 7:
                holder.color.setBackgroundResource(mSelected == position ? R.drawable.circle_gray_selected : R.drawable.circle_gray);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    private void selectColor(int colorType) {
        switch (colorType) {
            case Constants.COLOR_RED:
                mSelected = 0;
                break;
            case Constants.COLOR_ORANGE:
                mSelected = 1;
                break;
            case Constants.COLOR_PURPLE:
                mSelected = 2;
                break;
            case Constants.COLOR_GREEN:
                mSelected = 3;
                break;
            case Constants.COLOR_BLUE:
                mSelected = 4;
                break;
            case Constants.COLOR_CYAN:
                mSelected = 5;
                break;
            case Constants.COLOR_BROWN:
                mSelected = 6;
                break;
            case Constants.COLOR_GRAY:
                mSelected = 7;
                break;
        }
        notifyDataSetChanged();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.color)
        View color;

        ColorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelected = getLayoutPosition();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            EventBus.getDefault().post(new ColorChangedEvent(mSelected, location[0] + view.getWidth() / 2));
            notifyDataSetChanged();
        }
    }
}
