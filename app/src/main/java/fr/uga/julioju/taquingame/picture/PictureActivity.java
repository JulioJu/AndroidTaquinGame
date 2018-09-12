package fr.uga.julioju.taquingame.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.constraint.ConstraintLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;


import android.support.design.widget.Snackbar;
import fr.uga.julioju.taquingame.R;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;
import fr.uga.julioju.taquingame.util.CreateView;
import fr.uga.julioju.taquingame.util.DetectScreen;
import fr.uga.julioju.taquingame.util.ImageUtil;
import java.io.*;

public class PictureActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_IMAGE_URI =
        "fr.uga.julioju.taquingame.picture.PICTURE_URI";

    private static final int REQUEST_TAKE_PHOTO = 25;

    private static final int REQUEST_PICTURE_PICK = 17;

    private static final int
        REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 50;

    private static final int REQUEST_ANDROID_SETTINGS = 1000;

    private ConstraintLayout layout;

    private File imageFile;

    /** @param photoUri
      *     null if it's launch in the context of the intent REQUEST_TAKE_PHOTO
      *             and this.imageFile is no null
      *     no null if it's launch in the context of the intent
      *             REQUEST_PICTURE_PICK and this.imageFile is null
      */
    private void sendIntentToGame(@Nullable Uri photoUri)
            throws PictureActivityException {
        if (this.imageFile != null) {
            photoUri = Uri.fromFile(this.imageFile);
        }
        if (photoUri == null) {
            // https://en.wikipedia.org/wiki/Defensive_programming
            String messageError = "can't retrieve URI of a picture. " +
                "Please try again.";
            throw new PictureActivityException(messageError);
        }

        Intent intentOutcome = new Intent(this, TaquinActivity.class);

        // Third activity called returns its result to the first activity
        // To well understand this flag:
        // https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
        intentOutcome.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        // To forward Intent parameter through chains of Activities:
        // https://stackoverflow.com/a/12905952
        intentOutcome.putExtras(super.getIntent());

        intentOutcome.putExtra(EXTRA_MESSAGE_IMAGE_URI, photoUri);

        super.startActivity(intentOutcome);
        super.finishAndRemoveTask();
    }

    // ==================== onActivityResult ======================={{{1
    // =============================================================

    private void galleryAddPic() {
        // // See my issue at
        // // https://issuetracker.google.com/issues/114402174
        // // named: // « Some part of second example of
        Intent mediaScanIntent =
            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String mCurrentPhotoPath = this.imageFile.getAbsolutePath();
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void onActivityResultPicturePick (int resultCode,
            Intent resultData) throws PictureActivityException {
        // Source:
        // https://developer.android.com/guide/topics/providers/document-provider#results
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // REQUEST_PICTURE_PICK. If the request code seen here doesn't
        // match, it's the response to some other intent, and the code below
        // shouldn't run at all.
        if (resultCode == Activity.RESULT_OK && resultData != null) {
            // The document selected by the user won't be returned in the
            // intent.
            // Instead, a URI to that document will be contained in the return
            // intent provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri photoUri = resultData.getData();

            boolean isBitmapIsEmpty;
            try {
                isBitmapIsEmpty = ImageUtil.isBitmapIsEmpty(this,
                        photoUri);
            } catch (IOException e) {
                String messageError = "The photo you have selected" +
                    " can't be read. Try with an other file.";
                throw new PictureActivityException(messageError);
            }

            if (isBitmapIsEmpty) {
                String messageError = "The photo you have selected" +
                    " has size zero." + " Select an other file.";
                throw new PictureActivityException(messageError);
            }
            this.sendIntentToGame(photoUri);
        }
        else {
            String messageError = "Error when you tried to choose a photo."  +
                    "Please try again. ";
            throw new PictureActivityException(messageError);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent resultData) {
        if (requestCode == PictureActivity.REQUEST_PICTURE_PICK) {
            try {
                this.onActivityResultPicturePick(resultCode, resultData);
            } catch (PictureActivityException e) {
                PictureActivityException.displayError(this.layout, e);
            }
        }
        else if (requestCode == PictureActivity.REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                this.galleryAddPic();
                try {
                    this.sendIntentToGame(null);
                } catch (PictureActivityException e) {
                    PictureActivityException.displayError(this.layout, e);
                }
            } else {
                try {
                    String messageError = "Error when the app " +
                        " try to take  a photo. " + "Please try again.";
                    throw new PictureActivityException(messageError);
                } catch (PictureActivityException e) {
                    PictureActivityException.displayError(this.layout, e);
                }
            }
        }
        else if (requestCode == PictureActivity.REQUEST_ANDROID_SETTINGS) {
            this.dispatchTakePictureIntentPublicFolder();
        }
    }

    // ========= Common methods to take photos  ========== {{{1
    // =============================================================

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


    // ========= Take new Photo (saved in public folder)  ========== {{{1
    // =============================================================

    private void dispatchTakePictureIntentPublicFolderWithPermission()
            throws PictureActivityException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.imageFile = TakePhotoFileUtil.createImageFile(this, true);

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
                PictureActivity.REQUEST_TAKE_PHOTO);
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
        if (requestCode == PictureActivity
                    .REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
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
                                        PictureActivity.REQUEST_ANDROID_SETTINGS);
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
                    PictureActivity
                    .REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void dispatchTakePictureIntentPublicFolder() {
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

    // ==================== Pick picture from gallery ============== {{{1
    // =============================================================

    private void pickPictureFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Even if there is no chooser displayed, better to use
        // createChooser in case of there isn't provider app installed.
        super.startActivityForResult(Intent.createChooser(intent,
                   "Select Picture"), PictureActivity.REQUEST_PICTURE_PICK);

    }

    // ==== Take new Photo (saved in app's folder not display in gallery)  {{{1
    // =============================================================

    // https://developer.android.com/training/camera/photobasics#TaskPath
    private void dispatchTakePictureIntentPrivateFolder() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                this.imageFile = TakePhotoFileUtil.createImageFile(this, false);
            } catch (PictureActivityException e) {
                PictureActivityException.displayError(this.layout, e);
                return ;
            }
            // Continue only if the File was successfully created
            // Uri with scheme `content://' and not `file://'.
            // `photoUri' have scheme `file://' because we are in the same app.
            // See https://developer.android.com/reference/android/os/FileUriExposedException
            Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                    "fr.uga.julioju.taquingame.fileprovider",
                    this.imageFile);
            android.util.Log.d("photoUriContentScheme",
                    photoUriContentScheme.toString());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoUriContentScheme);
            takePictureIntent
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            super.startActivityForResult(takePictureIntent,
                    PictureActivity.REQUEST_TAKE_PHOTO);
        }
        else {
            // Permission has already been granted
            try {
                String messageError = "Your device can't take photo";
                throw new PictureActivityException(messageError);
            } catch (PictureActivityException e){
                PictureActivityException.displayError(this.layout, e);
            }
        }
    }

    // ==== Pick a picture in Filesystem (classic file explorer ==== {{{1
    // =============================================================

    // Source:
    // https://developer.android.com/guide/topics/providers/document-provider#search
    /**
     * Fires an intent to spin up the "file chooser" UI and select an picture.
     */
    private void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // // Filter to show only pictures, using the picture MIME data type.
        // // To search for all documents available via installed storage
        // // providers, it would be "*/*".
        intent.setType("image/*");

        // Can't work, because see README:
        // Uri storageDirUri = Uri.fromFile(Environment
        //     .getExternalStoragePublicDirectory(Environment
        //             .DIRECTORY_PICTURES));
        // intent.setDataAndType(storageDirUri, "image#<{(|");
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
        //         storageDirUri);

        // https://stackoverflow.com/a/48045737
        // In the particular case of ACTION_GET_CONTENT, that will tend to route
        // directly to a system-supplied UI for obtaining content, bypassing any
        // chooser, on Android 4.4+.
        super.startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), PictureActivity.REQUEST_PICTURE_PICK);

    }

    // ====================== Create Layout =====i================== {{{1
    // =============================================================

    private void createOneButton(ViewGroup buttonGroup, String text,
            View.OnClickListener onClickListener, int smallestWidth) {
        Button buttonPicturePick = new Button(this);
        buttonPicturePick.setOnClickListener(onClickListener);
        CreateView.createTextView(buttonPicturePick, buttonGroup,
                text, smallestWidth, false);
    }

    /**
      * Create two buttons
      * @return id of the group
      */
    private int createButtonGroup(int smallestWidth) {
        LinearLayout buttonGroup = new LinearLayout(this);
        int buttonGroupId = View.generateViewId();
        buttonGroup.setId(buttonGroupId);
        buttonGroup.setOrientation(LinearLayout.VERTICAL);
        buttonGroup.setGravity(Gravity.CENTER);
        buttonGroup.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));

        this.createOneButton(buttonGroup, "Take new photo\n" +
                "(saved in public folder)", view ->
                PictureActivity.this.dispatchTakePictureIntentPublicFolder(),
                smallestWidth);

        this.createOneButton(buttonGroup, "Pick a picture\nin gallery", view ->
                PictureActivity.this.pickPictureFromGallery(),
                smallestWidth);

        View lineView = new View(this);
        ViewGroup.MarginLayoutParams layoutParamsWrap =
            new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2);
        layoutParamsWrap.setMargins(0, 10, 0, 10);
        lineView.setLayoutParams(layoutParamsWrap);
        lineView.setBackgroundColor(0xFFFFFFFF);
        buttonGroup.addView(lineView);

        this.createOneButton(buttonGroup, "Take new photo\n" +
                "(saved in app's folder\nnot displayed in gallery)", view ->
                PictureActivity.this.dispatchTakePictureIntentPrivateFolder(),
                smallestWidth);

        this.createOneButton(buttonGroup, "Pick a picture\nin filesystem",
                view -> PictureActivity.this.performFileSearch(),
                smallestWidth);

        this.layout.addView(buttonGroup);

        return buttonGroupId;
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        this.layout = CreateView.createLayout(this);

        int buttonGroupId = this.createButtonGroup(smallestWidth);

        CreateView.centerAView(this.layout, buttonGroupId);

        int titleId = CreateView.createTextView(new TextView(this), this.layout,
                "Retrieve photo for the puzzle", smallestWidth, true);

        CreateView.viewCenteredInTopOfOtherView(this.layout,
                titleId, buttonGroupId);

    }

}

// vim: foldmethod=marker sw=4 ts=4 et textwidth=80 foldlevel=0
