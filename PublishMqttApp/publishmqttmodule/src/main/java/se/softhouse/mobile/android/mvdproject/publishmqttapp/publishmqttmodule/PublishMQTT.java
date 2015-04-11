package se.softhouse.mobile.android.mvdproject.publishmqttapp.publishmqttmodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sends data to mqtt-broker.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class PublishMQTT extends Service {

    public static final String MVDMQTTCLIENT_ACTION_TAG = "MVDMQTTCLIENT_ACTION_TAG";

    private static final String INTENT_EXTRA_DATA = "MVD_SCAN_PUBLISHMQTT_INTENT_EXTRA";
    private String TAG = getClass().getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Send information to mqtt-server.
     *
     * @param context           to start the service with.
     * @param informationToSend a jsonobject.
     */
    public static void sendInformation(Context context, JSONObject informationToSend) {
        final Intent intent = new Intent(context, PublishMQTT.class);
        intent.putExtra(INTENT_EXTRA_DATA, informationToSend.toString());
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String jsonString = intent.getExtras().getString(INTENT_EXTRA_DATA);
        try {
            final JSONObject jsonData = new JSONObject(jsonString);
            sendData(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not convert string to jsondata");
            closeService();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* Send data to mqtt. */
    private void sendData(JSONObject jsonData) {
        Log.d(TAG, "senddata");
        MVDMqttClient mvdMqttClient = new MVDMqttClient();
        mvdMqttClient.sendMessage(jsonData);
        Log.d(TAG, "send data to mqtt. JSONObject: " + jsonData);
        sendToBroadcast();
        closeService();
    }

    private void closeService() {
        stopSelf();
    }


    private void sendToBroadcast() {
        // broadcast receiver
        Log.d(TAG, "sendtobroadcast");
        Intent i = new Intent(MVDMQTTCLIENT_ACTION_TAG);
        sendBroadcast(i);
        closeService();
    }
}
