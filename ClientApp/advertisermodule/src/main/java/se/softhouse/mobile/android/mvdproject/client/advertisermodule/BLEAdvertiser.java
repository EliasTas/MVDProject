package se.softhouse.mobile.android.mvdproject.client.advertisermodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

import se.softhouse.mobile.android.mvdproject.coremodule.ByteConverter;

/**
 * BLE advertising code.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
class BLEAdvertiser {

    private static final String TAG = BLEAdvertiser.class.getSimpleName();
    private final ParcelUuid ADVERTISE_SERVICE_PARSELUUID;

    private final BLEAdvertiserInterface mListener;

    /**
     * Constructor.
     * @param listener between ClientAdvertiser and BLEAdvertiser.
     */
    BLEAdvertiser(BLEAdvertiserInterface listener, String uuidString) {
        mListener = listener;
        ADVERTISE_SERVICE_PARSELUUID = ParcelUuid.fromString(uuidString);
    }

    /**
     * Turn on or off local bluetooth-adapter.
     *
     * @param enable set true to enable bluetooth. Set false to disable bluetooth.
     */
    public void enableBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = getBTAdapter();

        if (isBluetoothEnabled() == enable) {
            return;
        }
        if (enable) {
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    /**
     * Check if local bluetooth-adapter is on.
     */
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = getBTAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * @return true if the device supports beacon transmission, has a Bluetooth LE chipset that supports peripheral mode, and a compatible hardware driver from the device manufacturer.
     */
    public boolean isMultipleAdvertisementSupported() {
        return getBTAdapter().isMultipleAdvertisementSupported();
    }

    /**
     * Start advertise data.
     * @param userId unique user id.
     * @param userStatus on/off hte buss.
     */
    public void startAdvertising(String userId, String userStatus) {
        getBleAdvertiser().startAdvertising(getAdvertisementSettings(), getAdvertiseData(userId, userStatus), mAdvertiseCallback);
    }

    /**
     * Stop advertising data.
     */
    public void stopAdvertising(){
        getBleAdvertiser().stopAdvertising(mAdvertiseCallback);
    }

    private BluetoothAdapter getBTAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private BluetoothLeAdvertiser getBleAdvertiser() {
        return getBTAdapter().getBluetoothLeAdvertiser();
    }

    private AdvertiseData getAdvertiseData(String userId, String userStatus) {
        AdvertiseData.Builder adBuilder = new AdvertiseData.Builder();
        adBuilder.setIncludeDeviceName(false); // saves bytes, depends on how long the name is
        adBuilder.setIncludeTxPowerLevel(false); // saves 3 bytes

        byte[] toSend = ByteConverter.convertToByteArray(userId + ":" + userStatus);

        // Can maximum send 27 bytes. Keep in mind that UUID takes 4 bytes.
        adBuilder.addServiceData(ADVERTISE_SERVICE_PARSELUUID, toSend);

        Log.d(TAG, ByteConverter.convertToString(toSend));
        return adBuilder.build();
    }

    private AdvertiseSettings getAdvertisementSettings() {
        AdvertiseSettings.Builder asBuilder = new AdvertiseSettings.Builder();
        asBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        asBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
        asBuilder.setConnectable(false);
        return asBuilder.build();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            mListener.onAdvertiseStartSuccess();
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            mListener.onAdvertiseStartFailed(errorCode);
        }
    };
}