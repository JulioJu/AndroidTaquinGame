package fr.uga.julioju.taquingame;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Complete example:
        // ________________
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet

        TextView textView = new TextView(this);
        textView.setText(R.string.app_name);
        textView.setTextSize(50);

        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.constrained);


        // https://stackoverflow.com/questions/1714297/android-textView-setidint-id-programmatically-how-to-avoid-id-conflicts
        int textViewId = textView.generateViewId();
        textView.setId(textViewId);

        layout.addView(textView);

        // ViewGroup.LayoutParams
        // XML attributes:
        // android:layout_height
        // android:layout_width
        // https://developer.android.com/reference/android/textView/ViewGroup.LayoutParams
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        // ViewGroup.MarginLayoutParams
        // e.g. of XML attributes:
        // android:layout_margin
        // https://developer.android.com/reference/android/textView/ViewGroup.MarginLayoutParams
       layoutParams.setMargins(0,0,0,0);


        // set constraints
        textView.setLayoutParams(layoutParams);

        // Official doc:
        // https://developer.android.com/reference/android/support/constraint/ConstraintSet#setmargin
        // Very short model:
        // https://stackoverflow.com/a/45264822
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(textViewId, ConstraintSet.RIGHT,
                  ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        set.connect(textViewId, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyTo(layout);


    }
}
