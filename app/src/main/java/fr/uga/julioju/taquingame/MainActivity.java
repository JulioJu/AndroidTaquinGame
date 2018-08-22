package fr.uga.julioju.taquingame;

import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int barriersForNewTextView(int textViewId,
            ConstraintLayout layout) {
        // *** BARRIERS ****

        // Add the new element to the existing verticalBarrier
        // Change the vertical barrier with id `R.id.barrierVertical'
        // defined at ../../../../../res/layout/activity_main.xml
        // like:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrierVertical"
        //     ........
        //     app:constraint_referenced_ids="L1, L2, L3, L4, textViewId" />
        Barrier verticalBarrier = findViewById(R.id.barrierVertical);
        int barrierReferencedIdsOld[] = verticalBarrier.getReferencedIds();
        int barrierReferencedIds[] =
                Arrays.copyOf(barrierReferencedIdsOld,
                        barrierReferencedIdsOld.length + 1);
        barrierReferencedIds[barrierReferencedIds.length - 1] = textViewId;
        verticalBarrier.setReferencedIds(barrierReferencedIds);

        // Create the following Horizontal Barrier:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrier4"
        //     android:layout_width="0dp"
        //     android:layout_height="0dp"
        //     app:barrierDirection="bottom"
        //     app:constraint_referenced_ids="L4, R4" />
        Barrier newHorizontalBarrier = new Barrier(this);
        int newHorizontalBarrierId = newHorizontalBarrier.generateViewId();
        newHorizontalBarrier.setId(newHorizontalBarrierId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(0, 0);
        newHorizontalBarrier.setType(Barrier.BOTTOM);
        newHorizontalBarrier.setReferencedIds(new int[] {R.id.L4, R.id.R4});
        layout.addView(newHorizontalBarrier);

        return newHorizontalBarrierId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Complete example:
        // ________________
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet

        // Retrieve layout
        ConstraintLayout layout = findViewById(R.id.constrained);

        // Create View
        TextView textView = new TextView(this);

        // Create ID
        // https://stackoverflow.com/questions/1714297/android-textView-setidint-id-programmatically-how-to-avoid-id-conflicts
        int textViewId = textView.generateViewId();
        textView.setId(textViewId);

        // ViewGroup.LayoutParams
        // XML attributes:
        // android:layout_height
        // android:layout_width
        // https://developer.android.com/reference/android/textView/ViewGroup.LayoutParams
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);

        // ViewGroup.MarginLayoutParams
        // e.g. of XML attributes:
        // android:layout_margin
        // https://developer.android.com/reference/android/textView/ViewGroup.MarginLayoutParams
       layoutParamsWrap.setMargins(0,0,0,0);

        // Set dimensions to textView
        textView.setLayoutParams(layoutParamsWrap);

        // Add text to textView
        textView.setText(R.string.app_name);
        textView.setTextSize(15);

        // Add TextView to layout
        layout.addView(textView);

        // ConstraintSet
        // e.g in xml
        // app:layout_constraintLeft_toLeftOf="parent"
        // app:layout_constraintTop_toBottomOf="@+id/barrier4"
        // Official doc:
        // https://developer.android.com/reference/android/support/constraint/ConstraintSet#setmargin
        // Very short model:
        // https://stackoverflow.com/a/45264822
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(textViewId, ConstraintSet.LEFT,
                  ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        set.connect(textViewId, ConstraintSet.TOP,
                this.barriersForNewTextView(textViewId, layout),
                ConstraintSet.BOTTOM, 0);
        set.applyTo(layout);

    }
}
