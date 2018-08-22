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
        TextView view = new TextView(this);

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

        return viewId;
    }

    private void existingBarrierAddNewView(Barrier verticalBarrier,
            int viewId) {
        // *** BARRIERS ****

        // Add the new element to the existing verticalBarrier
        // Change the vertical barrier with id `R.id.barrierVertical'
        // defined at ../../../../../res/layout/activity_main.xml
        // like:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrierVertical"
        //     ........
        //     app:constraint_referenced_ids="L1, L2, …, viewId" />
        int barrierReferencedIdsOld[] = verticalBarrier.getReferencedIds();
        int barrierReferencedIds[] =
                Arrays.copyOf(barrierReferencedIdsOld,
                        barrierReferencedIdsOld.length + 1);
        barrierReferencedIds[barrierReferencedIds.length - 1] = viewId;
        verticalBarrier.setReferencedIds(barrierReferencedIds);
    }

    private int newBarrier(ConstraintLayout layout, int barrierDirection,
            int[] referenceIds) {
        // Create the following Horizontal Barrier:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrier4"
        //     android:layout_width="0dp"
        //     android:layout_height="0dp"
        //     app:barrierDirection="bottom"
        //     app:constraint_referenced_ids="L2, R2" />
        Barrier barrier = new Barrier(this);
        int barrierId = View.generateViewId();
        barrier.setId(barrierId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(0, 0);
        barrier.setLayoutParams(layoutParamsWrap);
        barrier.setType(barrierDirection);
        barrier.setReferencedIds(referenceIds);
        layout.addView(barrier);

        return barrierId;
    }

    private void createNewRow(ConstraintLayout layout) {

        // TODO factorize more

        // Create square at first column, last row
        int firstColumnLastRowId = createTextViewWithoutConstraints(layout,
                "L5 L5 L5 L5 L5 L5 L5 L5 L5*");
        // Create square at second column, last row
        int secondColumnLastRowId = createTextViewWithoutConstraints(layout,
                "R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5*");
        int lastColumnFirstRowId = createTextViewWithoutConstraints(layout,
                "RR1");
        int lastColumnSecondRowId = createTextViewWithoutConstraints(layout,
                "RR2");
        int lastColumnLastRowId = createTextViewWithoutConstraints(layout,
                "RR3");

        // ====
        // Create last row
        // ====

        // append the square ID to existing Barrier above this new square
        existingBarrierAddNewView((Barrier)findViewById( R.id.barrierVertical),
                firstColumnLastRowId);

        // create new horizontal Barrier
        int newHorizontalBarrier = this.newBarrier(layout, Barrier.BOTTOM,
                        new int[] {R.id.L2, R.id.R2});

        // ConstraintSet for the square at fist column, last row
        // e.g in xml
        // app:layout_constraintLeft_toLeftOf="parent"
        // app:layout_constraintTop_toBottomOf="@+id/barrier4"
        // Official doc:
        // https://developer.android.com/reference/android/support/constraint/ConstraintSet#setmargin
        // Very short model:
        // https://stackoverflow.com/a/45264822
        ConstraintSet setFirstColumnLastRow = new ConstraintSet();
        setFirstColumnLastRow.clone(layout);
        setFirstColumnLastRow.connect(firstColumnLastRowId, ConstraintSet.LEFT,
                  ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        setFirstColumnLastRow.connect(firstColumnLastRowId, ConstraintSet.TOP,
                newHorizontalBarrier, ConstraintSet.BOTTOM, 0);
        setFirstColumnLastRow.applyTo(layout);

        // Constraints for square at second column, last row
        ConstraintSet setSecondColumnLastRow = new ConstraintSet();
        setSecondColumnLastRow.clone(layout);
        setSecondColumnLastRow.connect(secondColumnLastRowId,
                ConstraintSet.LEFT,
                R.id.barrierVertical, ConstraintSet.RIGHT, 0);
        setSecondColumnLastRow.connect(secondColumnLastRowId, ConstraintSet.TOP,
                newHorizontalBarrier, ConstraintSet.BOTTOM, 0);
        setSecondColumnLastRow.applyTo(layout);

        // ====
        // Create square at last column, first row
        // ====

        // append the square ID to existing Barrier above this new square
        existingBarrierAddNewView((Barrier)findViewById(newHorizontalBarrier),
                firstColumnLastRowId);

        // create new vertical Barrier
        int newVerticalBarrier = this.newBarrier(layout, Barrier.RIGHT,
                        new int[] {R.id.R1, R.id.R2, secondColumnLastRowId});

        // ConstraintSet lastColumnFirstRowId
        ConstraintSet setLastColumnFirstRow = new ConstraintSet();
        setLastColumnFirstRow.clone(layout);
        setLastColumnFirstRow.connect(lastColumnFirstRowId, ConstraintSet.LEFT,
                  newVerticalBarrier, ConstraintSet.RIGHT, 0);
        setLastColumnFirstRow.connect(lastColumnFirstRowId, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        setLastColumnFirstRow.applyTo(layout);

        // ConstraintSet lastColumnSecondRowId
        ConstraintSet setLastColumnSecondRow = new ConstraintSet();
        setLastColumnSecondRow.clone(layout);
        setLastColumnSecondRow.connect(lastColumnSecondRowId,
                ConstraintSet.LEFT, newVerticalBarrier, ConstraintSet.RIGHT, 0);
        setLastColumnSecondRow.connect(lastColumnSecondRowId, ConstraintSet.TOP,
                lastColumnFirstRowId, ConstraintSet.BOTTOM, 0);
        setLastColumnSecondRow.applyTo(layout);

        // ConstraintSet lastColumnLastRowId
        ConstraintSet setLastColumnLastRow = new ConstraintSet();
        setLastColumnLastRow.clone(layout);
        setLastColumnLastRow.connect(lastColumnLastRowId, ConstraintSet.LEFT,
                  newVerticalBarrier, ConstraintSet.RIGHT, 0);
        setLastColumnLastRow.connect(lastColumnLastRowId, ConstraintSet.TOP,
                lastColumnSecondRowId, ConstraintSet.BOTTOM, 0);
        setLastColumnLastRow.applyTo(layout);
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
