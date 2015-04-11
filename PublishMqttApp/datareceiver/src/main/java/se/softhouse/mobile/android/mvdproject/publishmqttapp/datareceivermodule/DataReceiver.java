package se.softhouse.mobile.android.mvdproject.publishmqttapp.datareceivermodule;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

/**
 * Fetches the data coming from ScanApp.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class DataReceiver extends Service {

    // Public constants
    public static final String DATARECEIVER_ACTION_TAG = "MVD_SCAN_DATARECEIVER_ACTION_TAG";
    public static final String DATARECEIVER_ACTION_KEY_TAG = "MVD_SCAN_DATARECEIVER_ACTION_KEY_TAG";

    private static final String TAG = DataReceiver.class.getSimpleName();

    ScanAppBroadcastReceiver mScanAppBroadcastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Fetches the data from ScanApp.
     * Sends the data via a broadcast with action-tag: {@link #DATARECEIVER_ACTION_TAG} and the data with actionkey-tag: {@link #DATARECEIVER_ACTION_KEY_TAG}.
     *
     * @param context to start the service with.
     */
    public static void receive(Context context) {
        final Intent intent = new Intent(context, DataReceiver.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mScanAppBroadcastReceiver = new ScanAppBroadcastReceiver();
        final IntentFilter actions = new IntentFilter();
        actions.addAction(getString(R.string.mvd_app_intent_action_key));
        registerReceiver(mScanAppBroadcastReceiver, actions);
    }

    private class ScanAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionKey = intent.getAction();
            Bundle extra = intent.getExtras();
            if (getString(R.string.mvd_app_intent_action_key).equals(actionKey)) {
                HashMap map = (HashMap) extra.get(getString(R.string.mvd_app_intent_action_extra_key));
                unregisterReceiver(this);
                sendToBroadcast(map);
            }
        }
    }

    private void sendToBroadcast(HashMap map) {
        Log.d(TAG, "sendtobroadcast");
        Intent i = new Intent(DATARECEIVER_ACTION_TAG);
        i.putExtra(DATARECEIVER_ACTION_KEY_TAG, map);
        sendBroadcast(i);
        closeService();
    }

    private void closeService() {
        stopSelf();
    }
}
