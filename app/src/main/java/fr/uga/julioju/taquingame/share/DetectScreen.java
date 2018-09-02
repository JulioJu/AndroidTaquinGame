package fr.uga.julioju.taquingame.share;

import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;

public class DetectScreen  {

    // Inspired from:
    // https://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically/15133776#15133776
    /**
      * @return width of the Screen with unit `dp'
      * Read README to understand how it works
    */
    static public int getSmallestWidth(AppCompatActivity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        // https://developer.android.com/reference/android/view/Display.html#getMetrics(android.util.DisplayMetrics)
        // « The returned size may be adjusted to exclude certain system decor
        // elements that are always visible ». For example, Navigation bar !
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        return (int) Math.min(widthDp, heightDp);
    }

}
