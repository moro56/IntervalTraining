package it.emperor.intervaltraining.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.utility.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected Bundle mSavedInstanceState;

    private boolean mIgnoreToolbarBack = false;
    private boolean mSavedInfoPresent = false;
    private boolean mUseBus = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        App.feather().injectFields(this);
        ButterKnife.bind(this);

        initVariables();

        if (getIntent().getExtras() != null) {
            loadParameters(getIntent().getExtras());
        }

        mSavedInstanceState = savedInstanceState;
        mSavedInfoPresent = false;
        if (savedInstanceState != null) {
            mSavedInfoPresent = true;
            loadInfos(savedInstanceState);
        }

        boolean hasToolbar = true;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null)
            hasToolbar = false;

        if (hasToolbar) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
            if (!mIgnoreToolbarBack) {
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInfos(outState);
    }

    protected abstract int getLayoutId();

    protected abstract void initVariables();

    protected abstract void loadParameters(Bundle extras);

    protected abstract void loadInfos(Bundle savedInstanceState);

    protected abstract void saveInfos(Bundle outState);

    protected abstract void initialize();

    @Override
    protected void onResume() {
        super.onResume();
        if (mUseBus && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mUseBus) {
            EventBus.getDefault().unregister(this);
        }
        Utils.hideKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUseBus) {
            EventBus.getDefault().unregister(this);
        }
        mSavedInstanceState = null;
    }

    protected View getRootView() {
        if (findViewById(R.id.coordinator) != null)
            return findViewById(R.id.coordinator);
        return findViewById(android.R.id.content);
    }

    protected void setUseBus(boolean useBus) {
        this.mUseBus = useBus;
    }

    /**
     * Da richiamare prima del super
     */
    protected void setIgnoreToolbarBack(boolean ignoreToolbarBack) {
        this.mIgnoreToolbarBack = ignoreToolbarBack;
    }

    protected boolean isSavedInfoPresent() {
        return mSavedInfoPresent;
    }
}
