package fr.uga.julioju.taquingame;

import android.view.View;

// https://developer.android.com/guide/topics/ui/ui-events
/**
  * Manage actions when we click on a Square
  */
public class SquareOnClickListener implements View.OnClickListener {

    /**
     * When we click in a Square, retrieve information of the Square and
     * display it in Logcat.
     * Implement the OnClickListener callback
     */
    @Override
    public void onClick(View v) {
        Square square = (Square) v;
        android.util.Log.i("clicked", "`square.getId()`: " + square.getId() +
                "\n`squareArray.getSquareArrayIndex()': " +
                square.getSquareArrayIndex() +
                "\n`unorderedListIndex.getUnorderedListIndex()': " +
                square.getUnorderedListIndex());
    }

}
