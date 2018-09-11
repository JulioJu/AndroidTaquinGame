package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.pm.PackageManager;

// Inspired from:
// https://github.com/googlesamples/android-RuntimePermissions/blob/230cecd4256b0d0d88b8f0da28874ffb9dfa5260/Application/src/main/java/com/example/android/system/runtimepermissions/PermissionUtil.java

/**
 * Utility class that wraps access to the runtime permissions API in M and
 * provides basic helper methods.
 */
abstract class PermissionUtil {

    /**
     * Check that all given permissions have been granted by verifying that each
     * entry in the given array is of the value {@link
     * PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    static boolean verifyPermissions(String[] permissions, int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            android.util.Log.e("Runtime permission", "No Runtime " +
                    " permission granted");
            return false;
        }

        boolean returnStatement = true;
        // Verify that each required permission has been granted, otherwise
        // return false.
        for (int i = 0 ; i < grantResults.length ; i ++) {
            // See readme
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                android.util.Log.e("Runtime permission", "Permission '" +
                        permissions[i] + "' not granted");
                returnStatement = false;
            } else {
            android.util.Log.i("permission", "Permission '" +
                    permissions[i] + "' granted");
            }
        }
        return returnStatement;
    }

}
