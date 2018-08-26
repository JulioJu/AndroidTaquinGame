package fr.uga.julioju.taquingame;

import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/** Build the Main Activity
  * See logs for details
  */
public class MainActivity extends AppCompatActivity {

    /** Create barrier.
      * @param layout Parent layout
      * @param barrierDirection in XML it's for instance
      *     `app:barrierDirection="bottom"`. Value could be
      *     either `Barrier.BOTTOM' (for horizontal Barrier)
      *     or `Barrier.RIGHT' (for vertical Barrier)
      * @param referenceIds array of referenced view ids that constraint
      *     the barrier. In XML, it's: `app:constraint_referenced_ids="L1, R1"'
      * @return reference id of the Barrier created. The barrier created is
      * e.g.
      * <pre>
      * <android.support.constraint.Barrier
      *     android:id="@+id/barrier1"
      *     android:layout_width="0dp"
      *     android:layout_height="0dp"
      *     app:barrierDirection="bottom"
      *     app:constraint_referenced_ids="L1, R1" />
      * </pre>
      */
    private int newBarrier(ConstraintLayout layout, int barrierDirection,
            int[] referenceIds) {
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

    /** Create Array of all vertical OR horizontal
      * android.support.constraint.Barrier
      * @param layout global ConstraintLayout
      * @param boardLength number of Barrier in this Direction
      * @param barrierDirection direction of Barrier.
      *     Either Barrier.BOTTOM (for horizontal Barrier)
      *     or Barrier.RIGHT (for vertical Barrier)
      * @param squareArray Array of Square
      * @return array of reference ids of all barriers created.
      */
    private int[] createBarrier(ConstraintLayout layout, int boardLength,
            int barrierDirection, Square[] squareArray) {
        // https://en.wikipedia.org/wiki/Defensive_programming
        // if (barrierDirection != Barrier.BOTTOM ||
        //         barrierDirection != Barrier.RIGHT) {
        //     throw new IllegalArgumentException("Argument should not be"
        //             + barrierDirection + ". It should be " + Barrier.BOTTOM
        //             + " or " + Barrier.RIGHT + ".");
        // }
        int[] barriersArray = new int[boardLength - 1];
        // Loop that populate array above
        for (int barriersArrayIndex = 0 ;
                barriersArrayIndex < barriersArray.length ;
                barriersArrayIndex++) {
            // Number of the first square that start the row / column
            int[] barrierReferencedIds = new int[boardLength];
            int squareArrayIndex;
            squareArrayIndex = barrierDirection == Barrier.BOTTOM
                ? barriersArrayIndex * boardLength
                : barriersArrayIndex;
            // Loop that populate array above
            for (int barrierReferencedIdsIndex = 0 ;
                    barrierReferencedIdsIndex < barrierReferencedIds.length ;
                    barrierReferencedIdsIndex++) {
                barrierReferencedIds[barrierReferencedIdsIndex] =
                    squareArray[squareArrayIndex].getViewId();
                squareArrayIndex = barrierDirection == Barrier.BOTTOM
                    ?   squareArrayIndex + 1
                    // either barrierDirection == Barrier.RIGHT
                    : squareArrayIndex + boardLength;
            }
            barriersArray[barriersArrayIndex] =
                this.newBarrier(layout, barrierDirection,
                        barrierReferencedIds);
        }
        return barriersArray;
    }

    /**
      * Create a board like below.
      * If boardLength < 3, draw with letters and "."
      * Else draw with numbers.
      * <pre>
      * HBP _ HB0_HB1 _HB2 _ VBP
      *   | 0  | 1 | 2  |  3
      *     _    _   _     _ VB0
      *   | 4  | 5 |  6 |  7
      *     _    _   _     _ VB1
      *   | 8  | 9 |  10|  11
      *    _     _    _    _ VB2
      *   | 12 | 13|  14|  15
      * HB0: Horizontal Barrier P (ConstraintSet.PARENT_ID)
      * HB1: Horizontal Barrier 0 (android.support.constraint.Barrier)
      * etc
      * HB0: Vertical Barrier P (ConstraintSet.PARENT_ID)
      * HB1: Vertical Barrier 1 (android.support.constraint.Barrier)
      * etc
      * Note: Barriers are aligns on the longer Square.
      * VB0 contains constraint_referenced_ids of Square 0, 1, 2, 3.
      * VB1 contains constraint_referenced_ids of Square 4, 5, 6, 7.
      * etc.
      * </pre>
      */
    private void createBoard(ConstraintLayout layout) {

        // Should not be < 2
        int boardLength = 10;

        // ============================
        // ============================
        // Create Square of the board
        // ============================
        // ============================
        int boardNumberOfSquares = boardLength * boardLength;
        Square[] squareArray = new Square[boardNumberOfSquares];
        // Loop that populate array above
        for (int squareArrayIndex = 0 ;
                squareArrayIndex  < boardNumberOfSquares ;
                squareArrayIndex++) {
            // squareText is the String text displayed in the Square.
            // Square.getView().getText() of a row could be
            //      longer than Square.getView().getText() of the row above.
            // We could see the interest of the Barrier
            // object : Barrier is aligned on the longer String)
            String squareText = String.valueOf(squareArrayIndex);
            // Create the Square
            squareArray[squareArrayIndex] =
                new Square(this, layout, squareText);
                }


        // TODO factorize Horizontal and Vertical Barriers

        // ===
        // Create Horizontal Barriers
        // ===
        // ============================
        // Create barriers like in xml:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrier1"
        //     android:layout_width="0dp"
        //     android:layout_height="0dp"
        //     app:barrierDirection="bottom"
        //     app:constraint_referenced_ids="L1, R1" />
        // ============================

        // Array that contains all horizontal `Barrier'
        // (Barrier super.findViewById(horizontalBarriersArray[0]))
        //     .getReferencedIds() == new int[] {squareArray[0],
        //         squareArray[1], squareArray[2], squareArray[3]})
        // (Barrier super.findViewById(horizontalBarriersArray[1]))
        //     .getReferencedIds() == new int[] {squareArray[1],
        //         squareArray[2], squareArray[2], squareArray[3]})
        // etc.
        int[] horizontalBarriersArray =
            this.createBarrier(layout, boardLength, Barrier.BOTTOM ,
                    squareArray);

        // ===
        // Create Vertical Barriers
        // ===
        // ============================
        // Create barriers like in xml:
        // <android.support.constraint.Barrier
        //     android:id="@+id/barrierVertical"
        //     android:layout_width="0dp"
        //     android:layout_height="0dp"
        //     app:barrierDirection="right"
        //     app:constraint_referenced_ids="L1, L2" />
        // ==============================

        // Array that contains all vertical `Barrier'
        // (Barrier super.findViewById(verticalBarrierArray[0]))
        //     .getReferencedIds() == new int[] {squareArray[0],
        //         squareArray[4], squareArray[8], squareArray[12]})
        // (Barrier super.findViewById(verticalBarrierArray[2]))
        //     .getReferencedIds() == new int[] {squareArray[1],
        //         squareArray[5], squareArray[2], squareArray[13]})
        // etc.
        //
        int[] verticalBarrierArray =
            this.createBarrier(layout, boardLength, Barrier.RIGHT, squareArray);

        // ConstraintSet
        // ============================================
        // ======
        // ======

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // ConstraintSet for horizontal barriers and squares
        // ======

        int constraintDirectionHorizontal = ConstraintSet.TOP;
        int toBarrierHorizontalId ;
        int toConstraintDirectionHorizontal ;

        // ConstraintSet.connect between:
        // 1) squareArray[0].getViewId(),
        //   then squareArray[0+1].getViewId(),
        //   then squareArrayIndex[0+1+1],
        //   then etc.
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierHorizontalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionHorizontal = ConstraintSet.TOP;
        for (int squareArrayIndex = 0 ;
                squareArrayIndex <= boardLength - 1  ;
                squareArrayIndex++) {
            set.connect(squareArray[squareArrayIndex].getViewId(),
                    constraintDirectionHorizontal,
                    toBarrierHorizontalId, toConstraintDirectionHorizontal,
                    0);
        }

        // For android.support.constraint.Barrier
        // As Barrier height = 0, could be also:
        //          `toConstraintDirectionHorizontal = ConstraintSet.LEFT'
        toConstraintDirectionHorizontal = ConstraintSet.BOTTOM;
        for (int barriersArrayIndex = 0 ;
                barriersArrayIndex < horizontalBarriersArray.length ;
                barriersArrayIndex++) {
            toBarrierHorizontalId = horizontalBarriersArray[barriersArrayIndex];
            // ConstraintSet.connect between
            // 1) squareArray[boardLength].getViewId(),
            //      then squareArray[boardLength + 1].getViewId(),
            //      then squareArrayIndex[boardLength + 1 + 1].getViewId(),
            //      then etc.
            // 2) AND horizontalBarriersArray[barriersArrayIndex]
            int referencedIdsIntArray[] = ((Barrier) super
                    .findViewById(toBarrierHorizontalId)).getReferencedIds();
            StringBuilder referencedIdsStringText =
                new StringBuilder(referencedIdsIntArray.length * 2);
            for (int referencedIdsIntArrayIndex : referencedIdsIntArray) {
                CharSequence textString = ((TextView)super
                        .findViewById(referencedIdsIntArrayIndex))
                        .getText();
                referencedIdsStringText.append(textString)
                .append(", ");
            }
            android.util.Log.i("Horizontal Barrier",
                    "Horizontal Barrier n°" + barriersArrayIndex +
                    ". Barrier.getReferencedIds() are square with number "
                    + referencedIdsStringText.toString());
            StringBuilder constraintSetConnect = new StringBuilder(boardLength);
            for (int squareArrayIndex = (barriersArrayIndex +1) * boardLength ;
                    squareArrayIndex <= (barriersArrayIndex + 1) * boardLength
                        + (boardLength - 1) ;
                    squareArrayIndex++
                ) {
                set.connect(squareArray[squareArrayIndex].getViewId(),
                        constraintDirectionHorizontal,
                        toBarrierHorizontalId, toConstraintDirectionHorizontal,
                        0);
                constraintSetConnect
                    .append(squareArrayIndex)
                    .append(", ");
            }
            android.util.Log.i("Horizontal Barrier",
                    "ConstraintSet.connect to Square with squareArrayIndex:" +
                            String.valueOf(constraintSetConnect));
        }

        // ConstraintSet for vertical barriers and squares
        // ======
        int constraintDirectionVertical = ConstraintSet.LEFT;
        int toBarrierVerticalId ;
        int toConstraintDirectionVertical ;

        // ConstraintSet.connect between:
        // 1) squareArray[0].getViewId(),
        //   then squareArray[boardLength].getViewId(),
        //   then squareArrayIndex[boardLength + boardLength],
        //   then etc.
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierVerticalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionVertical = ConstraintSet.LEFT;
        for (int squareArrayIndex = 0 ;
                squareArrayIndex <= boardNumberOfSquares - boardLength ;
                squareArrayIndex = squareArrayIndex + boardLength) {
            set.connect(squareArray[squareArrayIndex].getViewId(),
                    constraintDirectionVertical,
                    toBarrierVerticalId, toConstraintDirectionVertical,
                    0);
        }

        // For android.support.constraint.Barrier
        // As Barrier weight = 0, could be also
        //          ` toConstraintDirectionVertical = ConstraintSet.LEFT'
        toConstraintDirectionVertical = ConstraintSet.RIGHT;
        for (int barriersArrayIndex = 0 ;
                barriersArrayIndex < verticalBarrierArray.length ;
                barriersArrayIndex++) {
            toBarrierVerticalId = verticalBarrierArray[barriersArrayIndex];
            // ConstraintSet.connect between
            // 1) squareArray[barriersArrayIndex + 1].getViewId(),
            //      then squareArray[barriersArrayIndex + 1
            //          + boardLength].getViewId(),
            //      then squareArrayIndex[barriersArrayIndex + 1 + boardLength
            //          + boardLength].getViewId(),
            //      then etc.
            // 2) AND verticalBarrierArray[barriersArrayIndex]
            int referencedIdsIntArray[] = ((Barrier) super
                    .findViewById(toBarrierVerticalId)).getReferencedIds();
            StringBuilder referencedIdsStringText =
                new StringBuilder(referencedIdsIntArray.length * 2);
            for (int referencedIdsIntArrayIndex : referencedIdsIntArray) {
                CharSequence textString = ((TextView)super
                        .findViewById(referencedIdsIntArrayIndex))
                        .getText();
                referencedIdsStringText.append(textString)
                .append(", ");
            }
            android.util.Log.i("Vertical Barrier",
                    "Vertical Barrier n°" + barriersArrayIndex +
                    ". Barrier.getReferencedIds() are square with number "
                    + referencedIdsStringText.toString());
            StringBuilder constraintSetConnect = new StringBuilder(boardLength);
            for (int squareArrayIndex = barriersArrayIndex + 1 ;
                    squareArrayIndex <= boardNumberOfSquares
                            - (boardLength - (barriersArrayIndex + 1 )) ;
                    squareArrayIndex = squareArrayIndex + boardLength
                ) {
                set.connect(squareArray[squareArrayIndex].getViewId(),
                        constraintDirectionVertical,
                        toBarrierVerticalId, toConstraintDirectionVertical,
                        0);
                constraintSetConnect
                    .append(squareArrayIndex)
                    .append(", ");
            }
            android.util.Log.i("Vertical Barrier",
                    "ConstraintSet.connect to Square with squareArrayIndex:" +
                            String.valueOf(constraintSetConnect));
        }

        // Apply to layout
        // =========
        set.applyTo(layout);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve layout
        ConstraintLayout layout = super.findViewById(R.id.constrained);

        // Complete example:
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        createBoard(layout);
    }

}
