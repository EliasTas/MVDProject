package se.softhouse.mobile.android.mvdproject.publishmqttapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import se.softhouse.mobile.android.mvdproject.publishmqttapp.datareceivermodule.DataReceiver;
import se.softhouse.mobile.android.mvdproject.publishmqttapp.preparemessagemodule.PrepareMsg;
import se.softhouse.mobile.android.mvdproject.publishmqttapp.publishmqttmodule.PublishMQTT;

/**
 * Controls three modules: DataReceiver, PrepareMsgModule and PublishMQTTModule.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class PublishAppController extends Service {

    private PublishAppBroadcastReceiver mPublishAppBroadcastReceiver;
    private String TAG = PublishAppController.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mPublishAppBroadcastReceiver != null) {
            try {
                unregisterReceiver(mPublishAppBroadcastReceiver);
            } catch (IllegalArgumentException ignored) {
            }
        }
        registerReceiver();
        startDataReceiverService();
        return START_STICKY;
    }

    /**
     * Receives from remote apps.
     */
    public class PublishAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionKey = intent.getAction();
            Log.d(TAG, "actionkey: " + actionKey);
            Bundle extra = intent.getExtras();
            if (actionKey.equals(DataReceiver.DATARECEIVER_ACTION_TAG)) {
                HashMap map = (HashMap) extra.get(DataReceiver.DATARECEIVER_ACTION_KEY_TAG);
                final JSONObject data = startPrepareMsgModule(map);
                startPublishMQTTModule(data);
            } else if (actionKey.equals(PublishMQTT.MVDMQTTCLIENT_ACTION_TAG)) {
                startDataReceiverService();
            }
        }
    }

    private void startDataReceiverService() {
        Log.d(TAG, "startDataReceiverService");
        DataReceiver.receive(this);
    }

    /**
     * Adjusting the data that fits the database-server.
     */
    private JSONObject startPrepareMsgModule(HashMap map) {
        Log.d(TAG, "startPrepareMsgModule");
        return PrepareMsg.prepareMsg(map);
    }

    /**
     * Send data to server.
     */
    private void startPublishMQTTModule(JSONObject data) {
        Log.d(TAG, "startPublishMQTTModule");
        PublishMQTT.sendInformation(this, data);
    }

    private void registerReceiver() {
        mPublishAppBroadcastReceiver = new PublishAppBroadcastReceiver();
        final IntentFilter actions = new IntentFilter();
        actions.addAction(PublishMQTT.MVDMQTTCLIENT_ACTION_TAG);
        actions.addAction(DataReceiver.DATARECEIVER_ACTION_TAG);
        registerReceiver(mPublishAppBroadcastReceiver, actions);
    }

}
