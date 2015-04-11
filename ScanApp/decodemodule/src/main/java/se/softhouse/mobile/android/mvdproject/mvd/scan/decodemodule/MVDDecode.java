package se.softhouse.mobile.android.mvdproject.mvd.scan.decodemodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Decode scan-data.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 * TODO: Doesn't decrypt anything yet.
 */
public class MVDDecode extends Service {

    public static final String MVDDECODE_ACTION_TAG = "MVD_SCAN_MVDDECODER_ACTION_TAG";
    public static final String MVDDECODE_ACTION_KEY_TAG_USER = "MVD_SCAN_MVDDECODER_ACTION_KEY_TAG_USER_ID";
    public static final String MVDDECODE_ACTION_KEY_TAG_USER_STATUS = "MVD_SCAN_MVDDECODER_ACTION_KEY_TAG_USER_STATUS";

    private String TAG = MVDDecode.class.getSimpleName();
    private static final String MVDDECODE_ENCRYPT_USER_ID = "MVD_SCAN_MVDDECODER_ENCRYPTED_USERID";
    private static final String MVDDECODE_ENCRYPT_USER_STATUS = "MVD_SCAN_MVDDECODER_ENCRYPTED_USER_STATUS";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Sends the decoded values via a broadcast with action-tag: {@link #MVDDECODE_ACTION_TAG}. Actionkey-tag for decoded user id is: {@link #MVDDECODE_ACTION_KEY_TAG_USER}. Actionkey-tag for decoded user status id is: {@link #MVDDECODE_ACTION_KEY_TAG_USER_STATUS}.
     *
     * @param context             to start the service with.
     * @param encryptedUserId     raw scan-data.
     * @param encryptedUserStatus raw scan-data.
     */
    public static void requestDecoding(Context context, String encryptedUserId, String encryptedUserStatus) {
        final Intent intent = new Intent(context, MVDDecode.class);
        intent.putExtra(MVDDECODE_ENCRYPT_USER_ID, encryptedUserId);
        intent.putExtra(MVDDECODE_ENCRYPT_USER_STATUS, encryptedUserStatus);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String encryptedUserId = intent.getExtras().getString(MVDDECODE_ENCRYPT_USER_ID);
        final String encryptedUserStatus = intent.getExtras().getString(MVDDECODE_ENCRYPT_USER_STATUS);
        sendToBroadcast(encryptedUserId, encryptedUserStatus);

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendToBroadcast(String userId, String userStatus) {
        // broadcast receiver
        Intent i = new Intent(MVDDECODE_ACTION_TAG);
        i.putExtra(MVDDECODE_ACTION_KEY_TAG_USER, userId);
        i.putExtra(MVDDECODE_ACTION_KEY_TAG_USER_STATUS, userStatus);
        sendBroadcast(i);
        closeService();
    }

    private void closeService() {
        stopSelf();
    }
}
