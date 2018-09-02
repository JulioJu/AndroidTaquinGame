package fr.uga.julioju.taquingame.taquin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import android.support.constraint.ConstraintLayout;
import fr.uga.julioju.taquingame.R;

/**
  * A Square is simply a `android.view.View' (commonly named Widget).
  * https://developer.android.com/reference/android/view/View
  * This class is package private.
  */
class Square extends android.support.v7.widget.AppCompatTextView {

    /** Order of the content displayed in the Square */
    private int orderOfTheContent;

    /** Row index in the grid */
    private final int row;

    /** Column index in the grid */
    private final int column;

    /** Create a TextView with an id, text and Constraint*/
    Square (Context activity, ConstraintLayout layout, int orderOfTheContent,
            int row, int column, int squareWidth, int squareHeight,
            int marginLeft, int marginTop) {

        super(activity);

        this.orderOfTheContent = orderOfTheContent;

        this.row = row;

        this.column = column;

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
            // The source code says:
            // « @param width the width [..], or a fixed size in pixels
            // @param height the height[..], or a fixed size in pixels »
            new ConstraintLayout.LayoutParams(squareWidth, squareHeight);

        // ViewGroup.MarginLayoutParams
        // e.g. of XML attributes:
        // android:layout_margin
        // https://developer.android.com/reference/android/view/ViewGroup.MarginLayoutParams
        layoutParamsWrap.setMargins(marginLeft, marginTop, 0, 0);

        // Set dimensions to view
        super.setLayoutParams(layoutParamsWrap);

        // Text centered horizontally and vertically
        super.setGravity(Gravity.CENTER);

        // See README.md
        super.setBackground(activity.getDrawable(R.drawable.back));

        // Add text to view
        super.setText(String.valueOf(this.orderOfTheContent));
        // view.setTextSize(15);

        // Add View to layout
        // SHOULD BE HERE, BEFORE ConstraintSet
        layout.addView(this);

    }

    // Override default constructors otherwise there is a Warning:
    // https://stackoverflow.com/questions/17063090/custom-view-is-missing-constructor-used-by-tools-for-adapter
    /** UnsupportedOperationException */
    public Square(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    // Override default constructors otherwise there is a Warning:
    // https://stackoverflow.com/questions/17063090/custom-view-is-missing-constructor-used-by-tools-for-adapter
    /** UnsupportedOperationException */
    public Square(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new UnsupportedOperationException();
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    int getOrderOfTheContent() {
        return orderOfTheContent;
    }

    void setOrderOfTheContent(int orderOfTheContent) {
        this.orderOfTheContent = orderOfTheContent;
        this.setText(String.valueOf(this.orderOfTheContent));
    }

}
