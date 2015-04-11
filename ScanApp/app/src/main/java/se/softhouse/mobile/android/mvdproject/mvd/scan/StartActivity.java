package se.softhouse.mobile.android.mvdproject.mvd.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Start scan-controller.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class StartActivity extends Activity {

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ScanAppController.class);
        startService(intent);
        // Toast.makeText(getBaseContext(), "Start " + ScanAppController.class.getSimpleName(), Toast.LENGTH_LONG).show();
        finish();
    }
}
