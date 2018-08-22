package fr.uga.julioju.taquingame;

import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int createTextViewWithoutConstraints(ConstraintLayout layout,
            String androidText) {
        // Create View
        TextView textView = new TextView(this);

        // Create ID
        // https://stackoverflow.com/questions/1714297/android-textView-setidint-id-programmatically-how-to-avoid-id-conflicts
        int textViewId = View.generateViewId();
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
        textView.setText(androidText);
        // textView.setTextSize(15);

        // Add TextView to layout
        // SHOULD BE HERE, BEFORE ConstraintSet
        layout.addView(textView);

        return textViewId;
    }

    private void existingVerticalBarrierAddNewRow(int textViewId) {
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
    }

    private int newHorizontalBarrier(ConstraintLayout layout) {
        // Create the following Horizontal Barrier:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrier4"
        //     android:layout_width="0dp"
        //     android:layout_height="0dp"
        //     app:barrierDirection="bottom"
        //     app:constraint_referenced_ids="L4, R4" />
        Barrier newHorizontalBarrier = new Barrier(this);
        int newHorizontalBarrierId = View.generateViewId();
        newHorizontalBarrier.setId(newHorizontalBarrierId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(0, 0);
        newHorizontalBarrier.setLayoutParams(layoutParamsWrap);
        newHorizontalBarrier.setType(Barrier.BOTTOM);
        newHorizontalBarrier.setReferencedIds(new int[] {R.id.L4, R.id.R4});
        layout.addView(newHorizontalBarrier);

        return newHorizontalBarrierId;
    }

    private void createNewRow(ConstraintLayout layout) {

        // ==== Create first column ====

        int firstColumnId = createTextViewWithoutConstraints(layout,
                "L5 L5 L5 L5 L5 L5 L5 L5 L5*");

        existingVerticalBarrierAddNewRow(firstColumnId);

        int newHorizontalBarrierId =
                this.newHorizontalBarrier(layout);

        // ConstraintSet
        // e.g in xml
        // app:layout_constraintLeft_toLeftOf="parent"
        // app:layout_constraintTop_toBottomOf="@+id/barrier4"
        // Official doc:
        // https://developer.android.com/reference/android/support/constraint/ConstraintSet#setmargin
        // Very short model:
        // https://stackoverflow.com/a/45264822
        ConstraintSet setFirstCol = new ConstraintSet();
        setFirstCol.clone(layout);
        setFirstCol.connect(firstColumnId, ConstraintSet.LEFT,
                  ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        setFirstCol.connect(firstColumnId, ConstraintSet.TOP,
                newHorizontalBarrierId, ConstraintSet.BOTTOM, 0);
        setFirstCol.applyTo(layout);

        // ===== Create second column =====

        int secColId = createTextViewWithoutConstraints(layout,
                "R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5*");
        ConstraintSet setSecCol = new ConstraintSet();
        setSecCol.clone(layout);
        setSecCol.connect(secColId, ConstraintSet.LEFT,
                  R.id.barrierVertical, ConstraintSet.RIGHT, 0);
        setSecCol.connect(secColId, ConstraintSet.TOP,
                newHorizontalBarrierId, ConstraintSet.BOTTOM, 0);
        setSecCol.applyTo(layout);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve layout
        ConstraintLayout layout = findViewById(R.id.constrained);

        // Complete example:
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        createNewRow(layout);
    }

}
