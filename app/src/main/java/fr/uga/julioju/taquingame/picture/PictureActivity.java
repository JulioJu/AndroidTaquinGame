package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
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

import android.support.constraint.ConstraintLayout;

import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Date;

import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

public class PictureActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_IMAGE_URI =
        "fr.uga.julioju.taquingame.picture.PICTURE_URI";

    private static final int REQUEST_TAKE_PHOTO = 25;

    private static final int REQUEST_PICTURE_PICK = 17;

    private Uri photoURI = null;

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
            Toast.makeText(this, "ERROR: can't retrieve URI of a picture " +
                    "Please try again. ", Toast.LENGTH_LONG).show();
        }
    }

    // ==================== onActivityResult =======================
    // =============================================================

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
                Bitmap bitmap = null;
                try {
                    bitmap = this.getBitmapFromUri(this.photoURI);
                } catch (IOException e) {
                    android.util.Log.e("IOexception",  e.toString());
                    Toast.makeText(this, "ERROR: can't read " +
                            "the file selected.",
                            Toast.LENGTH_LONG).show();
                }
                if (bitmap == null || bitmap.getRowBytes() < 1) {
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
                Toast.makeText(this, "ERROR: when the app pick a picture. " +
                        "Please try again. ", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PictureActivity.REQUEST_TAKE_PHOTO ) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                this.sendIntentToGame();
            }
            else {
                Toast.makeText(this, "ERROR: when the app try to take " +
                        " a photo. " +
                        "Please try again. ", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ========= Take new Photo (saved in public folder)  ==========
    // =============================================================

    private void dispatchTakePictureIntentPublicFolder() {

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

    private File createImageFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new Date());
        String imageFileName = "taquinGame" + timeStamp + "_";

        // Folder:
        // [...]/Android/data/fr.uga.julioju.taquingame/files/Pictures
        // « Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist. »
        // https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)
        File storageDir = super.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        if (storageDir == null || ! storageDir.exists()) {
            Toast.makeText(this, "ERROR: can't access private app folder " +
                    "Is the external storage was unmounted ?"
                    , Toast.LENGTH_LONG).show();
            return null;
        }
        if (! Environment.getExternalStorageState(storageDir).equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "ERROR: can't have read / write access to " +
                    storageDir.toString() , Toast.LENGTH_LONG).show();
            return null;
        }

        // Could not work
        // https://stackoverflow.com/questions/3660572/android-createtempfile-throws-permission-denied
        // And probably can't work ! Because we want a scheme `content:///'
        // File image = new File(storageDir + "/" + imageFileName + "/" + ".jpg");
        // this.photoURI = Uri.fromFile(image);

        // WORKS
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
                    );
            this.photoURI = Uri.fromFile(image);
        } catch (IOException ex) {
            android.util.Log.e("IOException", ex.toString());
            Toast.makeText(this, "ERROR: can't create a file. Is the external "+
                    "storage was unmounted? ",
                    Toast.LENGTH_LONG).show();
        }

        return image;
    }

    // https://developer.android.com/training/camera/photobasics#TaskPath
    private void dispatchTakePictureIntentPrivateFolder() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = this.createImageFile();
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
                super.startActivityForResult(takePictureIntent,
                        PictureActivity.REQUEST_TAKE_PHOTO);
            }
            else {
                Toast.makeText(this, "ERROR: file not successfully created",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
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

        // Can't work, because we could send to another app only URI of
        //  form `content:///` for private folder app
        // // https://developer.android.com/reference/android/provider/DocumentsContract#EXTRA_INITIAL_URI
        // // « The initial location is system specific if this extra is missing or
        // //     document navigator failed to locate the desired initial location.
        // // »
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, storageDirUri);
        // intent.setDataAndType(storageDirUri, "image#<{(|");

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
