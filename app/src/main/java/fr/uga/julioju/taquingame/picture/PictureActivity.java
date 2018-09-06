package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.IOException;
import java.util.Date;

import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

// Sources http://www.chansek.com/splittingdividing-picture-into-smaller/
// https://developer.android.com/guide/topics/providers/document-provider#open-client
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
                    this.sendIntentToGame();
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new Date());
        String imageFileName = "taquinGame" + timeStamp + "_";
        File storageDir = super.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
                );
        this.photoURI = Uri.fromFile(image);
        return image;
    }

    // https://developer.android.com/training/camera/photobasics#TaskPath
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = this.createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "ERROR: can't create a file",
                        Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Uri with scheme `content://' and not `file://' contrary to
                // `this.photoURI'
                // See https://developer.android.com/reference/android/os/FileUriExposedException
                Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                        "fr.uga.julioju.taquingame.fileprovider",
                        photoFile);
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

        // Filter to show only pictures, using the picture MIME data type.
        // To search for all documents available via installed storage
        // providers, it would be "*/*".
        intent.setType("image/*");

        super.startActivityForResult(intent,
                PictureActivity.REQUEST_PICTURE_PICK);
    }

    /**
      * Create two buttons
      * @return id of the group
      */
    private int createButtons(ViewGroup layout, int smallestWidth) {
        LinearLayout buttonGroup = new LinearLayout(this);
        int buttonGroupId = View.generateViewId();
        buttonGroup.setId(buttonGroupId);
        buttonGroup.setOrientation(LinearLayout.VERTICAL);
        buttonGroup.setGravity(Gravity.CENTER);
        buttonGroup.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));

        Button buttonPicturePick = new Button(this);
        buttonPicturePick.setOnClickListener(view ->
                PictureActivity.this.performFileSearch());
        CreateView.createTextView(buttonPicturePick, buttonGroup,
                "Pick a picture", smallestWidth, false);

        Button buttonPictureCapture = new Button(this);
        buttonPictureCapture.setOnClickListener(view ->
                PictureActivity.this.dispatchTakePictureIntent());
        CreateView.createTextView(buttonPictureCapture, buttonGroup,
                "Take new photo", smallestWidth, false);

        layout.addView(buttonGroup);

        return buttonGroupId;
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        int buttonGroupId = this.createButtons(layout, smallestWidth);

        CreateView.centerAView(layout, buttonGroupId);

        int titleId = CreateView.createTextView(new TextView(this), layout,
                "Retrieve photo for the puzzle", smallestWidth, true);

        CreateView.viewCenteredInTopOfOtherView(layout, titleId, buttonGroupId);

    }

}
