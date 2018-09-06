package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.support.constraint.ConstraintLayout;

import android.support.v7.app.AppCompatActivity;


import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

// Sources http://www.chansek.com/splittingdividing-image-into-smaller/
// https://developer.android.com/guide/topics/providers/document-provider#open-client
public class PictureActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String EXTRA_MESSAGE_IMAGE_URI =
        "fr.uga.julioju.taquingame.picture.IMAGE_URI";

    private static final int REQUEST_PICTURE_PICKER = 17;

    private void sendIntentToGame(Uri uriImage) {
        Intent intentOutcome = new Intent(this, TaquinActivity.class);

        // Third activity called returns its result to the first activity
        // To well understand this flag:
        // https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
        intentOutcome.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        // To forward Intent parameter through chains of Activities:
        // https://stackoverflow.com/a/12905952
        intentOutcome.putExtras(super.getIntent());

        intentOutcome.putExtra(EXTRA_MESSAGE_IMAGE_URI, uriImage);

        super.startActivity(intentOutcome);
        super.finishAndRemoveTask();
    }

    private void sendErrorMessage() {
        Toast.makeText(this, "Error when the app pick a picture. " +
                "Please try again. ", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent resultData) {
        // Source:
        // https://developer.android.com/guide/topics/providers/document-provider#results
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // REQUEST_PICTURE_PICKER. If the request code seen here doesn't match,
        // it's the response to some other intent, and the code below shouldn't
        // run at all.
        if (requestCode == PictureActivity.REQUEST_PICTURE_PICKER
                && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the
            // intent.
            // Instead, a URI to that document will be contained in the return
            // intent provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                Uri uriImage = resultData.getData();
                this.sendIntentToGame(uriImage);
            }
            else {
                this.sendErrorMessage();
            }
        } else {
            this.sendErrorMessage();
        }
    }

    // Source:
    // https://developer.android.com/guide/topics/providers/document-provider#search
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // To search for all documents available via installed storage
        // providers, it would be "*/*".
        intent.setType("image/*");

        super.startActivityForResult(intent,
                PictureActivity.REQUEST_PICTURE_PICKER);
    }

    @Override
    public void onClick(View view) {
        this.performFileSearch();
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        Button button = new Button(this);
        button.setOnClickListener(this);
        int buttonId = CreateView.createTextView(button, layout,
                "Click", smallestWidth, false);

        CreateView.centerAView(layout, buttonId);

    }

}
