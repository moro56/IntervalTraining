package it.emperor.intervaltraining.ui.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.AddRepetitionClickedEvent;
import it.emperor.intervaltraining.events.EditRepetitionClickedEvent;
import it.emperor.intervaltraining.models.Repetition;

public class MultipleRepetitionLayout extends DragLinearLayout implements DragLinearLayout.OnViewSwapListener {

    private List<Repetition> mRepetitionList;
    private int mPosition;

    public MultipleRepetitionLayout(Context context) {
        super(context);
    }

    public MultipleRepetitionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        setOnViewSwapListener(this);
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null) {
            layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        }

        mRepetitionList = new ArrayList<>();
        mPosition = -1;

        Button add = ButterKnife.findById(this, R.id.add);
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new AddRepetitionClickedEvent());
            }
        });
    }

    private View loadRow(final Repetition repetition, final int position) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.item_rep_multiple_row, this, false);
        return loadRow(repetition, position, viewGroup);
    }

    private View loadRow(final Repetition repetition, final int position, ViewGroup viewGroup) {
        TextView title = ButterKnife.findById(viewGroup, R.id.title);
        TextView work = ButterKnife.findById(viewGroup, R.id.work);
        TextView rest = ButterKnife.findById(viewGroup, R.id.rest);
        TextView count = ButterKnife.findById(viewGroup, R.id.count);
        View layout = ButterKnife.findById(viewGroup, R.id.layout);
        final View buttonsLayout = ButterKnife.findById(viewGroup, R.id.buttons_layout);
        TextView edit = ButterKnife.findById(viewGroup, R.id.edit);
        TextView delete = ButterKnife.findById(viewGroup, R.id.delete);

        repetition.setOrder(position);
        title.setText(String.format(getContext().getString(R.string.repetition_order_text), position + 1));

        LocalTime workTime = new LocalTime(0, 0).plusSeconds(repetition.getWorkTime());
        work.setText(DateTimeFormat.forPattern("mm:ss").print(workTime));

        LocalTime restTime = new LocalTime(0, 0).plusSeconds(repetition.getRestTime());
        rest.setText(DateTimeFormat.forPattern("mm:ss").print(restTime));

        count.setText(String.valueOf(repetition.getCount()));

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsLayout.setVisibility(buttonsLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
            }
        });
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition = position;
                EventBus.getDefault().post(new EditRepetitionClickedEvent(repetition));
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(getContext().getString(R.string.training_delete_repetition_title))
                        .content(getContext().getString(R.string.training_delete_repetition_content))
                        .positiveText(getContext().getString(R.string.general_delete))
                        .negativeText(getContext().getString(R.string.general_annulla))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                deleteRepetition(repetition);
                            }
                        })
                        .show();
            }
        });

        return viewGroup;
    }

    public void initRepetitions(List<Repetition> repetitions) {
        for (Repetition repetition : repetitions) {
            addRepetition(repetition);
        }
    }

    public void addRepetition(Repetition repetition) {
        View view = loadRow(repetition, mRepetitionList.size());
        addDragView(view, ButterKnife.findById(view, R.id.drag), mRepetitionList.size());

        mRepetitionList.add(repetition);
    }

    public void updateRepetition(Repetition repetition) {
        if (mPosition == -1)
            return;

        mRepetitionList.set(mPosition, repetition);
        loadRow(repetition, mPosition, (ViewGroup) getChildAt(mPosition));
    }

    private void deleteRepetition(Repetition repetition) {
        int pos = mRepetitionList.indexOf(repetition);
        if (pos != -1) {
            removeDragView(getChildAt(pos));

            mRepetitionList.remove(pos);
        }
    }

    @Override
    public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
        Repetition repetition = mRepetitionList.get(firstPosition);
        mRepetitionList.set(firstPosition, mRepetitionList.get(secondPosition));
        mRepetitionList.set(secondPosition, repetition);
    }

    @Override
    public void onSwapEnd() {
        for (int i = 0; i < getChildCount() - 1; i++) {
            loadRow(mRepetitionList.get(i), i, (ViewGroup) getChildAt(i));
        }
    }

    public List<Repetition> getRepetitions() {
        return mRepetitionList;
    }
}
