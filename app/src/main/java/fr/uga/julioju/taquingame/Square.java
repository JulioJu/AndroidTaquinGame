package fr.uga.julioju.taquingame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import android.support.constraint.ConstraintLayout;

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
            int row, int column) {

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
