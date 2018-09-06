package fr.uga.julioju.taquingame.taquin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

class SplitImageUtil  {

    // Source http://www.chansek.com/splittingdividing-image-into-smaller/
    static private BitmapDrawable[] splitImage(Context context,
            Bitmap bitmapOriginal, int gridLength) {

        Resources resources = context.getResources();

        // For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        // To store all the small image chunks in bitmap format in this list
        BitmapDrawable [] chunkedImages =
            new BitmapDrawable[gridLength * gridLength];

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOriginal,
                bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), true);
        chunkHeight = bitmapOriginal.getHeight() / gridLength;
        chunkWidth = bitmapOriginal.getWidth() / gridLength;

        android.util.Log.d("size origin",
                        String.valueOf(bitmapOriginal.getRowBytes()));
        int chunkedImagesIndex = 0;
        // xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int row = 0 ; row < gridLength ; row++) {
            int xCoord = 0;
            for(int column = 0; column < gridLength ; column++) {
                if (row == 0 && column == 0) {
                    chunkedImages[0] = null;
                }
                else {
                    Bitmap bitmapLoc = Bitmap.createBitmap(scaledBitmap,
                        xCoord, yCoord, chunkWidth, chunkHeight);
                    BitmapDrawable bitmapDrawableLoc =
                        new BitmapDrawable(resources, bitmapLoc);
                    chunkedImages[chunkedImagesIndex] = bitmapDrawableLoc;
                    android.util.Log.d("size" + row + " " + column,
                            String.valueOf(bitmapLoc.getRowBytes()));
                }
                chunkedImagesIndex++;
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        return chunkedImages;
    }

    // https://developer.android.com/guide/topics/providers/document-provider#open-client
    static private Bitmap getBitmapFromUri(Context context,
            Uri uriImage) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
            context.getContentResolver().openFileDescriptor(uriImage, "r");
        if (parcelFileDescriptor != null) {
            FileDescriptor fileDescriptor =
                     parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }
        return null;
    }

    static BitmapDrawable[] generateBitmapDrawableArray(Context context,
            int gridLength, Uri uriImage) {
        Bitmap bitmapOriginal = null;
        try {
            bitmapOriginal = SplitImageUtil.getBitmapFromUri(context, uriImage);
        } catch (IOException e) {
            Toast.makeText(context, "Error when try to retrieve picture with " +
                    " uri. " + uriImage.toString() +
                    " No picture is displayed in Squares."
                    , Toast.LENGTH_LONG).show();
        }
        return SplitImageUtil.splitImage(context, bitmapOriginal, gridLength);
    }

}