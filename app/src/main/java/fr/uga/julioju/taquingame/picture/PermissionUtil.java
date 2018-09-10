/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

// From https://github.com/googlesamples/android-RuntimePermissions/blob/230cecd4256b0d0d88b8f0da28874ffb9dfa5260/Application/src/main/java/com/example/android/system/runtimepermissions/PermissionUtil.java

package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.pm.PackageManager;

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
    static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise
        // return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
