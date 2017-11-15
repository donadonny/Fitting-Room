package tk.talcharnes.unborify.Utilities;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by khuramchaudhry on 9/6/17.
 */

public class Analytics {


    public static void registerSwipe(Context context, String swipeDirection) {
        Bundle params = new Bundle();
        params.putInt(swipeDirection, 1);
        FirebaseAnalytics.getInstance(context).logEvent("swipes", params);
    }

    public static void registerUpload(Context context, String userID) {
        Bundle params = new Bundle();
        params.putString("uid", userID);
        if (context != null) {
            FirebaseAnalytics.getInstance(context).logEvent("photo_uploads", params);
        }
    }
}
