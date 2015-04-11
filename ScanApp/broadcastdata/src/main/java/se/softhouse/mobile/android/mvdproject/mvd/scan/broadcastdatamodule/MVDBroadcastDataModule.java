package se.softhouse.mobile.android.mvdproject.mvd.scan.broadcastdatamodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Publishes data to remote apps.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class MVDBroadcastDataModule extends Service {

    private String TAG = MVDBroadcastDataModule.class.getSimpleName();
    private static final String MVDBROADCASTDATA_DATA_TO_SEND = "MVD_SCAN_MVDBROADCASTDATA_DATA_TO_SEND";

    /**
     * Publishes data to remote apps.
     *
     * @param context to start the service with.
     * @param data    information to send.
     */
    public static void sendData(Context context, HashMap<String, String> data) {
        final Intent intent = new Intent(context, MVDBroadcastDataModule.class);
        intent.putExtra(MVDBROADCASTDATA_DATA_TO_SEND, data);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final HashMap data = (HashMap) intent.getExtras().get(MVDBROADCASTDATA_DATA_TO_SEND);
        sendToRemoteApp(data);
        closeService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendToRemoteApp(HashMap msg) {
        Intent intent = new Intent(getString(R.string.mvd_app_intent_action_key));
        intent.putExtra(getString(R.string.mvd_app_intent_action_extra_key), msg);
        sendBroadcast(intent);
        Log.d(TAG, "Sending to remote app");
        Toast.makeText(this, "Sending to remote app", Toast.LENGTH_SHORT).show();
    }

    private void closeService() {
        stopSelf();
    }
}
