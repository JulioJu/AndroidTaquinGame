package fr.uga.julioju.taquingame;

import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

/**
  * A Square is simply a `android.view.View' (commonly named Widget).
  * @see https://developer.android.com/reference/android/view/View
  */
public class Square {

    private TextView view;

    // Actually create a TextView
    public Square (AppCompatActivity activity,
            ConstraintLayout layout, String androidText) {

        // Create View
        this.view = new TextView(activity);

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

    public void setView(TextView view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public int getViewId() {
        return view.getId();
    }

}
