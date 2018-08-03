package it.emperor.intervaltraining.ui.credits;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.CreditsSelectedEvent;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.credits.adapters.CreditsAdapter;
import it.emperor.intervaltraining.ui.credits.models.Credits;
import it.emperor.intervaltraining.ui.views.DividerDecorator;

public class Act_Credits extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    // SYSTEM

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUseBus(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_credits;
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
        String json = getString(R.string.credits_json);

        List<Credits> creditsList = new Gson().fromJson(json, new TypeToken<ArrayList<Credits>>() {
        }.getType());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new CreditsAdapter(creditsList));
        mRecyclerView.addItemDecoration(new DividerDecorator(ContextCompat.getColor(this, R.color.colorGrayMedium)));
    }

    @Subscribe
    public void onEvent(CreditsSelectedEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl()));
        startActivity(intent);
    }
}
