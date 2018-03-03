package tk.talcharnes.unborify.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

/**
 * Created by khuramchaudhry on 2/28/18.
 */

public class PermissionConstants {

    public static final String READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_STORGAE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String CAMERA = Manifest.permission.CAMERA;


    public static String[] checkIfPermissionsAreGranted(Activity activity,
                                                    String... permissions) {
        ArrayList<String> pendingPermissionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                pendingPermissionList.add(permission);
            }
        }

        return pendingPermissionList.toArray(new String[0]);
    }

    public static void askForPermissions(Activity activity, final int requestId, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions,requestId);
    }
}
