package fr.uga.julioju.taquingame.taquin;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import fr.uga.julioju.taquingame.R;

class SplitImageUtil  {

    // Source http://www.chansek.com/splittingdividing-image-into-smaller/
    static private BitmapDrawable[] splitImage(Resources resources,
            Bitmap bitmapOriginal, int gridLength) {

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

    static BitmapDrawable[] generateBitmapDrawableArray(Resources resources,
            int gridLength) {
        Bitmap bitmapOriginal = BitmapFactory.decodeResource(resources,
                R.drawable.archlinux);
        return SplitImageUtil.splitImage(resources, bitmapOriginal, gridLength);
    }

}
