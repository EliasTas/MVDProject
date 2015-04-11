package se.softhouse.mobile.android.mvdproject.mvd.scan.scanmodule;

/**
 * Interface between BLEScanner and MVDScanner.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
interface BLEScannerInterface {

    /**
     * Invokes when found a advertising device with data.
     * @param serviceData is advertising data.
     */
    void onScanResult(byte[] serviceData);

    /**
     * Could not scan after ble-devices.
     * @param errorCode .
     */
    void onScanFailed(int errorCode);
}
