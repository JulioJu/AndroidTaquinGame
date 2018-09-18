package fr.uga.julioju.taquingame.picture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;


import android.support.design.widget.Snackbar;
import fr.uga.julioju.taquingame.R;
import java.io.*;

/**
  * All code to take a photo and save it in public Directory,
  * except onActivityResult() that is in the child class
  */
public abstract class TakePhotoSavedInPublicDirectory extends
        AppCompatActivity {

    private static final int
        INTENT_PERMISSION_WRITE_EXTERNAL_STORAGE = 50;

    private static final int INTENT_ANDROID_SETTINGS = 1000;

    protected ConstraintLayout layout;
    // package private
    File imageFile;

    private boolean isNotPermissionsWriteExternal() {
        boolean returnStatement = false;
        if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.e("runtime permission", "'"        +
                    Manifest.permission.WRITE_EXTERNAL_STORAGE  +
                    "' not granted. Should be granted.");
            returnStatement = true;
        }
        return returnStatement;
    }

    private void dispatchTakePictureIntentPublicFolderWithPermission()
            throws PictureActivityException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.imageFile = TakePhotoSavedInPublicDirectoryFileUtil
            .createImageFile(this, true);

        // // https://stackoverflow.com/questions/43247674/saving-photos-and-videos-using-android-fileprovider-to-the-gallery/43303823
        // String AUTHORITY_FORMAT = "%s.fileprovider";
        // String packageName = super.getApplicationContext().getPackageName();
        // String authority = String.format(Locale.getDefault(),
        //         AUTHORITY_FORMAT, packageName);
        // Uri uri = FileProvider.getUriForFile(super.getApplicationContext(),
        //         authority, this.imageFile);
        Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                "fr.uga.julioju.taquingame.fileprovider",
                this.imageFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                photoUriContentScheme);
        super.startActivityForResult(takePictureIntent,
                PictureActivity.INTENT_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent resultData) {
        if (requestCode == TakePhotoSavedInPublicDirectory
                .INTENT_ANDROID_SETTINGS) {
            this.dispatchTakePictureIntentPublicFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String permissions[], @NonNull int[] grantResults) {

        // See:
        // https://developer.android.com/guide/topics/permissions/overview
        // Inspired from:
        // https://developer.android.com/training/permissions/requesting
        // See also:
        // https://developer.android.com/reference/android/app/Activity.html#onRequestPermissionsResult(int,%20java.lang.String[],%20int[])

        // Strongly inspired from         :
        // https://github.com/googlesamples/android-RuntimePermissions/blob/master/Application/src/main/java/com/example/android/system/runtimepermissions/PermissionUtil.java

        // « Verify that each required permission has been granted, otherwise
        // return false. »
        if (requestCode == TakePhotoSavedInPublicDirectory
                    .INTENT_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            android.util.Log.i("permission",
                    "Received response for permissions request.");

            // « We have requested multiple permissions all of them need to be
            // checked. »
            if (PermissionUtil.verifyPermissions(permissions, grantResults)) {
                // « All required permissions have been granted, »
                // launch the game
                try {
                    this.dispatchTakePictureIntentPublicFolderWithPermission();
                }
                catch (PictureActivityException e){
                    PictureActivityException.displayError(this.layout, e);
                }
            } else {
                // A little inspired from:
                // https://www.vladmarton.com/granted-denied-and-permanently-denied-permissions-in-android/
                // WARNING : explanations in this website are wrong !
                Snackbar
                    .make(this.layout, R.string
                            .permission_write_external_storage_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS",
                            view -> {
                                Intent intent = new Intent(Settings
                                        .ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        super.getPackageName(), null);
                                intent.setData(uri);
                                super.startActivityForResult(intent,
                                        TakePhotoSavedInPublicDirectory
                                        .INTENT_ANDROID_SETTINGS);
                            })
                .show();
            }
        }

    }

    private void activityCompatRequestPermissions() {
        // « request the permission
        // The callback method gets the result of the request. »
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    TakePhotoSavedInPublicDirectory
                    .INTENT_PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    // package private
    void dispatchTakePictureIntentPublicFolder() {
        // See:
        // https://developer.android.com/guide/topics/permissions/overview
        // Inspired from:
        // https://developer.android.com/training/permissions/requesting
        if (this.isNotPermissionsWriteExternal()) {

            // « Permission is not granted
            // Should we show an explanation? »
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // https://developer.android.com/training/permissions/requesting#make-the-request
                // « Show an explanation to the user *asynchronously* -- don't
                // block this thread waiting for the user's response! After the
                // user sees the explanation, try again to request the
                // permission. »

                // https://developer.android.com/training/permissions/requesting#explain
                // « Android provides a utility method,
                // shouldShowRequestPermissionRationale(), that returns true
                // if the user has previously denied the request, and
                // returns false if a user has denied a permission and
                // selected the Don't ask again option in the permission
                // request dialog, or if a device policy prohibits the
                // permission. »

                // See also my issue at
                // https://issuetracker.google.com/issues/114554343

                // Strongly inspired from
                // https://github.com/googlesamples/android-RuntimePermissions/blob/230cecd4256b0d0d88b8f0da28874ffb9dfa5260/Application/src/main/java/com/example/android/system/runtimepermissions/MainActivity.java#L136,L153

                // « Provide an additional rationale to the user if the permission
                // was not granted and the user would benefit from additional
                // context for the use of the permission.  For example if the
                // user has previously denied the permission. »
                android.util.Log.i("permission",
                        "You must storage permission to write in public " +
                        "directory.");
                Snackbar
                    .make(layout, R.string
                            .permission_write_external_storage_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view ->
                        this.activityCompatRequestPermissions()
                    )
                .show();
            }
            else {
                // No explanation needed, we can request the permission.
                this.activityCompatRequestPermissions();
            }
        } else {
            // Permission has already been granted
            try {
                this.dispatchTakePictureIntentPublicFolderWithPermission();
            }
            catch (PictureActivityException e){
                PictureActivityException.displayError(this.layout, e);
            }
        }

    }

}
