package fr.uga.julioju.taquingame;

import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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

    // `String barrierType, could be deduced thanks `int viewId', `int
    // toViewId1' and `toViewId2'. But I prefer use "barrierType", it's
    // clearer.
    private void createConstraintSetToSquare(ConstraintLayout layout,
            int viewId, int toBarrierHorizontal, int toBarrierVertical) {

        int constraintDirectionHorizontal = ConstraintSet.TOP;
        int constraintDirectionVertical = ConstraintSet.LEFT;

        int toConstraintDirectionHorizontal = ConstraintSet.BOTTOM;
        int toConstraintDirectionVertical = ConstraintSet.RIGHT;

        if (toBarrierHorizontal == ConstraintSet.PARENT_ID) {
            // The barrier horizontal is ConstraintSet.PARENT_ID
            toConstraintDirectionHorizontal = ConstraintSet.TOP;
        }
        else if (toBarrierVertical == ConstraintSet.PARENT_ID) {
            // The barrier vertical is ConstraintSet.PARENT_ID
            toConstraintDirectionVertical = ConstraintSet.LEFT;
        }
        else if (toBarrierHorizontal == ConstraintSet.PARENT_ID
                && toBarrierVertical == ConstraintSet.PARENT_ID) {
            // Only for the square L1 / R1.
            toConstraintDirectionHorizontal = ConstraintSet.TOP;
            toConstraintDirectionVertical = ConstraintSet.LEFT;
        }

        // ConstraintSet for the square at fist column, last row
        // e.g in xml
        // app:layout_constraintTop_toBottomOf="@+id/barrier4"
        // app:layout_constraintLeft_toLeftOf="parent"
        // Official doc:
        // https://developer.android.com/reference/android/support/constraint/ConstraintSet#setmargin
        // Very short model:
        // https://stackoverflow.com/a/45264822
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(viewId, constraintDirectionHorizontal,
                toBarrierHorizontal, toConstraintDirectionHorizontal, 0);
        set.connect(viewId, constraintDirectionVertical,
                toBarrierVertical, toConstraintDirectionVertical, 0);
        set.applyTo(layout);
    }

    private void createNewRow(ConstraintLayout layout) {

        // Create square at first column, last row
        Square firstColumnLastRow = new Square(this, layout,
                "L5 L5 L5 L5 L5 L5 L5 L5 L5*");
        // Create square at second column, last row
        Square secondColumnLastRow = new Square(this, layout,
                "R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5 R5*");
        Square lastColumnFirstRow = new Square(this, layout, "RR1");
        Square lastColumnSecondRow = new Square(this, layout, "RR2");
        Square lastColumnLastRow = new Square(this, layout, "RR3");

        // ====
        // Create last row
        // ====

        // append the square ID to existing Barrier above this new square
        existingBarrierAddNewView((Barrier)findViewById( R.id.barrierVertical),
                firstColumnLastRow.getViewId());

        // create new horizontal Barrier
        int newHorizontalBarrier = this.newBarrier(layout, Barrier.BOTTOM,
                        new int[] {R.id.L2, R.id.R2});

        // ConstraintSet for the square at fist column, last row
        this.createConstraintSetToSquare(layout, firstColumnLastRow.getViewId(),
                newHorizontalBarrier, ConstraintSet.PARENT_ID);

        // Constraints for square at second column, last row
        this.createConstraintSetToSquare(layout, secondColumnLastRow.getViewId(),
                newHorizontalBarrier, R.id.barrierVertical);

        // ====
        // Create square at last column, first row
        // ====

        // append the square ID to existing Barrier above this new square
        existingBarrierAddNewView((Barrier)findViewById(newHorizontalBarrier),
                firstColumnLastRow.getViewId());

        // create new vertical Barrier
        int newVerticalBarrier = this.newBarrier(layout, Barrier.RIGHT,
                        new int[] {R.id.R1, R.id.R2,
                            secondColumnLastRow.getViewId()});

        // ConstraintSet lastColumnFirstRow.getViewId()
        this.createConstraintSetToSquare(layout, lastColumnFirstRow.getViewId(),
                ConstraintSet.PARENT_ID, newVerticalBarrier);

        // ConstraintSet lastColumnSecondRow.getViewId()
        this.createConstraintSetToSquare(layout,
                lastColumnSecondRow.getViewId(), lastColumnFirstRow.getViewId(),
                newVerticalBarrier);

        // ConstraintSet lastColumnLastRow.getViewId()
        this.createConstraintSetToSquare(layout, lastColumnLastRow.getViewId(),
                lastColumnSecondRow.getViewId(), newVerticalBarrier);
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
