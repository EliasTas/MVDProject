package se.softhouse.mobile.android.mvdproject.mvd.scan.databasemodule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Database. Prevents duplicates.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
public class UserDatabase extends Service {

    public static final String MVDDATABASE_ACTION_TAG = "MVDDATABASE_ACTION_TAG";
    /**
     * Boolean. True if duplicate.
     */
    public static final String MVDDATABASE_ACTION_KEY_TAG = "MVDDATABASE_ACTION_KEY_TAG";

    private static final String USER_BUNDLE_KEY = "USER_BUNDLE_KEY";
    private static final String USER_ENDS_TRIP_BUNDLE_KEY = "USER_ENDS_TRIP_BUNDLE_KEY";

    private static final String TAG = UserDatabase.class.getSimpleName();

    /**
     * Database.
     */
    private static ArrayList<String> users = new ArrayList<String>();

    /**
     * Sends the result via a broadcast with action-tag: {@link #MVDDATABASE_ACTION_TAG} and actionkey-tag: {@link #MVDDATABASE_ACTION_KEY_TAG}.
     *
     * @param context      to start the service with.
     * @param userId       user-id.
     * @param userEndsTrip false if user is in the bus.
     */
    public static void checkUser(Context context, String userId, boolean userEndsTrip) {
        final Intent intent = new Intent(context, UserDatabase.class);
        intent.putExtra(USER_BUNDLE_KEY, userId);
        intent.putExtra(USER_ENDS_TRIP_BUNDLE_KEY, userEndsTrip);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String user;
        boolean userEndsTrip;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            userEndsTrip = extras.getBoolean(USER_ENDS_TRIP_BUNDLE_KEY);
            user = extras.getString(USER_BUNDLE_KEY);
            // Check if user has already started a trip. If user ends trip then user will be removed from database.
            if (userEndsTrip) {
                // user is not in the bus
                removeUser(user);
            } else {
                addUser(user);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void removeUser(String user) {
        sendToBroadcast(!users.remove(user));
    }

    private void addUser(String user) {
        boolean isDuplicate = true;
        if (!users.contains(user)) {
            users.add(user);
            isDuplicate = false;
        }
        sendToBroadcast(isDuplicate);
    }


    private void sendToBroadcast(boolean isDuplicate) {
        // broadcast receiver
        closeService();
        Intent i = new Intent(MVDDATABASE_ACTION_TAG);
        i.putExtra(MVDDATABASE_ACTION_KEY_TAG, isDuplicate);
        sendBroadcast(i);
    }

    private void closeService() {
        stopSelf();
    }

}
