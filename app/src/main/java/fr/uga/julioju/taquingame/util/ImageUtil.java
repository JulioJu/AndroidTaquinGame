package fr.uga.julioju.taquingame.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.IOException;

import fr.uga.julioju.taquingame.picture.PictureActivityException;

public abstract class ImageUtil  {

    private static final int RATIO_SCREEN_IMAGE = 5;

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

    /** I've found it alone ^^ ! */
    private static Bitmap cropImage(Bitmap image, BitmapFactory.Options
            bitmapOption, int screenWidth, int screenHeight) {

        double imageWidthOrigin  = (double) bitmapOption.outWidth;
        double imageHeightOrigin = (double) bitmapOption.outHeight;

        double ratioScreenWidthScreenHeight =
            (double) screenWidth / (double) screenHeight;

        boolean isCropPhotoWidth = (screenWidth - imageWidthOrigin)
            <= (screenHeight - imageHeightOrigin);

        double xCoord       = 0;
        double yCoord       = 0;
        double chunkWidth   = imageWidthOrigin;
        double chunkHeight  = imageHeightOrigin;

        if (isCropPhotoWidth) {
            chunkWidth  = imageHeightOrigin * ratioScreenWidthScreenHeight;
            xCoord      = (imageWidthOrigin - chunkWidth) / 2;
        }
        else {
            chunkHeight = imageWidthOrigin / ratioScreenWidthScreenHeight;
            yCoord      = (imageHeightOrigin - chunkHeight) / 2;
        }

        android.util.Log.i("crop", "size in pixels"                      +
                "\nscreenWidth: "   + screenWidth                        +
                "\nscreenHeight: "                   + screenHeight      +
                "\nRatio screenWidth / screenHeight: "                   +
                    ratioScreenWidthScreenHeight                         +
                "\nimageWidthOrigin: "               + imageWidthOrigin  +
                "\nimageHeightOrigin: "              + imageHeightOrigin +
                "\nRatio imageWidthOrigin / imageHeightOrigin: "         +
                    (imageWidthOrigin / imageHeightOrigin)               +
                "\nchunkWidth: "                     + chunkWidth        +
                "\nchunkHeight: "                    + chunkHeight       +
                "\nRatio chunkWidth / chunkHeight: "                     +
                    (chunkWidth / chunkHeight)                           +
                "\nxCoord: "                         + xCoord            +
                "\nyCoord: "                         + yCoord            );
        return Bitmap.createBitmap(image, (int) xCoord, (int) yCoord,
            (int) chunkWidth, (int) chunkHeight);
    }

    // https://developer.android.com/topic/performance/graphics/load-bitmap
    @NonNull
    private static Bitmap decodeSampledBitmapFromStream(Context context, Uri
            uri, int screenWidth, int screenHeight) throws IOException,
            InterruptedException {

        String messageError = "Fail to decode bitmap from Uri" +
            uri.toString();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options bitmapOption =
            new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;
        // https://stackoverflow.com/a/6228188
        // bitmapOption.inDither=true; //optional (deprecated)
        // bitmapOption.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        Bitmap image = ImageUtil.getBitmapFromUri(context, uri,
                bitmapOption, messageError);

        // // https://stackoverflow.com/a/6228188
        // // As it's tested in PictureActivity.java, should always be true.
        // if ((imageWidth == -1)
        //         || (imageHeight == -1)) {
        //     throw new IOException(messageError);
        // }


        // Decode bitmap with inSampleSize set
        bitmapOption.inJustDecodeBounds = false;

        // Calculate inSampleSize
        bitmapOption.inSampleSize = ImageUtil
            .calculateInSampleSize(bitmapOption, screenWidth, screenHeight);

        for (int i = 0 ; image == null && i < 5  ; i++) {
            image = ImageUtil.getBitmapFromUri(context, uri,
                    bitmapOption, messageError);
            if (image == null) {
                android.util.Log.e("image is null",
                        "image is null, retrying: " + i);
                Thread.sleep(4000);
            }
        }

        if (image == null) {
            throw new IOException(messageError);
        }

        image = ImageUtil.cropImage(image, bitmapOption, screenWidth,
                screenHeight);
        if (image == null) {
            throw new IOException(messageError);
        }

        return image;

    }

    /** Used only by TaquinActivity */
    @NonNull
    public static BitmapDrawable[] generateBitmapDrawableArray(Context context,
            int gridLength, int screenWidth, int screenHeight,
            @NonNull Uri uriImage)
            throws IOException, InterruptedException {
        // Bitmap bitmapOriginal = ImageUtil.getBitmapFromUri(context,
        //         uriImage, null, "test");
        Bitmap bitmapOriginal = ImageUtil.decodeSampledBitmapFromStream(
                context, uriImage, screenWidth, screenHeight);
        return ImageUtil.splitImage(context, bitmapOriginal, gridLength);
    }

    private static boolean isBitmapIsEmpty(int imageWidth, int imageHeight) {
        return (imageWidth == -1) || (imageHeight == -1);
    }

    private static boolean isTooSmallImage(int screenWidth, int screenHeight,
            int imageWidth, int imageHeight) {
        return (screenWidth / ImageUtil.RATIO_SCREEN_IMAGE) > imageWidth
            && (screenHeight / ImageUtil.RATIO_SCREEN_IMAGE) > imageHeight;
    }

    public static Point screenSize(Activity activity) {
        // See :
        // https://developer.android.com/reference/android/view/WindowManager.html#getDefaultDisplay()
        // https://developer.android.com/reference/android/view/Display.html#getSize(android.graphics.Point)
        // « The returned size may be adjusted to exclude certain system decor
        // elements that are always visible ». For example, Navigation bar !
        Point screenSize = new Point();
        activity.getWindowManager()
            .getDefaultDisplay()
            .getSize(screenSize);
        return screenSize;
    }

    public static int[] isGoodImage(Activity activity, Uri photoUri)
            throws PictureActivityException {
        final BitmapFactory.Options bitmapOption =
            new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;
        try {
            ImageUtil.getBitmapFromUri(activity, photoUri,
                    bitmapOption, "IOException");
        } catch (IOException e){
            String messageError = "The photo you have selected" +
                " can't be read. Try with an other file.";
            throw new PictureActivityException(messageError, e);
        }

        int imageWidth = bitmapOption.outWidth;
        int imageHeight = bitmapOption.outHeight;

        Point screenSize = ImageUtil.screenSize(activity);
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;

        if (ImageUtil.isBitmapIsEmpty(imageWidth, imageHeight)) {
            String messageError = "The photo you have selected" +
                " has size zero." + " Select an other file.";
            throw new PictureActivityException(messageError);
        }

        if (ImageUtil.isTooSmallImage(screenWidth, screenHeight, imageWidth,
                    imageHeight)) {
            String messageError = "The photo has a resolution\n" +
                ImageUtil.RATIO_SCREEN_IMAGE + "x lower than " +
                " the screen. \nUse an other photo." +
                "\nscreen width: "   + screenWidth   +
                "\nscreen height: "  + screenHeight  +
                "\nimage width: "    + imageWidth    +
                "\nimage height: "   + imageHeight;
            throw new PictureActivityException(messageError);
        }

        return new int[] {imageWidth, imageHeight};

    }

}
