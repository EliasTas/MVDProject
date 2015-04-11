package se.softhouse.mobile.android.mvdproject.client.advertisermodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import se.softhouse.mobile.android.mvdproject.coremodule.UuidConstants;

/**
 * Starts advertising.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class ClientAdvertiser extends Service implements BLEAdvertiserInterface {

    public static final String ADVERTISER_ACTION_TAG = "ADVERTISER_ACTION_TAG";
    public static final String ADVERTISER_ACTION_KEY_TAG = "ADVERTISER_ACTION_KEY_TAG";

    private static final String TAG = ClientAdvertiser.class.getSimpleName();
    private static final String USER_ID_BUNDLE_KEY = "USER_ID_BUNDLE_KEY";
    private static final String USER_STATUS_BUNDLE_KEY = "USER_STATUS_BUNDLE_KEY";
    private static final String STOP_ADVERTISEMENT = "STOP_ADVERTISEMENT";

    private boolean mIsAdvertising;
    private BLEAdvertiser mBleAdvertiser;
    private String mUserId;

    /**
     * Starts advertisement.
     *
     * @param context    to start the service with.
     * @param userId     text to send via ble.
     * @param userStatus on/off the bus.
     */
    public static void startAdvertisement(final Context context, String userId, String userStatus) {
        final Intent intent = new Intent(context, ClientAdvertiser.class);
        intent.putExtra(USER_ID_BUNDLE_KEY, userId);
        intent.putExtra(USER_STATUS_BUNDLE_KEY, userStatus);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                context.startService(intent);
            }
        });
    }

    public static void stopAdvertisement(final Context context) {
        final Intent intent = new Intent(context, ClientAdvertiser.class);
        intent.putExtra(STOP_ADVERTISEMENT, true);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                context.startService(intent);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (intent != null) {
            String userStatus = intent.getStringExtra(USER_STATUS_BUNDLE_KEY);
            boolean stopAdvertisement = intent.getBooleanExtra(STOP_ADVERTISEMENT, false);

            if (stopAdvertisement) {
                if (mBleAdvertiser != null) {
                    showToast("Advertisement stopped");
                    mBleAdvertiser.stopAdvertising();
                    closeService();
                }
            } else if (intent.getExtras() != null && userStatus != null) {
                mUserId = intent.getStringExtra(USER_ID_BUNDLE_KEY);
                if (!mIsAdvertising) {
                    userStatus = intent.getStringExtra(USER_STATUS_BUNDLE_KEY);
                    mBleAdvertiser = new BLEAdvertiser(this, UuidConstants.UUID);
                    if (mBleAdvertiser.isMultipleAdvertisementSupported()) {
                        mIsAdvertising = true;
                        Log.d("Capability", "Multiple Advertisements supported");
                        mBleAdvertiser.startAdvertising(mUserId, userStatus);
                    } else {
                        Log.d("Capability", "Multiple Advertisements NOT supported!");
                        Log.d("Bluetooth is enabled: ", Boolean.toString(mBleAdvertiser.isBluetoothEnabled()));
                        if (mBleAdvertiser.isBluetoothEnabled()){
                            showToast("Advertisement not supported");
                        } else {
                            showToast("Bluetooth is off");
                        }
                        closeService();
                    }
                } else {
                    // start advertising new user status
                    Log.d(TAG, "update user status");
                    mBleAdvertiser.stopAdvertising();
                    mBleAdvertiser.startAdvertising(mUserId, userStatus);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void closeService() {
        mIsAdvertising = false;
        stopSelf();
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /* BLEAdvertiser interface implementation. */

    @Override
    public void onAdvertiseStartSuccess() {
        showToast("Advertisement started");
    }

    @Override
    public void onAdvertiseStartFailed(int error) {
        Log.e(TAG, "Advertising failed, errorCode: " + error);
        showToast("Advertisement stopped");
        mBleAdvertiser.stopAdvertising();
        closeService();
    }
}
