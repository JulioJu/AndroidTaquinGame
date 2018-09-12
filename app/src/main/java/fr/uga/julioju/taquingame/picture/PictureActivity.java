package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.support.annotation.Nullable;

import android.support.constraint.ConstraintLayout;


import android.support.v4.content.FileProvider;



import fr.uga.julioju.taquingame.taquin.TaquinActivity;
import fr.uga.julioju.taquingame.util.CreateView;
import fr.uga.julioju.taquingame.util.DetectScreen;
import fr.uga.julioju.taquingame.util.ImageUtil;
import java.io.*;

public class PictureActivity extends TakePhotoSavedInPublicDirectory {

    public static final String EXTRA_MESSAGE_IMAGE_URI =
        "fr.uga.julioju.taquingame.picture.PICTURE_URI";

    static final int REQUEST_TAKE_PHOTO = 25;

    private static final int REQUEST_PICTURE_PICK = 17;

    // ====================== onActivityResult =====================
    // =============================================================
    // =============================================================
    // =============================================================
    // =============================================================

    /** @param photoUri
      *     null if it's launch in the context of the intent REQUEST_TAKE_PHOTO
      *             and super.imageFile is no null
      *     no null if it's launch in the context of the intent
      *             REQUEST_PICTURE_PICK and super.imageFile is null
      */
    private void sendIntentToGame(@Nullable Uri photoUri)
            throws PictureActivityException {
        if (super.imageFile != null) {
            photoUri = Uri.fromFile(super.imageFile);
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

    /** Could not be added if it a app private folder */
    private void galleryAddPic() {
        // // See my issue at
        // // https://issuetracker.google.com/issues/114402174
        // // named: // « Some part of second example of
        Intent mediaScanIntent =
            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String mCurrentPhotoPath = super.imageFile.getAbsolutePath();
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
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PictureActivity.REQUEST_PICTURE_PICK) {
            try {
                this.onActivityResultPicturePick(resultCode, resultData);
            } catch (PictureActivityException e) {
                PictureActivityException.displayError(super.layout, e);
            }
        }
        else if (requestCode == PictureActivity.REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                // Could not be added if it a app private folder
                // Method executed but without result in this case.
                this.galleryAddPic();
                try {
                    this.sendIntentToGame(null);
                } catch (PictureActivityException e) {
                    PictureActivityException.displayError(super.layout, e);
                }
            } else {
                try {
                    String messageError = "Error when the app " +
                        " try to take  a photo. " + "Please try again.";
                    throw new PictureActivityException(messageError);
                } catch (PictureActivityException e) {
                    PictureActivityException.displayError(super.layout, e);
                }
            }
        }
    }

    // ====================== Buttons action =======================
    // =============================================================
    // =============================================================
    // =============================================================
    // =============================================================

    /** Pick picture from gallery */
    private void pickPictureFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Even if there is no chooser displayed, better to use
        // createChooser in case of there isn't provider app installed.
        super.startActivityForResult(Intent.createChooser(intent,
                   "Select Picture"), PictureActivity.REQUEST_PICTURE_PICK);

    }

    /** Take new Photo (saved in app's folder not display in gallery) */
    // https://developer.android.com/training/camera/photobasics#TaskPath
    private void dispatchTakePictureIntentPrivateFolder() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                super.imageFile = TakePhotoSavedInPublicDirectoryFileUtil
                    .createImageFile(this, false);
            } catch (PictureActivityException e) {
                PictureActivityException.displayError(super.layout, e);
                return ;
            }
            // Continue only if the File was successfully created
            // Uri with scheme `content://' and not `file://'.
            // `photoUri' have scheme `file://' because we are in the same app.
            // See https://developer.android.com/reference/android/os/FileUriExposedException
            Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                    "fr.uga.julioju.taquingame.fileprovider",
                    super.imageFile);
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
                PictureActivityException.displayError(super.layout, e);
            }
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

    // ====================== Create Layout ========================
    // =============================================================
    // =============================================================
    // =============================================================
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
                PictureActivity.super.dispatchTakePictureIntentPublicFolder(),
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

        super.layout.addView(buttonGroup);

        return buttonGroupId;
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        super.layout = CreateView.createLayout(this);

        int buttonGroupId = this.createButtonGroup(smallestWidth);

        CreateView.centerAView(super.layout, buttonGroupId);

        int titleId = CreateView.createTextView(new TextView(this), super.layout,
                "Retrieve photo for the puzzle", smallestWidth, true);

        CreateView.viewCenteredInTopOfOtherView(super.layout,
                titleId, buttonGroupId);

    }

}
