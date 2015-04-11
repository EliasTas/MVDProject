package se.softhouse.mobile.android.mvdproject.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import se.softhouse.mobile.android.mvdproject.client.advertisermodule.ClientAdvertiser;
import se.softhouse.mobile.android.mvdproject.coremodule.UserStatus;

/**
 * Activity that fetches gps location and does advertising with ble.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";
    private ImageView bluetoothImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.clientactivity);
        super.onCreate(savedInstanceState);

        ((Spinner) findViewById(R.id.clientactivity_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showBluetoothImage(false);
                ClientAdvertiser.stopAdvertisement(ClientActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.clientactivity_button_advertising_on) {
                    showBluetoothImage(true);
                    startClientAdvertiserModule(UserStatus.ON);
                } else if (v.getId() == R.id.clientactivity_button_advertising_off) {
                    showBluetoothImage(true);
                    startClientAdvertiserModule(UserStatus.OFF);
                } else if (v.getId() == R.id.clientactivity_button_stop_advertising) {
                    showBluetoothImage(false);
                    ClientAdvertiser.stopAdvertisement(ClientActivity.this);
                }
            }
        };

        findViewById(R.id.clientactivity_button_advertising_on).setOnClickListener(buttonListener);
        findViewById(R.id.clientactivity_button_advertising_off).setOnClickListener(buttonListener);
        findViewById(R.id.clientactivity_button_stop_advertising).setOnClickListener(buttonListener);

        bluetoothImage = (ImageView) findViewById(R.id.clientactivity_bluetooth_image);
    }

    private void startClientAdvertiserModule(String userStatus) {
        String userId = getUserId();
        if (!"00".equals(userId)) {
            ClientAdvertiser.startAdvertisement(ClientActivity.this, userId, userStatus);
        } else {
            showBluetoothImage(false);
            Toast.makeText(this, "Please choose a team", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserId() {
        return (String) ((Spinner) findViewById(R.id.clientactivity_spinner)).getSelectedItem();
    }

    private void showBluetoothImage(boolean show) {
        bluetoothImage.setVisibility(show ? ImageView.VISIBLE : ImageView.INVISIBLE);
    }
}
