package it.emperor.intervaltraining.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.RemoveAdsPurchasedEvent;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.credits.Act_Credits;
import it.emperor.intervaltraining.utility.Constants;
import it.emperor.intervaltraining.utility.Prefs;

public class Act_Settings extends BaseActivity implements PurchasesUpdatedListener {

    @BindView(R.id.switch_screen_on)
    SwitchCompat mScreenOnSwitch;
    @BindView(R.id.switch_sound_on)
    SwitchCompat mSoundOnSwitch;
    @BindView(R.id.switch_vibration_on)
    SwitchCompat mVibrationsOnSwitch;

    @OnClick(R.id.screen_on_layout)
    void screenOnLayoutClicked() {
        mScreenOnSwitch.performClick();
    }

    @OnClick(R.id.sound_on_layout)
    void soundOnLayoutClicked() {
        mSoundOnSwitch.performClick();
    }

    @OnClick(R.id.vibration_on_layout)
    void vibrationOnLayoutClicked() {
        mVibrationsOnSwitch.performClick();
    }

    @OnClick(R.id.backup_layout)
    void backupLayoutClicked() {
        startActivity(new Intent(this, Act_Backup.class));
    }

    @OnClick(R.id.rate_layout)
    void rateLayoutClicked() {
        String appPackage = getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
        startActivity(intent);
    }

    @OnClick(R.id.share_layout)
    void shareLayoutClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));
        startActivity(intent);
    }

    @OnClick(R.id.contact_us_layout)
    void contactUsClicked() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "emperorgames.info@gmail.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interval Training");
        startActivity(Intent.createChooser(intent, getString(R.string.general_send_mail)));
    }

    @OnClick(R.id.credits_layout)
    void creditsLayoutClicked() {
        startActivity(new Intent(this, Act_Credits.class));
    }

    @OnClick(R.id.remove_ads_layout)
    void removeAdsLayout() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            BillingFlowParams.Builder builder = new BillingFlowParams.Builder().setSku(getString(R.string.app_remove_ads)).setType(BillingClient.SkuType.INAPP);
            int response = mBillingClient.launchBillingFlow(this, builder.build());
            switch (response) {
                case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
                    EventBus.getDefault().postSticky(new RemoveAdsPurchasedEvent());

                    new MaterialDialog.Builder(this)
                            .content(getString(R.string.purchase_already_owned))
                            .positiveText(getString(R.string.general_ok))
                            .cancelable(false)
                            .show();
                    break;
            }
        }
    }

    // SUPPORT

    private BillingClient mBillingClient;

    // SYSTEM

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBillingClient = new BillingClient.Builder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int resultCode) {
                if (resultCode == BillingClient.BillingResponse.OK) {
                    Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    List<Purchase> purchases = purchasesResult.getPurchasesList();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_settings;
    }

    @Override
    protected void initVariables() {

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
    protected void initialize() {
        mScreenOnSwitch.setChecked(Prefs.getBoolean(Constants.PREF_SCREEN_ON, true));
        mSoundOnSwitch.setChecked(Prefs.getBoolean(Constants.PREF_SOUND_ON, true));
        mVibrationsOnSwitch.setChecked(Prefs.getBoolean(Constants.PREF_VIBRATION_ON, true));

        mScreenOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean(Constants.PREF_SCREEN_ON, isChecked);
            }
        });
        mSoundOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean(Constants.PREF_SOUND_ON, isChecked);
            }
        });
        mVibrationsOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean(Constants.PREF_VIBRATION_ON, isChecked);
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if ((responseCode == BillingClient.BillingResponse.OK || responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED)
                && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals(getString(R.string.app_remove_ads))) {
                    EventBus.getDefault().postSticky(new RemoveAdsPurchasedEvent());
                }
            }
        } else {
            String message;
            switch (responseCode) {
                case BillingClient.BillingResponse.USER_CANCELED:
                    message = getString(R.string.purchase_user_canceled);
                    break;
                default:
                    message = getString(R.string.purchase_error);
                    break;
            }
            new MaterialDialog.Builder(this)
                    .content(message)
                    .positiveText(getString(R.string.general_ok))
                    .cancelable(false)
                    .show();
        }
    }
}
