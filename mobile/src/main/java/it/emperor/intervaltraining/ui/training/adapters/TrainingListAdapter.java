package it.emperor.intervaltraining.ui.training.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.NumberTrainingsChangedEvent;
import it.emperor.intervaltraining.events.TrainingDeleteEvent;
import it.emperor.intervaltraining.events.TrainingEditEvent;
import it.emperor.intervaltraining.events.TrainingSelectedEvent;
import it.emperor.intervaltraining.models.Repetition;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.utility.Utils;

public class TrainingListAdapter extends RealmRecyclerViewAdapter<Training, TrainingListAdapter.TrainingViewHolder> {

    private Context mContext;
    private boolean mIsLollipop;

    public TrainingListAdapter(Context context, RealmResults<Training> trainings) {
        super(trainings, true);
        mContext = context;
        mIsLollipop = Utils.isLollipop();

        setOnChangeListener(new OnChangeListener() {
            @Override
            public void onSizeChanged(int size) {
                EventBus.getDefault().postSticky(new NumberTrainingsChangedEvent(size));
            }
        });
    }

    @Override
    public TrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrainingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_list, parent, false));
    }

    @Override
    public void onBindViewHolder(TrainingViewHolder holder, int position) {
        final Training training = getItem(position);
        if (training == null) {
            return;
        }
        int color = Utils.getColorFromType(mContext, training.getColor());

        holder.line.setBackgroundColor(color);

        holder.title.setTextColor(color);
        holder.title.setText(training.getTitle());

        if (mIsLollipop) {
            updateCompoundDrawable(holder.prepareText, color);
        } else {
            updateCompoundDrawable(holder.prepareIcon, color);
        }
        holder.prepare.setText(String.format(Locale.getDefault(), "%02d:%02d", training.getPrepareTime() / 60, training.getPrepareTime() % 60));

        if (mIsLollipop) {
            updateCompoundDrawable(holder.cooldownText, color);
        } else {
            updateCompoundDrawable(holder.cooldownIcon, color);
        }
        holder.cooldown.setText(String.format(Locale.getDefault(), "%02d:%02d", training.getCooldownTime() / 60, training.getCooldownTime() % 60));

        holder.edit.setTextColor(color);
        holder.delete.setTextColor(color);

        holder.repLayout.removeAllViews();
        for (Repetition repetition : training.getRepetitions().sort("order")) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_training_list_rep, holder.repLayout, false);
            TextView order = ButterKnife.findById(viewGroup, R.id.order);
            ImageView orderIcon = ButterKnife.findById(viewGroup, R.id.order_icon);
            TextView work = ButterKnife.findById(viewGroup, R.id.work);
            ImageView workIcon = ButterKnife.findById(viewGroup, R.id.work_icon);
            TextView rest = ButterKnife.findById(viewGroup, R.id.rest);
            ImageView restIcon = ButterKnife.findById(viewGroup, R.id.rest_icon);
            TextView count = ButterKnife.findById(viewGroup, R.id.count);
            ImageView countIcon = ButterKnife.findById(viewGroup, R.id.count_icon);

            if (mIsLollipop) {
                updateCompoundDrawable(order, color);
            } else {
                updateCompoundDrawable(orderIcon, color);
            }
            order.setText(String.format(mContext.getString(R.string.repetition_order_text), repetition.getOrder() + 1));

            if (mIsLollipop) {
                updateCompoundDrawable(work, color);
            } else {
                updateCompoundDrawable(workIcon, color);
            }
            work.setText(String.format(Locale.getDefault(), "%02d:%02d", repetition.getWorkTime() / 60, repetition.getWorkTime() % 60));

            if (mIsLollipop) {
                updateCompoundDrawable(rest, color);
            } else {
                updateCompoundDrawable(restIcon, color);
            }
            rest.setText(String.format(Locale.getDefault(), "%02d:%02d", repetition.getRestTime() / 60, repetition.getRestTime() % 60));

            if (mIsLollipop) {
                updateCompoundDrawable(count, color);
            } else {
                updateCompoundDrawable(countIcon, color);
            }
            count.setText(String.valueOf(repetition.getCount()));

            holder.repLayout.addView(viewGroup);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new TrainingEditEvent(training.getId(), training.getColor()));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new TrainingDeleteEvent(training.getId()));
            }
        });
    }

    private void updateCompoundDrawable(TextView textView, int color) {
        Drawable drawable = textView.getCompoundDrawables()[0];
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    private void updateCompoundDrawable(ImageView imageView, int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    class TrainingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.prepare_layout)
        View prepareLayout;
        @BindView(R.id.prepare_text)
        TextView prepareText;
        @BindView(R.id.prepare)
        TextView prepare;
        @Nullable
        @BindView(R.id.prepare_icon)
        ImageView prepareIcon;
        @BindView(R.id.cooldown_layout)
        View cooldownLayout;
        @BindView(R.id.cooldown_text)
        TextView cooldownText;
        @BindView(R.id.cooldown)
        TextView cooldown;
        @Nullable
        @BindView(R.id.cooldown_icon)
        ImageView cooldownIcon;
        @BindView(R.id.rep_layout)
        ViewGroup repLayout;
        @BindView(R.id.edit)
        Button edit;
        @BindView(R.id.delete)
        Button delete;
        @BindView(R.id.line)
        View line;

        TrainingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Training training = getItem(position);
            if (training != null) {
                EventBus.getDefault().post(new TrainingSelectedEvent(training.getId()));
            }
        }
    }
}
