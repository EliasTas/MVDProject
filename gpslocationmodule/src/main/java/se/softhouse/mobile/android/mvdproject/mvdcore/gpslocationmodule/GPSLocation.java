package se.softhouse.mobile.android.mvdproject.mvdcore.gpslocationmodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Get the current address on the device.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class GPSLocation extends Service {

    public static final String GPSLOCATION_ACTION_TAG = "MVD_SCAN_GPSLOCATION_ACTION_TAG";
    public static final String GPSLOCATION_ACTION_KEY_TAG = "MVD_SCAN_GPSLOCATION_ACTION_KEY_TAG";

    private static final String TAG = GPSLocation.class.getSimpleName();

    /**
     * The location will be returned as a string with format "latitude:longitude".
     * Sends the location via a broadcast with action-tag: {@link #GPSLOCATION_ACTION_TAG} and actionkey-tag: {@link #GPSLOCATION_ACTION_KEY_TAG}.
     *
     * @param context to start the service with.
     */
    public static void requestCurrentAddress(Context context) {
        final Intent intent = new Intent(context, GPSLocation.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onCreateInGPS");
        // System location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // When setting the interval to 0, 0 the location listener will get a notify as frequently as possible
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        // sendToBroadcast("55.55555:44.44444");

        return super.onStartCommand(intent, flags, startId);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "found a location");
            sendToBroadcast(getLatitudeAndLongitude(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }
    };

    private void sendToBroadcast(String location){
        Intent i = new Intent(GPSLOCATION_ACTION_TAG);
        i.putExtra(GPSLOCATION_ACTION_KEY_TAG, location);
        sendBroadcast(i);
        stopService();
    }

    private String getLatitudeAndLongitude(Location location){
        return location.getLatitude() + ":" + location.getLongitude();
    }

    /**
     * Get city, address, street, etc.
     */
    private String getLocation(Location location){
        Geocoder geo = new Geocoder(GPSLocation.this);
        List<Address> fromLocation;
        try {
            fromLocation = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return fromLocation.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void stopService(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(mLocationListener);
        stopSelf();
    }
}