package fr.uga.julioju.taquingame.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;

import java.util.Date;
import java.util.Locale;

import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;
import java.io.*;

public class PictureActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_IMAGE_URI =
        "fr.uga.julioju.taquingame.picture.PICTURE_URI";

    private static final int REQUEST_TAKE_PHOTO = 25;

    private static final int REQUEST_PICTURE_PICK = 17;

    private Uri photoURI = null;

    private String mCurrentPhotoPath;

    private void sendIntentToGame() {
        if (this.photoURI != null) {
            Intent intentOutcome = new Intent(this, TaquinActivity.class);

            // Third activity called returns its result to the first activity
            // To well understand this flag:
            // https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
            intentOutcome.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

            // To forward Intent parameter through chains of Activities:
            // https://stackoverflow.com/a/12905952
            intentOutcome.putExtras(super.getIntent());

            intentOutcome.putExtra(EXTRA_MESSAGE_IMAGE_URI, this.photoURI);

            super.startActivity(intentOutcome);
            super.finishAndRemoveTask();
        }
        else {
            android.util.Log.e("error",
                    Thread.currentThread().getStackTrace().toString());
            Toast.makeText(this, "ERROR: can't retrieve URI of a picture " +
                    "Please try again. ", Toast.LENGTH_LONG).show();
        }
    }

    // ==================== onActivityResult =======================
    // =============================================================

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // https://developer.android.com/guide/topics/providers/document-provider#open-client
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
            super.getContentResolver().openFileDescriptor(uri, "r");
        if (parcelFileDescriptor == null) {
            return null;
        }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent resultData) {
        if (requestCode == PictureActivity.REQUEST_PICTURE_PICK) {
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
                this.photoURI = resultData.getData();

                this.galleryAddPic();

                Bitmap bitmap = null;
                try {
                    bitmap = this.getBitmapFromUri(this.photoURI);
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    android.util.Log.e("IOexception",  sw.toString());
                    Toast.makeText(this, "ERROR: can't read " +
                            "the file selected.",
                            Toast.LENGTH_LONG).show();
                }
                if (bitmap == null || bitmap.getRowBytes() < 1) {
                    android.util.Log.e("error",
                            Thread.currentThread().getStackTrace().toString());
                    Toast.makeText(this, "ERROR: the photo you have " +
                            "selected has size zero." +
                            " Select an other file.",
                            Toast.LENGTH_LONG).show();
                    }
                    else {
                        this.sendIntentToGame();
                    }
            }
            else {
                android.util.Log.e("error",
                        Thread.currentThread().getStackTrace().toString());
                Toast.makeText(this, "ERROR: when the app pick a picture. " +
                        "Please try again. ", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PictureActivity.REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                // // See my issue at
                // // https://issuetracker.google.com/issues/114402174
                // // named:
                // // « Some part of second example of
                // // Context.getExternalFilesDir are wrong. »
                // // Tell the media scanner about the new file so that
                // // it is immediately available to the user.
                // MediaScannerConnection.scanFile(this,
                //         new String[] { bitmap.toString() }, null,
                //         new MediaScannerConnection
                //         .OnScanCompletedListener() {
                //             public void onScanCompleted(String path, Uri uri) {
                //                 android.util.Log.i( "ExternalStorage",
                //                         "Scanned " + path + ":");
                //                 android.util.Log.i( "ExternalStorage",
                //                         "-> uri=" + uri);
                //                     }
                //         });
                this.galleryAddPic();

                this.sendIntentToGame();
            }
            else {
                android.util.Log.e("error",
                        Thread.currentThread().getStackTrace().toString());
                Toast.makeText(this, "ERROR: when the app try to take " +
                        " a photo. " +
                        "Please try again. ", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ========= Common methods to take photos  ==========
    // =============================================================

    private File createStorageDir(boolean isPublicDirectory) {

        File storageDir;
        if (isPublicDirectory) {
            // « Create a path where we will place our picture in the user's
            // public pictures directory and check if the file exists.  If
            // external storage is not currently mounted this will think the
            // picture doesn't exist. »
            // https://developer.android.com/reference/android/os/Environment#getExternalStoragePublicDirectory(java.lang.String)

            // « Unlike Environment.getExternalStoragePublicDirectory(), the
            // directory returned here will be automatically created for you. »
            // https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)


            // Here, thisActivity is the current activity
            // https://stackoverflow.com/questions/38885982/android-file-mkdirs-always-return-false?rq=1
            if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Make sure the Pictures directory exists.
                storageDir = new File(Environment
                        .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "taquingame");
                android.util.Log.d("path photo 1", storageDir.toString() + "");

                // if (storageDir.mkdirs()) {
                //     android.util.Log.d("mkdir", storageDir.toString() +
                //             "successfully created.");
                // }
                // else {
                //     android.util.Log.e("error",
                //             Thread.currentThread().getStackTrace().toString());
                //     android.util.Log.e("mkdir", "ERROR: " + storageDir.toString() +
                //             " not created.");
                //     return null;
                // }
            }
            else {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "/taquingame");
                android.util.Log.e("path photo", storageDir.toString() + "");
                android.util.Log.e("error",
                        Thread.currentThread().getStackTrace().toString());
                android.util.Log.e("mkdir", "ERROR: " + storageDir.toString() +
                        "not created. (permission denied)");
                return null;
            }

        }
        else {
            // Folder:
            // [...]/Android/data/fr.uga.julioju.taquingame/files/Pictures
            // See my issue at https://issuetracker.google.com/issues/114402174
            // named:
            // « Some part of second example of Context.getExternalFilesDir are wrong. »

            // « Unlike Environment.getExternalStoragePublicDirectory(), the
            // directory returned here will be automatically created for you. »
            // https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)
            storageDir = super.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
        }
        // storageDir could be null.
        if (storageDir == null || ! storageDir.exists()) {
            android.util.Log.e("Access folder error", "Error, can't access" +
                    " app folder ");
            android.util.Log.e("error",
                    Thread.currentThread().getStackTrace().toString());
            Toast.makeText(this, "ERROR: can't access app folder " +
                    "Is the external storage was unmounted ?"
                    , Toast.LENGTH_LONG).show();
            return null;
        }
        if (! Environment.getExternalStorageState(storageDir).equals(
                Environment.MEDIA_MOUNTED)) {
            android.util.Log.e("Now write permission", "No write permission" +
                    " in " + storageDir.toString());
            android.util.Log.e("error",
                    Thread.currentThread().getStackTrace().toString());
            Toast.makeText(this, "ERROR: can't have read / write access to " +
                    storageDir.toString() , Toast.LENGTH_LONG).show();
            return null;
        }

        return storageDir;

    }


    private File createImageFile(boolean isPublicDirectory) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new Date());
        String imageFileName = "taquinGame" + timeStamp + "_";

        File storageDir = this.createStorageDir(isPublicDirectory);

        if (storageDir == null) {
            return null;
        }

        // Could not work
        // https://stackoverflow.com/questions/3660572/android-createtempfile-throws-permission-denied
        // And especially because
        // « 09-08 13:11:40.644 1807-1807/com.android.camera2 E/CAM_StateSavePic: exception while saving result to URI: Optional.of(content://fr.uga.julioju.taquingame.fileprovider/images/taquinGame20180908_131135_/.jpg) +
        // And probably can't work ! Because we want a scheme `content:///'
        // File image = new File(storageDir + "/" + imageFileName + "/" + ".jpg");

        // WORKS
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
                    );
            this.photoURI = Uri.fromFile(image);
            android.util.Log.d("path photo 4", image.toString() + "");
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            android.util.Log.e("IOexception",  sw.toString());
            Toast.makeText(this, "ERROR: can't create file. Is the external "+
                    "storage was unmounted? Or have you permission?",
                    Toast.LENGTH_LONG).show();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // ========= Take new Photo (saved in public folder)  ==========
    // =============================================================

    private void dispatchTakePictureIntentPublicFolderWithPermission() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = this.createImageFile(true);
        if (photoFile == null) {
            android.util.Log.e("error",
                    Thread.currentThread().getStackTrace().toString());
            Toast.makeText(this, "ERROR: file not successfully created",
                    Toast.LENGTH_LONG).show();
            return ;
        }
        android.util.Log.d("path photo", photoFile.toString() + "");

        // // https://stackoverflow.com/questions/43247674/saving-photos-and-videos-using-android-fileprovider-to-the-gallery/43303823
        // String AUTHORITY_FORMAT = "%s.fileprovider";
        // String packageName = super.getApplicationContext().getPackageName();
        // String authority = String.format(Locale.getDefault(),
        //         AUTHORITY_FORMAT, packageName);
        // Uri uri = FileProvider.getUriForFile(super.getApplicationContext(),
        //         authority, photoFile);
        Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                "fr.uga.julioju.taquingame.fileprovider",
                photoFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUriContentScheme);
        super.startActivityForResult(takePictureIntent,
                PictureActivity.REQUEST_TAKE_PHOTO);
    }


    @Override
    @NonNull
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            android.util.Log.i("coucou", "coocoiucoicoiucu");
            this.dispatchTakePictureIntentPublicFolderWithPermission();
        }
        // TODO improve it with
        // https://developer.android.com/guide/topics/permissions/overview

        // this.dispatchTakePictureIntentPublicFolderWithPermission();
        // switch (requestCode) {
        //     case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
        //         // If request is cancelled, the result arrays are empty.
        //         if (grantResults.length > 0
        //             && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //             // permission was granted, yay! Do the
        //             // contacts-related task you need to do.
        //         } else {
        //             // permission denied, boo! Disable the
        //             // functionality that depends on this permission.
        //         }
        //         return;
        //     }
        //
        //     // other 'case' lines to check for other
        //     // permissions this app might request.
        // }
    }

    private void dispatchTakePictureIntentPublicFolder() {
        // TODO improve it with
        // https://developer.android.com/guide/topics/permissions/overview
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA},
                50);
        // if (ContextCompat.checkSelfPermission(this,
        //             Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //         != PackageManager.PERMISSION_GRANTED) {
        //
        //     // Permission is not granted
        //     // Should we show an explanation?
        //     if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        //                 Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        //         // Show an explanation to the user *asynchronously* -- don't block
        //         // this thread waiting for the user's response! After the user
        //         // sees the explanation, try again to request the permission.
        //         android.util.Log.e("permission error",
        //                 Thread.currentThread().getStackTrace().toString());
        //         Toast.makeText(this, "ERROR: see logs, not cool to be here",
        //                 Toast.LENGTH_LONG).show();
        //     } else {
        //         // No explanation needed; request the permission
        //
        //         // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        //         // app-defined int constant. The callback method gets the
        //         // result of the request.
        //     }
        // } else {
        //     this.dispatchTakePictureIntentPublicFolderWithPermission();
        // }

    }

    // ==================== Pick picture from gallery ==============
    // =============================================================

    private void pickPictureFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Even if there is no chooser displayed, better to use
        // createChooser in case of there isn't provider app installed.
        super.startActivityForResult(Intent.createChooser(intent,
                   "Select Picture"), PictureActivity.REQUEST_PICTURE_PICK);

    }

    // ==== Take new Photo (saved in app's folder not display in gallery)  ====
    // =============================================================

    // https://developer.android.com/training/camera/photobasics#TaskPath
    private void dispatchTakePictureIntentPrivateFolder() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = this.createImageFile(false);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Uri with scheme `content://' and not `file://' contrary to
                // `this.photoURI'
                // See https://developer.android.com/reference/android/os/FileUriExposedException
                Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                        "fr.uga.julioju.taquingame.fileprovider",
                        photoFile);
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
                android.util.Log.e("error",
                        Thread.currentThread().getStackTrace().toString());
                Toast.makeText(this, "ERROR: file not successfully created",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            android.util.Log.e("error",
                    Thread.currentThread().getStackTrace().toString());
            Toast.makeText(this, "ERROR: Your device can't take photo",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ==== Pick a picture in Filesystem (classic file explorer ====
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
        // intent.setDataAndType(storageDirUri, "image#<{(|");
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, storageDirUri);

        // https://stackoverflow.com/a/48045737
        // In the particular case of ACTION_GET_CONTENT, that will tend to route
        // directly to a system-supplied UI for obtaining content, bypassing any
        // chooser, on Android 4.4+.
        super.startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), PictureActivity.REQUEST_PICTURE_PICK);

    }

    // ====================== Create Layout =====i==================
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
    private int createButtonGroup(ViewGroup layout, int smallestWidth) {
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

        this.createOneButton(buttonGroup, "Pick a picture\nin filesystem", view ->
                PictureActivity.this.performFileSearch(), smallestWidth);

        layout.addView(buttonGroup);

        return buttonGroupId;
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        int buttonGroupId = this.createButtonGroup(layout, smallestWidth);

        CreateView.centerAView(layout, buttonGroupId);

        int titleId = CreateView.createTextView(new TextView(this), layout,
                "Retrieve photo for the puzzle", smallestWidth, true);

        CreateView.viewCenteredInTopOfOtherView(layout, titleId, buttonGroupId);

    }

}
