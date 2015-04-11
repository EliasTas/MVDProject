package se.softhouse.mobile.android.mvdproject.mvd.scan;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

import se.softhouse.mobile.android.mvdproject.coremodule.MandatoryVehicleRules;
import se.softhouse.mobile.android.mvdproject.mvd.scan.broadcastdatamodule.MVDBroadcastDataModule;
import se.softhouse.mobile.android.mvdproject.mvd.scan.busmodule.MVDBusModule;
import se.softhouse.mobile.android.mvdproject.mvd.scan.databasemodule.UserDatabase;
import se.softhouse.mobile.android.mvdproject.mvd.scan.scanmodule.MVDScanner;

/**
 * Controls four modules: BroadcastdataModule, BusRulesModule, DataBaseModules and ScanModule.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class ScanAppController extends Service {

    private ScanAppBroadcastReceiver mRegisteredBroadcastReceiver;
    private HashMap<String, String> mBusRules;

    private static final String TAG = ScanAppController.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if(mRegisteredBroadcastReceiver != null) {
            try {
                unregisterReceiver(mRegisteredBroadcastReceiver);
            } catch (IllegalArgumentException ignored) {
            }
        }
        registerReceiver();
        startMVDScannerModule();
        return START_STICKY;
    }

    private void registerReceiver() {
        mRegisteredBroadcastReceiver = new ScanAppBroadcastReceiver();
        final IntentFilter actions = new IntentFilter();
        actions.addAction(MVDScanner.MVDSCANNER_ACTION_TAG);
        actions.addAction(UserDatabase.MVDDATABASE_ACTION_TAG);
        registerReceiver(mRegisteredBroadcastReceiver, actions);
    }

    private class ScanAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extra = intent.getExtras();
            if (action.equals(MVDScanner.MVDSCANNER_ACTION_TAG)) {
                String userId = extra.getString(MVDScanner.MVDSCANNER_ACTION_KEY_TAG_USER_ID);
                String userStatus = extra.getString(MVDScanner.MVDSCANNER_ACTION_KEY_TAG_USER_STATUS);
                mBusRules = getBusRules(userId, userStatus);
                Log.d(TAG, "busrules: " + mBusRules.toString());
                verifyUser(userId, mBusRules.get(MandatoryVehicleRules.TRIP).equals(getString(R.string.settings_trip_end)));
            } else if (action.equals(UserDatabase.MVDDATABASE_ACTION_TAG)) {
                if (extra.containsKey(UserDatabase.MVDDATABASE_ACTION_KEY_TAG)) {
                    boolean isDuplicate = extra.getBoolean(UserDatabase.MVDDATABASE_ACTION_KEY_TAG);
                    Log.d(TAG, "isDuplicate: " + isDuplicate);
                    if (!isDuplicate) {
                        publishData(mBusRules);
                    }
                }
                startMVDScannerModule();
            }
        }
    }

    private void startMVDScannerModule() {
        MVDScanner.requestScan(this);
    }

    private void verifyUser(String userId, boolean isInBus) {
        UserDatabase.checkUser(this, userId, isInBus);
    }

    private void publishData(HashMap<String, String> msg) {
        MVDBroadcastDataModule.sendData(this, msg);
    }

    private HashMap<String, String> getBusRules(String userId, String userStatus) {
        return MVDBusModule.requestBusRules(ScanAppController.this, userId, userStatus);
    }
}
