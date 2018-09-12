package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;

import android.support.annotation.NonNull;

import java.util.Date;

import java.io.*;

class TakePhotoSavedInPublicDirectoryFileUtil {

    @NonNull
    static private File createStorageDir(Activity activity, boolean
            isPublicDirectory) throws PictureActivityException {

        File storageDir;
        if (isPublicDirectory) {
            // « Create a path where we will place our picture in the user's
            // public pictures directory and check if the file exists.  If
            // external storage is not currently mounted this will think the
            // picture doesn't exist. »
            // https://developer.android.com/reference/android/os/Environment#getExternalStoragePublicDirectory(java.lang.String)

            // « Unlike Environment.getExternalStoragePublicDirectory(), the
            // directory returned here will be automatically created for you. »
            // https://developer.android.com/reference/android/content/Activity#getExternalFilesDir(java.lang.String)

            storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "taquingame");

            // Make sure the Pictures directory exists.
            if (! storageDir.exists()) {
                if (storageDir.mkdirs()) {
                    android.util.Log.i("mkdir", "'" + storageDir.toString() +
                            "' successfully created.");
                }
                else {
                    String messageError = "'" + storageDir.toString() + "'" +
                            " not created.";
                    throw new PictureActivityException(messageError);
                }
            }
            else {
                android.util.Log.i("mkdir", "'" + storageDir.toString() +
                        "' exists.");
            }
        }
        else {
            // Folder:
            // [...]/Android/data/fr.uga.julioju.taquingame/files/Pictures
            // See my issue at https://issuetracker.google.com/issues/114402174
            // named:
            // « Some part of second example of Activity.getExternalFilesDir are
            // wrong. »

            // « Unlike Environment.getExternalStoragePublicDirectory(), the
            // directory returned here will be automatically created for you. »
            // https://developer.android.com/reference/android/content/Activity#getExternalFilesDir(java.lang.String)
            storageDir = activity.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
        }

        // storageDir could be null.
        if (storageDir == null || ! storageDir.exists()) {
            String messageError = "ERROR: can't access app folder " +
                    "Is the external storage was unmounted ?";
            throw new PictureActivityException(messageError);
        }
        if (! Environment.getExternalStorageState(storageDir).equals(
                Environment.MEDIA_MOUNTED)) {
            String messageError = "Permission denied. You have no read / write" +
                " access to '" + storageDir.toString() + "'.";
            throw new PictureActivityException(messageError);
        }

        return storageDir;

    }

    /** @param isPublicDirectory if is true, Runtime Permission
            WRITE_EXTERNAL_STORAGE should already be tested */
    @NonNull
    static File createImageFile(Activity activity, boolean isPublicDirectory)
        throws PictureActivityException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new Date());
        String imageFileName = "taquinGame" + timeStamp + "_";

        File storageDir = TakePhotoSavedInPublicDirectoryFileUtil
            .createStorageDir(activity,
                isPublicDirectory);

        // Could not work
        // https://stackoverflow.com/questions/3660572/android-createtempfile-throws-permission-denied
        // And especially because
        // « 09-08 13:11:40.644 1807-1807/com.android.camera2
        //      E/CAM_StateSavePic: exception while saving result to URI:
        //      Optional.of(content://fr.uga.julioju.taquingame.fileprovider/images/taquinGame20180908_131135_/.jpg)
        // »
        // And can't work ! Because we want a scheme `content:///'
        // File image = new File(storageDir + "/" + imageFileName + "/"
        //    + ".jpg");

        // WORKS
        File image;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
                    );
        } catch (IOException ex) {
            String messageError = "'" + storageDir + "/" + imageFileName  +
                ".jpg" + "'" + " can't be created.";
            throw new PictureActivityException(messageError, ex);
        }

        return image;
    }

}
