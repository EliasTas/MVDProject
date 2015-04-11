package se.softhouse.mobile.android.mvdproject.mvd.scan.busmodule;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import se.softhouse.mobile.android.mvdproject.coremodule.MandatoryVehicleRules;
import se.softhouse.mobile.android.mvdproject.coremodule.UserStatus;

/**
 * Get vehicle-information.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class MVDBusModule {

    private static String TAG = MVDBusModule.class.getSimpleName();

    /**
     * Get vehicle-information.
     *
     * @param context        to start the service with.
     * @param userStatus     current userStatus.
     * @param userId         user id.
     * @return hashmap with information vehicle and user information.
     */
    public static HashMap<String, String> requestBusRules(Context context, String userId, String userStatus) {
        HashMap<String, String> map = new HashMap<String, String>();

        final Calendar instance = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateTime = simpleDateFormat.format(instance.getTime());

        if (UserStatus.OFF.equals(userStatus)) {
            map.put(MandatoryVehicleRules.TRIP, context.getString(R.string.settings_trip_end));
            Log.d(TAG, "user is not in the bus");
        } else {
            map.put(MandatoryVehicleRules.TRIP, context.getString(R.string.settings_trip_start));
            Log.d(TAG, "user is in the bus");
        }

        map.put(MandatoryVehicleRules.USER_ID, userId);
        map.put(context.getString(R.string.busrules_user_current_time), dateTime);
        map.put(context.getString(R.string.busrules_user_vehicle_id), String.valueOf(context.getResources().getInteger(R.integer.settings_vehicle_id)));
        map.put(context.getString(R.string.busrules_user_line), context.getString(R.string.settings_line));
        map.put(context.getString(R.string.busrules_user_latitude), "55.6121964");
        map.put(context.getString(R.string.busrules_user_longitude), "12.9993054");
        return map;
    }
}
