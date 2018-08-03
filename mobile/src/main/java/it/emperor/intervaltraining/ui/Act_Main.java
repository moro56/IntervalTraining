package it.emperor.intervaltraining.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.RemoveAdsPurchasedEvent;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.providers.SoundProvider;
import it.emperor.intervaltraining.services.KillNotificationsService;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.settings.Act_Settings;
import it.emperor.intervaltraining.ui.training.Act_RunTraining;
import it.emperor.intervaltraining.ui.training.Act_TrainingCreate;
import it.emperor.intervaltraining.ui.training.Act_TrainingList;
import it.emperor.intervaltraining.ui.views.MainTrainingView;
import it.emperor.intervaltraining.utility.Constants;
import it.emperor.intervaltraining.utility.Utils;

public class Act_Main extends BaseActivity implements PurchasesUpdatedListener {

    @BindView(R.id.training_layout)
    MainTrainingView mTrainingLayout;

    @BindView(R.id.ad_view_layout)
    LinearLayout mAdViewLayout;

    @OnClick(R.id.fab)
    void fabClicked() {
        Training training = mTrainingLayout.getTraining();

        startActivity(Act_RunTraining.newIntent(training, mShowAds));
    }

    // SUPPORT
    @Inject
    Realm realm;
    @Inject
    SoundProvider soundProvider;

    private ServiceConnection mServiceConnection;
    private BillingClient mBillingClient;
    private boolean mShowAds;

    private static final int CODE_CREATE = 2001;
    private static final int CODE_LIST = 2002;

    // SYSTEM

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setIgnoreToolbarBack(true);
        super.onCreate(savedInstanceState);
        setUseBus(true);

        mBillingClient = new BillingClient.Builder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int resultCode) {
                if (resultCode == BillingClient.BillingResponse.OK) {
                    Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    List<Purchase> purchases = purchasesResult.getPurchasesList();
                    boolean found = false;
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(getString(R.string.app_remove_ads))) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        mShowAds = true;
                        mAdViewLayout.removeAllViews();
                        mAdViewLayout.addView(Utils.createAdView(Act_Main.this));
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    protected void initVariables() {
        mShowAds = false;
    }

    @Override
    protected void loadParameters(Bundle extras) {

    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {

    }

    @Override
    protected void saveInfos(Bundle outState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivityForResult(new Intent(this, Act_TrainingCreate.class), CODE_CREATE);
                break;
            case R.id.menu_list:
                startActivityForResult(Act_TrainingList.newIntent(mShowAds), CODE_LIST);
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, Act_Settings.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void initialize() {
        bindService(new Intent(this, KillNotificationsService.class), mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                ((KillNotificationsService.KillBinder) binder).service.startService(new Intent(
                        Act_Main.this, KillNotificationsService.class));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CREATE:
                    Snackbar.make(getRootView(), getString(R.string.training_created_title), Snackbar.LENGTH_SHORT).show();
                    break;
                case CODE_LIST:
                    String id = data.getStringExtra(Constants.INTENT_DATA);
                    if (id != null) {
                        Training training = Training.getTrainingById(realm, id);
                        if (training != null) {
                            training = realm.copyFromRealm(training);
                            mTrainingLayout.showTraining(training);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {

    }

    @Subscribe(sticky = true)
    public void onEvent(RemoveAdsPurchasedEvent event) {
        mShowAds = false;
        EventBus.getDefault().removeStickyEvent(event);
        mAdViewLayout.removeAllViews();
    }
}
