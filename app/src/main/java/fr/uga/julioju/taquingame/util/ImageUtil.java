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

public class ImageUtil  {

    private static int RATIO_SCREEN_IMAGE = 5;

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

    private static boolean isCropPhotoWidth(int screenWidth, int screenHeight,
            int imageWidth, int imageHeight) {
        return (screenWidth - imageWidth) <= (screenHeight - imageHeight);
    }

    private static boolean isImageGreaterThanScreen(int screenWidth, int
            screenHeight, int imageWidth, int imageHeight) {
        return screenWidth < imageWidth && screenHeight < imageHeight;
    }

    /** I've found it alone ^^ !  */
    // a / (b / c) - d raise a silent error not shown in Logcat
    private static double pixelWidthToRemove(int screenWidth, int screenHeight,
            int imageWidth, int imageHeight) {
        double imageHeightScreenHeightRatio =
            (double) imageHeight / (double) screenHeight;
        double imageWidthRatioScreen = imageWidth /
            imageHeightScreenHeightRatio;
        return imageWidthRatioScreen - screenWidth;
    }

    static private double ratioScreenImage(int screenLength, int imageLength) {
        return (double) screenLength / (double) imageLength;
    }

    /** I've found it alone ^^ ! */
    // a / (b / c) - d raise a silent error not shown in Logcat
    private static double pixelHeightToRemove(int screenWidth, int screenHeight,
            int imageWidth, int imageHeight) {
        double imageWidthScreenWidthRatio =
            (double) imageWidth / (double) screenWidth;
        double imageHeightRatioScreen = imageHeight /
            imageWidthScreenWidthRatio;
        return imageHeightRatioScreen - screenHeight;
    }

    /** I've found it alone ^^ ! */
    private static Bitmap cropImage(Bitmap image, BitmapFactory.Options
            bitmapOption, int screenWidth, int screenHeight) {

        int imageWidth = bitmapOption.outWidth;
        int imageHeight = bitmapOption.outHeight;


        boolean isCropPhotoWidth =
            ImageUtil.isCropPhotoWidth(screenWidth, screenHeight, imageWidth,
                imageHeight);

        double pixelWidthToRemove;
        double pixelHeightToRemove;

        double ratioScreenImage;

        double xCoord = 0;
        double yCoord = 0;
        double chunkWidth = imageWidth;
        double chunkHeight = imageHeight;

        boolean isImageGreaterThanScreen = ImageUtil.isImageGreaterThanScreen(
                screenWidth, screenHeight, imageWidth, imageHeight);

        if (isCropPhotoWidth) {
            pixelWidthToRemove =  ImageUtil
                .pixelWidthToRemove(screenWidth, screenHeight,
                        imageWidth, imageHeight);
            if (isImageGreaterThanScreen) {
                xCoord = pixelWidthToRemove / 2;
            } else {
                ratioScreenImage = ImageUtil
                    .ratioScreenImage(screenHeight, imageHeight);
                xCoord = pixelWidthToRemove / 2;
                xCoord = xCoord / ratioScreenImage;
            }
            chunkWidth = imageWidth - xCoord;
        }
        else {
            pixelHeightToRemove = ImageUtil
                .pixelHeightToRemove(screenWidth, screenHeight,
                        imageWidth, imageHeight);
            if (isImageGreaterThanScreen) {
                ratioScreenImage =  ImageUtil
                    .ratioScreenImage(screenWidth, imageWidth);
                yCoord = pixelHeightToRemove / 2;
                yCoord = yCoord / ratioScreenImage;
            } else {
                yCoord = pixelHeightToRemove / 2;
            }
            chunkHeight = imageHeight - yCoord;
        }

        android.util.Log.i("crop", "screenWidth: "  + screenWidth     +
                "\nscreenHeight: "                  + screenHeight    +
                "\nimageWidthOrigin: "              + imageWidth      +
                "\nimageHeightOrigin: "             + imageHeight     +
                "\nxCoord: "                        + xCoord          +
                "\nchunkWidth: "                    + chunkWidth      +
                "\nyCoord: "                        + yCoord          +
                "\nchunkHeight: "                   + chunkHeight );
        return Bitmap.createBitmap(image, (int) xCoord, (int) yCoord,
            (int) chunkWidth, (int) chunkHeight);
    }

    // https://developer.android.com/topic/performance/graphics/load-bitmap
    @NonNull
    private static Bitmap decodeSampledBitmapFromStream(Context context, Uri
            uri, int screenWidth, int screenHeight) throws IOException {

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

        Bitmap image = ImageUtil.getBitmapFromUri(context, uri,
                bitmapOption, messageError);
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
            throws IOException {
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
