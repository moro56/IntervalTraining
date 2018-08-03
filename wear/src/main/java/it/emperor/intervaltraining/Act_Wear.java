package it.emperor.intervaltraining;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Act_Wear extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wear);
        mTextView = (TextView) findViewById(R.id.text);
    }
}
