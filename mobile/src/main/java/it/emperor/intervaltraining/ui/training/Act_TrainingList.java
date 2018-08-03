package it.emperor.intervaltraining.ui.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.NumberTrainingsChangedEvent;
import it.emperor.intervaltraining.events.TrainingDeleteEvent;
import it.emperor.intervaltraining.events.TrainingEditEvent;
import it.emperor.intervaltraining.events.TrainingSelectedEvent;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.training.adapters.TrainingListAdapter;
import it.emperor.intervaltraining.utility.Constants;
import it.emperor.intervaltraining.utility.Utils;

public class Act_TrainingList extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_items)
    ViewGroup mNoItemsLayout;
    @BindView(R.id.count_text)
    TextView mCountText;

    @BindView(R.id.ad_view_layout)
    LinearLayout mAdViewLayout;

    @OnClick(R.id.add_training)
    void addTrainingClicked() {
        startActivityForResult(new Intent(this, Act_TrainingCreate.class), CODE_CREATE);
    }

    // SUPPORT

    @Inject
    Realm realm;

    private boolean mShowAds;

    private static final String PARAM_SHOW_ADS = "paramShowAds";

    private static final int CODE_CREATE = 2001;

    // SYSTEM

    public static Intent newIntent(boolean showAds) {
        Intent intent = new Intent(App.context(), Act_TrainingList.class);
        intent.putExtra(PARAM_SHOW_ADS, showAds);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUseBus(true);

        if (mShowAds) {
            mAdViewLayout.removeAllViews();
            mAdViewLayout.addView(Utils.createAdView(this));
            mRecyclerView.setPadding(0, 0, 0, (int) Utils.dpToPx(56, this));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_training_list;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void loadParameters(Bundle extras) {
        mShowAds = extras.getBoolean(PARAM_SHOW_ADS, false);
    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {

    }

    @Override
    protected void saveInfos(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_training_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                addTrainingClicked();
                break;
//            case R.id.menu_sort:
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initialize() {
        RealmResults<Training> trainings = Training.getAllTraining(realm);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TrainingListAdapter(this, trainings));

        updateViewsVisibility(trainings.size());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CREATE:
                    Snackbar.make(getRootView(), "Training created", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Subscribe
    public void onEvent(TrainingEditEvent event) {
        startActivity(Act_TrainingCreate.newIntentEdit(event.getColor(), event.getId()));
    }

    @Subscribe
    public void onEvent(final TrainingDeleteEvent event) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.training_popup_delete_title))
                .content(getString(R.string.training_popup_delete_content))
                .positiveText(getString(R.string.general_delete))
                .negativeText(getString(R.string.general_annulla))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Training training = Training.getTrainingById(realm, event.getId());
                                    if (training.getRepetitions() != null) {
                                        training.getRepetitions().deleteAllFromRealm();
                                    }
                                    training.deleteFromRealm();
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Snackbar.make(getRootView(), getString(R.string.training_error_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .show();
    }

    @Subscribe(sticky = true)
    public void onEvent(NumberTrainingsChangedEvent event) {
        updateViewsVisibility(event.getNewSize());
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Subscribe
    public void onEvent(TrainingSelectedEvent event) {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_DATA, event.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    // FUNCTIONS

    private void updateViewsVisibility(int size) {
        mCountText.setText(String.format(getString(R.string.training_list_count_text), size));

        if (size == 0) {
            mNoItemsLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoItemsLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
