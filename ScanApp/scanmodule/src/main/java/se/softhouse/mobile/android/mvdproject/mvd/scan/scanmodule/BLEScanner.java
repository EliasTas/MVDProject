package se.softhouse.mobile.android.mvdproject.mvd.scan.scanmodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.UUID;

/**
 * BLE code. Scan after ble devices.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
class BLEScanner {

    protected static final int START_SCAN_UNSUCCESSFUL = 16;

    private BLEScannerInterface mListener;
    private final ParcelUuid ADVERTISE_SERVICE_PARSELUUID;
    private BluetoothAdapter mBluetoothLEScanner;

    /**
     * @param listener between MVDScanner and BLEScanner.
     * @param uuidString filter result with uuid.
     */
    BLEScanner(BLEScannerInterface listener, String uuidString){
        mListener = listener;
        ADVERTISE_SERVICE_PARSELUUID = ParcelUuid.fromString(uuidString);
    }

    /**
     * Start the scanning after ble devices. Sends result to {@link BLEScannerInterface#onScanResult}.
     */
    void startScan() {

        // start callback
        UUID[] uuids = new UUID[1];
        uuids[0] = ADVERTISE_SERVICE_PARSELUUID.getUuid();
        boolean startScanSuccessful = false;
        if(getBluetoothLEScanner() != null) {
            startScanSuccessful = getBluetoothLEScanner().startLeScan(mLEScanCallback);
        }
        if(!startScanSuccessful){
            // is bluetooth on?
            mListener.onScanFailed(START_SCAN_UNSUCCESSFUL);
        }
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (scanRecord != null) {
                mListener.onScanResult(scanRecord);
            }
        }
    };

    /**
     * Stop scan.
     */
    void stopScan() {
        getBluetoothLEScanner().stopLeScan(mLEScanCallback);
    }

    private BluetoothAdapter getBluetoothLEScanner() {
        if(mBluetoothLEScanner == null){
            mBluetoothLEScanner = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothLEScanner;
    }
}
