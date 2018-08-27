package fr.uga.julioju.taquingame;

import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

/**
  * A Square is simply a `android.view.View' (commonly named Widget).
  * https://developer.android.com/reference/android/view/View
  * This class is package private.
  */
class Square {

    private final TextView view;
    /**
      * index of this Square in the board. In ./MainActivity.java,
      *     there is an Array named <code>squareArray</code> that contain
      *     all the Square of the board drawn.
      */
    private final int squareArrayIndex;

    // Actually create a TextView
    Square (AppCompatActivity activity,
            ConstraintLayout layout, int squareArrayIndex,
            String androidText) {

        // Create View
        this.view = new TextView(activity);

        this.squareArrayIndex=squareArrayIndex;

        // Create ID
        // https://stackoverflow.com/questions/1714297/android-view-setidint-id-programmatically-how-to-avoid-id-conflicts
        int viewId = View.generateViewId();
        view.setId(viewId);

        // ViewGroup.LayoutParams
        // XML attributes:
        // android:layout_height
        // android:layout_width
        // https://developer.android.com/reference/android/view/ViewGroup.LayoutParams
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);

        // ViewGroup.MarginLayoutParams
        // e.g. of XML attributes:
        // android:layout_margin
        // https://developer.android.com/reference/android/view/ViewGroup.MarginLayoutParams
        layoutParamsWrap.setMargins(0,0,0,0);

        // Set dimensions to view
        view.setLayoutParams(layoutParamsWrap);

        // Add text to view
        view.setText(androidText);
        // view.setTextSize(15);

        // Add View to layout
        // SHOULD BE HERE, BEFORE ConstraintSet
        layout.addView(view);

    }

    int getViewId() {
        return this.view.getId();
    }

    int getSquareArrayIndex() {
        return this.squareArrayIndex;
    }

    // Actually not used
    // public TextView getView() {
    //     return view;
    // }

}
