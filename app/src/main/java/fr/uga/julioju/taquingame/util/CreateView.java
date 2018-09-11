package fr.uga.julioju.taquingame.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

public class CreateView  {

    static public void setTextSize (TextView textView, int smallestWidth) {
        // TODO optimize for other screen size
        if (smallestWidth >= 600) {
            textView.setTextSize(40);
        }
    }

    /**
      * Create a TextView object and return its id
      * @param isTitle true if the text is a title that should be displayed
      *     on top of an other object, and a little bit bigger
      */
    static public int createTextView(TextView textView,
            ViewGroup layout, String string, int smallestWidth,
            boolean isTitle) {
        int textViewId = View.generateViewId();
        textView.setId(textViewId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(
                    ConstraintSet.WRAP_CONTENT,
                    ConstraintSet.WRAP_CONTENT);
        if (isTitle) {
            if (smallestWidth >= 600) {
                layoutParamsWrap.setMargins(0, 0, 0, 22);
                textView.setTextSize(45);
            } else {
                layoutParamsWrap.setMargins(0, 0, 0, 12);
                textView.setTextSize(25);
            }
        }
        textView.setLayoutParams(layoutParamsWrap);
        textView.setText(string);
        CreateView.setTextSize(textView, smallestWidth);
        layout.addView(textView);
        return textViewId;
    }

    /**
      * View horizontally centered in its PARENT
      * and put on top on another view
      */
    static public void viewCenteredInTopOfOtherView (ConstraintLayout layout,
            int viewId, int toViewId) {
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(viewId, ConstraintSet.BOTTOM, toViewId,
                ConstraintSet.TOP);
        set.connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT);
        set.connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT);
        set.applyTo(layout);
    }

    /** Center a View in its PARENT and put a title on top */
    static public void centerAView(ConstraintLayout layout, int viewId) {
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT);
        set.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        set.connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT);
        set.connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        set.applyTo(layout);
    }

    /** Create a ConstraintLayout object and return this object */
    static public ConstraintLayout createLayout(Activity activity) {
        ConstraintLayout layout = new ConstraintLayout(activity);
        layout.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
        activity.setContentView(layout);
        return layout;
    }

}
