package fr.uga.julioju.taquingame.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.support.constraint.ConstraintLayout;

import android.support.v7.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import fr.uga.julioju.taquingame.R;
import fr.uga.julioju.taquingame.main.MainActivity;
import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

// Sources http://www.chansek.com/splittingdividing-image-into-smaller/
// https://developer.android.com/guide/topics/providers/document-provider#open-client
public class PictureActivity extends AppCompatActivity
        implements View.OnClickListener {

    /** Length of the grid. SHOULD NOT BE < 2 */
    private int gridLength;

    public static final String EXTRA_MESSAGE_BITMAP_ARRAY =
        "fr.uga.julioju.taquingame.picture.BITMAP_ARRAY";

    private static final int REQUEST_PICTURE_PICKER = 17;

    private void sendIntentToGame(ArrayList<Bitmap> bitmapArray) {
        Intent intentOutcome = new Intent(this, TaquinActivity.class);

        // Third activity called returns its result to the first activity
        // To well understand this flag:
        // https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
        intentOutcome.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        // To forward Intent parameter through chains of Activities:
        // https://stackoverflow.com/a/12905952
        intentOutcome.putExtras(super.getIntent());

        intentOutcome.putParcelableArrayListExtra(EXTRA_MESSAGE_BITMAP_ARRAY,
                bitmapArray);

        super.startActivity(intentOutcome);
        super.finishAndRemoveTask();
    }


    // Source http://www.chansek.com/splittingdividing-image-into-smaller/
    private ArrayList<Bitmap> splitImage(Bitmap bitmap) {

        // For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        // To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImages =
            new ArrayList<>(this.gridLength * this.gridLength);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth(), bitmap.getHeight(), true);
        chunkHeight = bitmap.getHeight() / this.gridLength;
        chunkWidth = bitmap.getWidth() / this.gridLength;

        android.util.Log.d("size origin",
                        String.valueOf(bitmap.getRowBytes()));
        // xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int row = 0 ; row < this.gridLength ; row++) {
            int xCoord = 0;
            for(int column = 0; column < this.gridLength ; column++) {
                if (row == 0 && column == 0) {
                    chunkedImages.add(null);
                }
                else {
                    Bitmap bitmapLoc = Bitmap.createBitmap(scaledBitmap,
                        xCoord, yCoord, chunkWidth, chunkHeight);
                    chunkedImages.add(bitmapLoc);
                    android.util.Log.d("size" + row + " " + column,
                            String.valueOf(bitmapLoc.getRowBytes()));
                }
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        return chunkedImages;
    }

    // https://developer.android.com/guide/topics/providers/document-provider#open-client
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
            super.getContentResolver().openFileDescriptor(uri, "r");
        if (parcelFileDescriptor != null) {
            FileDescriptor fileDescriptor =
                     parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }
        return null;
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
            Uri uri;
            Bitmap image;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    image = this.getBitmapFromUri(uri);
                    this.sendIntentToGame(this.splitImage(image));
                } catch (IOException e) {
                    this.sendErrorMessage();
                }
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
        // this.performFileSearch();
        Bitmap bitmap = BitmapFactory.decodeResource(super.getResources(),
                R.drawable.archlinux);
        this.sendIntentToGame(this.splitImage(bitmap));
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intentIncome = super.getIntent();
        this.gridLength = intentIncome.getIntExtra(MainActivity
                        .EXTRA_MESSAGE_GRID_LENGTH, 10);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        Button button = new Button(this);
        button.setOnClickListener(this);
        int buttonId = CreateView.createTextView(button, layout,
                "Click", smallestWidth, false);

        CreateView.centerAView(layout, buttonId);

    }

}
