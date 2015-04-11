package se.softhouse.mobile.android.mvdproject.mvd.scan.scanmodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import se.softhouse.mobile.android.mvdproject.coremodule.ByteConverter;
import se.softhouse.mobile.android.mvdproject.coremodule.UserStatus;
import se.softhouse.mobile.android.mvdproject.coremodule.UuidConstants;

/**
 * Scan after advertising devices and fetch data.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class MVDScanner extends Service implements BLEScannerInterface {

    public static final String MVDSCANNER_ACTION_TAG = "MVD_SCAN_MVDSCANNER_ACTION_TAG";
    public static final String MVDSCANNER_ACTION_KEY_TAG_USER_ID = "MVDSCANNER_ACTION_KEY_TAG_USER_ID";
    public static final String MVDSCANNER_ACTION_KEY_TAG_USER_STATUS = "MVDSCANNER_ACTION_KEY_TAG_USER_STATUS";

    private static final String TAG = MVDScanner.class.getSimpleName();

    private boolean mScanning;
    private BLEScanner mBLEScanner;

    /**
     * Start scanning after advertising devices with a specific uuid. See R.string.service_uuid.
     * Sends advertising-data via a broadcast with action-tag: {@link #MVDSCANNER_ACTION_TAG}. Actionkey-tag for encoded user id is: {@link #MVDSCANNER_ACTION_KEY_TAG_USER_ID}. Actionkey-tag for encoded user status id is: {@link #MVDSCANNER_ACTION_KEY_TAG_USER_STATUS}.
     *
     * @param context to start the service with.
     */
    public static void requestScan(Context context) {
        context.startService(new Intent(context, MVDScanner.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mScanning) {
            mBLEScanner = new BLEScanner(this, UuidConstants.UUID);
            Log.d(TAG, "start scanning");
            mScanning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBLEScanner.startScan();
                }
            }, 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // Values from byte 4-8 (or 9)
    private boolean readAdvertiseData(byte[] serviceData) {
        if (serviceData.length < 9) {
            return false;
        }

        byte[] valuesByteArray = new byte[5];
        // 4 to 8
        System.arraycopy(serviceData, 4, valuesByteArray, 0, 5);
        String[] values = ByteConverter.convertToString(valuesByteArray).split(":");

        if(values.length < 2) {
            return false;
        }

        String userId = values[0];
        String userStatus = UserStatus.ON.equals(values[1]) ? UserStatus.ON : UserStatus.OFF;

        Log.d(TAG, "userid: " + userId + " userStatus: " + userStatus);

        if(!getString(R.string.valid_user_id).equals(userId)){
            Log.w(TAG, "Ignoring user: " + userId);
            return false;
        }

        sendToBroadcast(userId, userStatus);
        return true;
    }

    private void sendToBroadcast(String userId, String userStatus) {
        // broadcast receiver
        closeService();
        Intent i = new Intent(MVDSCANNER_ACTION_TAG);
        i.putExtra(MVDSCANNER_ACTION_KEY_TAG_USER_ID, userId);
        i.putExtra(MVDSCANNER_ACTION_KEY_TAG_USER_STATUS, userStatus);
        sendBroadcast(i);
    }

    private void closeService() {
        mScanning = false;
        stopSelf();
    }


    /* BLEScannerInterface */
    @Override
    public void onScanFailed(int errorCode) {
        if (errorCode == BLEScanner.START_SCAN_UNSUCCESSFUL) {
            Log.e(TAG, "onScanFailed: " + "Start scan failed");
        } else {
            Log.e(TAG, "onScanFailed: " + errorCode);
        }
        Log.w(TAG, "is BT on?");
        closeService();
    }

    @Override
    public void onScanResult(byte[] serviceData) {
        if (mScanning) {
            if (readAdvertiseData(serviceData)) {
                mBLEScanner.stopScan();
                closeService();
            }
            // else keep on scanning
        }
    }

}
