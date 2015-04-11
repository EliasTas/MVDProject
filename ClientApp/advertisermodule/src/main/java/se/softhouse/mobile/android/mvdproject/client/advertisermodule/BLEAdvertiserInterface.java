package se.softhouse.mobile.android.mvdproject.client.advertisermodule;

/**
 * Interface between ClientAdvertiser and BLEAdvertiser.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
interface BLEAdvertiserInterface {

    /**
     * Advertising is successful.
     */
    void onAdvertiseStartSuccess();

    /**
     * Advertising failed.
     */
    void onAdvertiseStartFailed(int error);
}
