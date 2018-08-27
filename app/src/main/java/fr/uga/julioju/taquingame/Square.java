package fr.uga.julioju.taquingame;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

/**
  * A Square is simply a `android.view.View' (commonly named Widget).
  * https://developer.android.com/reference/android/view/View
  * This class is package private.
  */
class Square extends android.support.v7.widget.AppCompatTextView {

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

        super(activity);

        this.squareArrayIndex=squareArrayIndex;

        // Create ID
        // https://stackoverflow.com/questions/1714297/android-view-setidint-id-programmatically-how-to-avoid-id-conflicts
        int viewId = View.generateViewId();
        super.setId(viewId);

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
        layoutParamsWrap.setMargins(50, 0, 50, 0);

        // Set dimensions to view
        super.setLayoutParams(layoutParamsWrap);

        // Add text to view
        super.setText(androidText);
        // view.setTextSize(15);

        // Add View to layout
        // SHOULD BE HERE, BEFORE ConstraintSet
        layout.addView(this);

    }

    // Override default constructors otherwise there is a Warning:
    // https://stackoverflow.com/questions/17063090/custom-view-is-missing-constructor-used-by-tools-for-adapter
    public Square(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    // Override default constructors otherwise there is a Warning:
    // https://stackoverflow.com/questions/17063090/custom-view-is-missing-constructor-used-by-tools-for-adapter
    public Square(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new UnsupportedOperationException();
    }

    int getSquareArrayIndex() {
        return this.squareArrayIndex;
    }

}
