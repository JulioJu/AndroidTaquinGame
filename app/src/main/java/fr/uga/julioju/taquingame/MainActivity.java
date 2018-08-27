package fr.uga.julioju.taquingame;

import android.os.Bundle;
import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/** Build the Main Activity, it contains a Grid constructed thanks a
  * ConstraintLayout (better than Grid View).
  * The goal of the game is than <code>ArrayList<\Integer> unorderedList</code>
  * become ordered.
  * Each <code>Square</code> of the <code>squareArray</code> doesn't move:
  * only its content changes.
  */
public class MainActivity extends AppCompatActivity {

    /** Do not use GridLayout */
    private ConstraintLayout layout;

    /** Length of the grid. SHOULD NOT BE < 2 */
    private int gridLength;

    /** Number of square of the grid (this.gridLength * this.gridLength) */
    private int gridNumberOfSquares;

    /** Square of the Grid */
    private ArrayList<Square> squareArray;

    /** The goal of the game is to ordered unorderedList */
    private ArrayList<Integer> unorderedList;

    /** Create barrier.
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
    private int newBarrier(int barrierDirection, int[] referenceIds) {
        Barrier barrier = new Barrier(this);
        int barrierId = View.generateViewId();
        barrier.setId(barrierId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(0, 0);
        barrier.setLayoutParams(layoutParamsWrap);
        barrier.setType(barrierDirection);
        barrier.setReferencedIds(referenceIds);
        this.layout.addView(barrier);
        return barrierId;
    }

    /** Create Array of all vertical OR horizontal
      * android.support.constraint.Barrier
      * @param barrierDirection direction of Barrier.
      *     Either Barrier.BOTTOM (for horizontal Barrier)
      *     or Barrier.RIGHT (for vertical Barrier)
      * @return array of reference ids of all barriers created.
      */
    private int[] createBarrier(int barrierDirection) {
        // https://en.wikipedia.org/wiki/Defensive_programming
        // if (barrierDirection != Barrier.BOTTOM ||
        //         barrierDirection != Barrier.RIGHT) {
        //     throw new IllegalArgumentException("Argument should not be"
        //             + barrierDirection + ". It should be " + Barrier.BOTTOM
        //             + " or " + Barrier.RIGHT + ".");
        // }
        int[] barriersArray = new int[this.gridLength - 1];
        // Loop that populate array above
        for (int barriersArrayIndex = 0 ;
                barriersArrayIndex < barriersArray.length ;
                barriersArrayIndex++) {
            // Number of the first square that start the row / column
            int[] barrierReferencedIds = new int[this.gridLength];
            int squareArrayIndex;
            squareArrayIndex = barrierDirection == Barrier.BOTTOM
                ? barriersArrayIndex * this.gridLength
                : barriersArrayIndex;
            // Loop that populate array above
            for (int barrierReferencedIdsIndex = 0 ;
                    barrierReferencedIdsIndex < barrierReferencedIds.length ;
                    barrierReferencedIdsIndex++) {
                barrierReferencedIds[barrierReferencedIdsIndex] =
                    this.squareArray.get(squareArrayIndex).getId();
                squareArrayIndex = barrierDirection == Barrier.BOTTOM
                    ?   squareArrayIndex + 1
                    // either barrierDirection == Barrier.RIGHT
                    : squareArrayIndex + this.gridLength;
            }
            barriersArray[barriersArrayIndex] =
                this.newBarrier(barrierDirection, barrierReferencedIds);
        }
        return barriersArray;
    }

    /** Print Barrier information.
      * 1) Print the index of the barrier in its array
      * 2) Print for each item of Barrier.getReferencedIds() its corresponding
      *     index item in this.squareArray.
      * 3) Print the index in this.squareArray of each Square that have a
      *     ConstraintSet.connect with the Barrier.
      */
    private void printBarrierInfo(String barrierDirection, int toBarrierId,
            int barriersArrayIndex, StringBuilder constraintSetConnect) {
        Barrier barrier =  super.findViewById(toBarrierId);
        int[] barrierReferencedIdsArray = barrier.getReferencedIds();
        StringBuilder barrierReferencedIds_stringArrayIndex =
            new StringBuilder(barrierReferencedIdsArray.length * 2);
        for (int aBarrierReferencedIdsArray : barrierReferencedIdsArray) {
            barrierReferencedIds_stringArrayIndex
                    .append(((Square)
                                super.findViewById(aBarrierReferencedIdsArray))
                            .getSquareArrayIndex())
                    .append(", ");
        }
        android.util.Log.i(barrierDirection + " Barrier " + barriersArrayIndex,
                barrierDirection + "Barrier n°" + barriersArrayIndex +
                ". Barrier.getReferencedIds() are Square with index in " +
                " this.squareArray: " + barrierReferencedIds_stringArrayIndex +
                "\nThis Barrier have ConstraintSet.connect() to Square" +
                " with index in this.squareArray: " +
                String.valueOf(constraintSetConnect));
    }

    /**
      * ConstraintSet.connect() between each barrier of
      * horizontalBarriersArray[] and their Square
      * @param set should be already instantiated
      * @param horizontalBarriersArray Array that contains all horizontal
      *             barriers of the grid
      */
    private void constraintSetBetweenHorizontalBarriersAndTheirSquare(
            ConstraintSet set, int[] horizontalBarriersArray) {
        int constraintDirectionHorizontal = ConstraintSet.TOP;
        int toBarrierHorizontalId ;
        int toConstraintDirectionHorizontal ;
        // ConstraintSet.connect between:
        // 1) this.squareArray.get(0).getId(),
        //   then this.squareArray.get(0+1).getId(),
        //   then this.squareArray.get(0+1+1),
        //   then etc.
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierHorizontalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionHorizontal = ConstraintSet.TOP;
        for (int squareArrayIndex = 0 ;
                squareArrayIndex <= this.gridLength - 1  ;
                squareArrayIndex++) {
            set.connect(this.squareArray.get(squareArrayIndex).getId(),
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
            // 1) this.squareArray.get(this.gridLength).getId(),
            //      then this.squareArray.get(this.gridLength + 1).getId(),
            //      then this.squareArray.get(this.gridLength + 1 + 1).getId(),
            //      then etc.
            // 2) AND horizontalBarriersArray[barriersArrayIndex]
            StringBuilder constraintSetConnect =
                new StringBuilder(this.gridLength);
            for (int squareArrayIndex =
                        (barriersArrayIndex +1) * this.gridLength ;
                    squareArrayIndex <=
                        (barriersArrayIndex + 1) * this.gridLength
                        + (this.gridLength - 1) ;
                    squareArrayIndex++
                ) {
                set.connect(this.squareArray.get(squareArrayIndex).getId(),
                        constraintDirectionHorizontal,
                        toBarrierHorizontalId, toConstraintDirectionHorizontal,
                        0);
                constraintSetConnect.append(squareArrayIndex).append(", ");
            }
            printBarrierInfo("Horizontal", toBarrierHorizontalId,
                    barriersArrayIndex, constraintSetConnect);
        }
    }

    /**
      * ConstraintSet.connect() between each barrier of
      * verticalBarrierArray[] and their Square
      * @param set should be already instantiated
      * @param verticalBarrierArray Array that contains all vertical
      *             barriers of the grid
      */
    private void constraintSetBetweenVerticalBarriersAndTheirSquare(
            ConstraintSet set, int[] verticalBarrierArray) {
        int constraintDirectionVertical = ConstraintSet.LEFT;
        int toBarrierVerticalId ;
        int toConstraintDirectionVertical ;

        // ConstraintSet.connect between:
        // 1) this.squareArray.get(0).getId(),
        //   then this.squareArray.get(this.gridLength).getId(),
        //   then this.squareArray.get(this.gridLength + this.gridLength),
        //   then etc.
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierVerticalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionVertical = ConstraintSet.LEFT;
        for (int squareArrayIndex = 0 ;
                squareArrayIndex <= this.gridNumberOfSquares - this.gridLength ;
                squareArrayIndex = squareArrayIndex + this.gridLength) {
            set.connect(this.squareArray.get(squareArrayIndex).getId(),
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
            // 1) this.squareArray.get(barriersArrayIndex + 1).getId(),
            //      then this.squareArray.get(barriersArrayIndex + 1
            //          + this.gridLength).getId(),
            //      then this.squareArray.get(barriersArrayIndex + 1
            //          + this.gridLength + this.gridLength).getId(),
            //      then etc.
            // 2) AND verticalBarrierArray[barriersArrayIndex]
            StringBuilder constraintSetConnect =
                new StringBuilder(this.gridLength);
            for (int squareArrayIndex = barriersArrayIndex + 1 ;
                    squareArrayIndex <= this.gridNumberOfSquares
                            - (this.gridLength - (barriersArrayIndex + 1 )) ;
                    squareArrayIndex = squareArrayIndex + this.gridLength
                ) {
                set.connect(this.squareArray.get(squareArrayIndex).getId(),
                        constraintDirectionVertical,
                        toBarrierVerticalId, toConstraintDirectionVertical,
                        0);
                constraintSetConnect .append(squareArrayIndex) .append(", ");
            }
            printBarrierInfo("Vertical", toBarrierVerticalId,
                    barriersArrayIndex, constraintSetConnect);
        }

    }

    /**
      * Create a grid like below.
      * If this.gridLength < 3, draw with letters and "."
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
    private void createGrid() {

        // ============================
        // ============================
        // Create Square of the grid
        // ============================
        // ============================

        // Loop that populate array above
        for (int squareArrayIndex = 0 ;
                squareArrayIndex  < this.gridNumberOfSquares ;
                squareArrayIndex++) {
            // squareText is the String text displayed in the Square.
            // Square.getView().getText() of a row could be
            //      longer than Square.getView().getText() of the row above.
            // We could see the interest of the Barrier
            // object : Barrier is aligned on the longer String)
            int unorderedListIndex = this.unorderedList.get(squareArrayIndex);
            String squareText = String.valueOf(unorderedListIndex);
            Square square = new Square (this, this.layout, squareArrayIndex,
                    unorderedListIndex, squareText);
            this.squareArray.add(square);

            // https://developer.android.com/guide/topics/ui/ui-events
            square.setOnClickListener(new SquareOnClickListener());

        }

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
        //     .getReferencedIds() == new int[] {this.squareArray.get(0),
        //         this.squareArray.get(1), this.squareArray.get(2),
        //              this.squareArray.get(3)})
        // (Barrier super.findViewById(horizontalBarriersArray[1]))
        //     .getReferencedIds() == new int[] {this.squareArray.get(1),
        //         this.squareArray.get(2), this.squareArray.get(2),
        //              this.squareArray.get(3)})
        // etc.
        int[] horizontalBarriersArray = this.createBarrier(Barrier.BOTTOM);

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
        //     .getReferencedIds() == new int[] {this.squareArray.get(0),
        //         this.squareArray.get(4), this.squareArray.get(8),
        //              this.squareArray.get(12)})
        // (Barrier super.findViewById(verticalBarrierArray[2]))
        //     .getReferencedIds() == new int[] {this.squareArray.get(1),
        //         this.squareArray.get(5), this.squareArray.get(2),
        //              this.squareArray.get(13)})
        // etc.
        //
        int[] verticalBarrierArray = this.createBarrier(Barrier.RIGHT);

        // ConstraintSet
        // ============================================
        // ======
        // ======

        ConstraintSet set = new ConstraintSet();
        set.clone(this.layout);

        // ConstraintSet for horizontal barriers and squares
        // ======
        this.constraintSetBetweenHorizontalBarriersAndTheirSquare(set,
                horizontalBarriersArray);

        // ConstraintSet for vertical barriers and squares
        // ======
        this.constraintSetBetweenVerticalBarriersAndTheirSquare(set,
                verticalBarrierArray);

        // Apply to layout
        // =========
        set.applyTo(this.layout);

    }

    /** Create a list unordered */
    private ArrayList<Integer> populateUnorderedList() {
        // builderList is a list like {0: 0, 1: 1, 2: 2, etc.}
        ArrayList<Integer> builderList = new ArrayList<>(gridNumberOfSquares);
        for (int index = 0 ; index <= gridNumberOfSquares ; index++) {
            builderList.add(index);
        }
        ArrayList<Integer> unorderedList = new ArrayList<>(this.gridNumberOfSquares);
        for (int max = this.gridNumberOfSquares - 1 ; max > 0 ; max--) {
            // Generate a random number between [0; max]
            // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
            int randomNum = ThreadLocalRandom.current().nextInt(0, max);
            unorderedList.add(builderList.get(randomNum));
            builderList.remove(randomNum);
        }
        unorderedList.add(builderList.get(0));
        return unorderedList;
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        // Retrieve layout
        this.layout = super.findViewById(R.id.constrained);

        this.gridLength = 10;

        this.gridNumberOfSquares = this.gridLength * this.gridLength;

        this.squareArray = new ArrayList<>(this.gridNumberOfSquares);

        this.unorderedList = this.populateUnorderedList();

        // Complete example:
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        this.createGrid();
    }

}
