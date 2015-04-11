package se.softhouse.mobile.android.mvdproject.publishmqttapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Start scan service.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class StartActivity extends Activity {

    private String TAG = StartActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PublishAppController.class);
        startService(intent);
        // Toast.makeText(getBaseContext(), "Start " + PublishAppController.class.getSimpleName(), Toast.LENGTH_LONG).show();
        finish();
    }
}
