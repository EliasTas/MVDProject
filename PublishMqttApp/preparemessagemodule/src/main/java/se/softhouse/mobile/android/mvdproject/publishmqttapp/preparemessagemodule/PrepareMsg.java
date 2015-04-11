package se.softhouse.mobile.android.mvdproject.publishmqttapp.preparemessagemodule;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import se.softhouse.mobile.android.mvdproject.coremodule.MandatoryVehicleRules;

/**
 * Adjusting the data that fits the server.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class PrepareMsg {

    private static final String TAG = PrepareMsg.class.getSimpleName();

    /**
     * Convert a hashmap to a JSONObject.
     *
     * @param map with data.
     * @return a jsonobject.
     */
    public static JSONObject prepareMsg(HashMap map) {
        final String userId = MandatoryVehicleRules.USER_ID;
        final String trip = MandatoryVehicleRules.TRIP;

        String jsonString = "{ \"" + "d" + "\":" + "{"
                + "\"" + userId + "\"" + ":" + "\"" + Integer.valueOf((String)map.remove(userId)) + "\"" + ","
                + "\"" + trip + "\"" + ":" + "\"" + map.remove(trip) + "\"" + ","
                + "\"" + "metaData" + "\"" + ":" + new JSONObject(map).toString()
                + "}" + "}";

        Log.d(TAG, jsonString);

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not create jsonObject");
            return null;
        }
    }
}
