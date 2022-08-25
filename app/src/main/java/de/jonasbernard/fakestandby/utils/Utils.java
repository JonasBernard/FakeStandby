package android.jonas.fakestandby.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    private static Boolean firstOpen = null;
    /**
     * Checks if the user is opening the app for the first time.
     * Note that this method should be placed inside an activity and it can be called multiple times.
     * @return boolean
     */
    public static boolean isFirstOpen(Context context) {
        if (firstOpen == null) {
            SharedPreferences mPreferences = context.getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, Context.MODE_PRIVATE);
            firstOpen = mPreferences.getBoolean(Constants.Preferences.FIRST_OPEN, true);
            if (firstOpen) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(Constants.Preferences.FIRST_OPEN, false);
                editor.commit();
            }
        }
        return firstOpen;
    }

    public static boolean isOverlayShowing(Context context) {
        return context.getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(Constants.Preferences.IS_OVERLAY_SHOWING, false);
    }

}
