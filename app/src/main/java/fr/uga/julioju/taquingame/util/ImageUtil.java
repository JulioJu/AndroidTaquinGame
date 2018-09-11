package fr.uga.julioju.taquingame.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.IOException;

public class ImageUtil  {

    // Source http://www.chansek.com/splittingdividing-image-into-smaller/
    @NonNull
    private static BitmapDrawable[] splitImage(Context context,
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

    // https://developer.android.com/topic/performance/graphics/load-bitmap
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both height and width larger than the requested height and
            // width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // https://developer.android.com/guide/topics/providers/document-provider#open-client
    /** Return null when option.inJustDecodeBounds = true */
    @Nullable
    private static Bitmap getBitmapFromUri(Context context,
            Uri uriImage, BitmapFactory.Options options,
            String messageError) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
            context.getContentResolver().openFileDescriptor(uriImage, "r");
        if (parcelFileDescriptor == null) {
            throw new IOException(messageError);
        }
        FileDescriptor fileDescriptor =
                    parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor,
                null,
                options);
        parcelFileDescriptor.close();
        return image;
    }

    // https://developer.android.com/topic/performance/graphics/load-bitmap
    @NonNull
    private static Bitmap decodeSampledBitmapFromStream(Context context,
            Uri uri, int reqWidth, int reqHeight)
        throws IOException {

        String messageError = "Fail to decode bitmap from Uri" +
            uri.toString();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options bitmapOption =
            new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;
        // https://stackoverflow.com/a/6228188
        // bitmapOption.inDither=true; //optional (deprecated)
        // bitmapOption.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        ImageUtil.getBitmapFromUri(context, uri,
                bitmapOption, messageError);

        // https://stackoverflow.com/a/6228188
        if ((bitmapOption.outWidth == -1)
                || (bitmapOption.outHeight == -1)) {
            throw new IOException(messageError);
        }

        // Calculate inSampleSize
        bitmapOption.inSampleSize =
            ImageUtil.calculateInSampleSize(bitmapOption, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        bitmapOption.inJustDecodeBounds = false;

        Bitmap image = ImageUtil.getBitmapFromUri(context, uri,
                bitmapOption, messageError);
        if (image == null) {
            throw new IOException(messageError);
        }
        return image;
    }

    @NonNull
    public static BitmapDrawable[] generateBitmapDrawableArray(Context context,
            int gridLength, int screenWidth, int screenHeight,
            @NonNull Uri uriImage)
            throws IOException {
        // Bitmap bitmapOriginal = ImageUtil.getBitmapFromUri(context,
        //         uriImage, null, "test");
        Bitmap bitmapOriginal = ImageUtil.decodeSampledBitmapFromStream(
                context, uriImage, screenWidth, screenHeight);
        return ImageUtil.splitImage(context, bitmapOriginal, gridLength);
    }

    public static boolean isBitmapIsEmpty(Context context, Uri photoUri)
            throws  IOException {

        final BitmapFactory.Options bitmapOption =
            new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;
        ImageUtil.getBitmapFromUri(context, photoUri,
                bitmapOption, "IOException");
        return (bitmapOption.outWidth == -1)
            || (bitmapOption.outHeight == -1);
    }

}
