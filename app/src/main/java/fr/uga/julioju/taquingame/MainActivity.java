package fr.uga.julioju.taquingame;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/** Build the Main Activity, it contains a Grid constructed thanks a
  * ConstraintLayout (better than Grid View).
  * The goal of the game is than for each
  * <code>index</code> ((Square) grid.get(index)).getOrderOfTheContent() == 0
  * Each <code>Square</code> of the <code>grid</code> doesn't change:
  * only <code((Square) grid.get(index)).orderOfTheContent</code> change
  */
public class MainActivity extends AppCompatActivity {

    /** Do not use GridLayout */
    private ConstraintLayout layout;

    /** Length of the grid. SHOULD NOT BE < 2 */
    private int gridLength;

    /** Square of the Grid */
    private Square[][] grid;

    /**
      * Create an Array, with values that are aun unordered sequence of
      * number between 0 and (gridNumberOfSquares - 1).
      */
    private int[] createUnorderedSequence() {
        int gridNumberOfSquares = this.gridLength * this.gridLength;
        // builderList is a list like {0: 0, 1: 1, 2: 2, etc.}
        ArrayList<Integer> builderList = new ArrayList<>(gridNumberOfSquares);
        for (int index = 0 ; index <= gridNumberOfSquares ; index++) {
            builderList.add(index);
        }
        int unorderedArray[] = new int[gridNumberOfSquares];
        int unorderedArrayIndex = 0;
        for (int max = gridNumberOfSquares - 1 ; max > 0 ; max--) {
            // Generate a random number between [0; max]
            // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
            int randomNum = ThreadLocalRandom.current().nextInt(0, max);
            unorderedArray[unorderedArrayIndex] = builderList.get(randomNum);
            builderList.remove(randomNum);
            unorderedArrayIndex++;
        }
        unorderedArray[gridNumberOfSquares - 1] = builderList.get(0);
        return unorderedArray;
    }

    /** Create barrier.
      * @param barrierDirection in XML it's for instance
      *     <code>app:barrierDirection="bottom"</code>. Value could be
      *     either <code>Barrier.BOTTOM</code> (for horizontal Barrier)
      *     or <code>Barrier.RIGHT</code> (for vertical Barrier)
      * @param referenceIds array of referenced view ids that constraint
      *     the barrier. In XML, it's:
      *         <code>app:constraint_referenced_ids="L1, R1"</code>
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
            int row, column;
            if(barrierDirection == Barrier.BOTTOM) {
                row = 0;
                column = barriersArrayIndex;
            }
            // else barrierDirection == Barrier.RIGHT
            else {
                row =  barriersArrayIndex;
                column = 0;
            }
            // Loop that populate array above
            for (int barrierReferencedIdsIndex = 0 ;
                    barrierReferencedIdsIndex < barrierReferencedIds.length ;
                    barrierReferencedIdsIndex++) {
                barrierReferencedIds[barrierReferencedIdsIndex] =
                    this.grid[column][row].getId();
                if(barrierDirection == Barrier.BOTTOM) {
                    row++;
                }
                else {
                    column++;
                }
            }
            barriersArray[barriersArrayIndex] =
                this.newBarrier(barrierDirection, barrierReferencedIds);
        }
        return barriersArray;
    }

    /** Print Barrier information.
      * 1) Print the index of the barrier in its array
      * 2) Print for each item of Barrier.getReferencedIds() its corresponding
      *     index item in this.grid.
      * 3) Print the index in this.grid of each Square that have a
      *     ConstraintSet.connect with the Barrier.
      */
    private void printBarrierInfo(String barrierDirection, int toBarrierId,
            int barriersArrayIndex, StringBuilder constraintSetConnect) {
        Barrier barrier =  super.findViewById(toBarrierId);
        int[] barrierReferencedIdsArray = barrier.getReferencedIds();
        StringBuilder barrierReferencedIds_stringArrayIndexS =
            new StringBuilder(barrierReferencedIdsArray.length * 2);
        StringBuilder barrierReferencedIdsArray_squareOrderOfTheContent =
            new StringBuilder(barrierReferencedIdsArray.length * 2);
        for (int aBarrierReferencedIdsArray : barrierReferencedIdsArray) {
            Square square = super.findViewById(aBarrierReferencedIdsArray);
            barrierReferencedIds_stringArrayIndexS
                .append("this.grid[")
                .append(square.getColumn())
                .append("]")
                .append("[")
                .append(square.getRow())
                .append("]")
                .append(", ");
           barrierReferencedIdsArray_squareOrderOfTheContent
                    .append(square.getOrderOfTheContent())
                    .append(", ");
        }
        android.util.Log.i(barrierDirection + " Barrier " + barriersArrayIndex,
                barrierDirection + "Barrier n°" + barriersArrayIndex +
                ". \nbarrier.getReferencedIds() have Square with index in " +
                " this.grid: " + barrierReferencedIds_stringArrayIndexS +
                ". \nbarrier.getReferencedIds() have Square with order of the" +
                " content: " +
                barrierReferencedIdsArray_squareOrderOfTheContent +
                "\nThis Barrier have ConstraintSet.connect() to Square" +
                " with index in this.grid: " +
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
        // 1) grid[0][row].getId()
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierHorizontalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionHorizontal = ConstraintSet.TOP;
        for (int row = 0 ; row < this.gridLength ; row++) {
            set.connect(this.grid[0][row].getId(),
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
            // 1) this.grid[horizontalBarriersArray + 1][0].getId(),
            //      then this.grid[horizontalBarriersArray + 1][1].getId(),
            //      then this.grid[horizontalBarriersArray + 1][2].getId()
            // 2) AND horizontalBarriersArray[barriersArrayIndex]
            StringBuilder constraintSetConnect =
                new StringBuilder(this.gridLength);
            for (int row = 0 ; row < this.gridLength ; row++) {
                set.connect(this.grid[barriersArrayIndex + 1][row].getId(),
                        constraintDirectionHorizontal,
                        toBarrierHorizontalId, toConstraintDirectionHorizontal,
                        0);
                constraintSetConnect
                    .append("this.grid[")
                    .append(barriersArrayIndex)
                    .append(1)
                    .append("][")
                    .append("[")
                    .append(row)
                    .append("]")
                    .append(", ");
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
        // 1) grid[column][0].getId()
        // 2) AND vertical ConstraintSet.PARENT_ID
        toBarrierVerticalId = ConstraintSet.PARENT_ID;
        toConstraintDirectionVertical = ConstraintSet.LEFT;
        for (int column = 0 ; column < this.gridLength ; column++) {
            set.connect(this.grid[column][0].getId(),
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
            // 1) this.grid[0][verticalBarrierArray + 1].getId(),
            //      then this.grid[1][verticalBarrierArray + 1].getId(),
            //      then this.grid[2][verticalBarrierArray + 1].getId()
            // 2) AND verticalBarrierArray[barriersArrayIndex]
            StringBuilder constraintSetConnect =
                new StringBuilder(this.gridLength);
            for (int column = 0 ; column < this.gridLength ; column++) {
                set.connect(
                        this.grid[column][barriersArrayIndex + 1].getId(),
                        constraintDirectionVertical,
                        toBarrierVerticalId, toConstraintDirectionVertical,
                        0);
                constraintSetConnect
                    .append("this.grid[")
                    .append(column)
                    .append("][")
                    .append("[")
                    .append(barriersArrayIndex)
                    .append(1)
                    .append("]")
                    .append(", ");
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

        int squareNumberIndex = 0;

        // See :
        // https://developer.android.com/reference/android/view/WindowManager.html#getDefaultDisplay()
        // https://developer.android.com/reference/android/view/Display.html#getSize(android.graphics.Point)
        Point point = new Point();
        this.getWindowManager()
            .getDefaultDisplay()
            .getSize(point);
        int squareWidth = point.x / this.gridLength;
        int squareHeight = point.y / this.gridLength;
        Toast.makeText(this, point.toString(), Toast.LENGTH_SHORT)
            .show();

        int[] unorderedSequence = this.createUnorderedSequence();
        // Loop that populate array above
        for (int column = 0 ; column  < this.gridLength ; column++) {
            for (int row = 0 ; row < this.gridLength ; row++) {
            // squareText is the String text displayed in the Square.
            // Square.getText() of a row could be
            //      longer than Square.getText() of the row above.
            // We could see the interest of the Barrier
            // object : Barrier is aligned on the longer String)
            Square square = new Square (this, this.layout,
                    unorderedSequence[squareNumberIndex], row, column,
                    squareWidth, squareHeight);
            this.grid[column][row] = square;
            // https://developer.android.com/guide/topics/ui/ui-events
            square.setOnClickListener(new SquareOnClickListener(this));
            squareNumberIndex++;
            }
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
        //     .getReferencedIds() == new int[][] {this.grid[0][0],
        //         this.grid[0][1], this.grid[0][2], etc.}
        // (Barrier super.findViewById(horizontalBarriersArray[1]))
        //     .getReferencedIds() == new int[][] {this.grid[1][0],
        //         this.grid[1][1], this.grid[1][2], etc.}
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
        //     .getReferencedIds() == new int[][] {this.grid[0][0],
        //         this.grid[1][0], this.grid[2][0], etc.}
        // (Barrier super.findViewById(verticalBarrierArray[1]))
        //     .getReferencedIds() == new int[][] {this.grid[0][1],
        //         this.grid[1][1], this.grid[2][1], etc.}
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

    /**
      * Goto Immersive mode
      * Read https://developer.android.com/training/system-ui/immersive
      * Inspired from:
      *     https://github.com/googlesamples/android-ImmersiveMode/blob/master/Application/src/main/java/com/example/android/immersivemode/ImmersiveModeFragment.java
      */
    private void gotoImmersiveMode () {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int newUiOptions = this.getWindow().getDecorView()
            .getSystemUiVisibility();
        // Navigation bar hiding:  Backwards compatible to ICS.
        // if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        // }

        // Status bar hiding: Backwards compatible to Jellybean
        // if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        // }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments
        // the behavior of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the
        // purposes of this sample all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is
        // referred to as "sticky".  Sticky immersive mode differs in that it
        // makes the navigation and status bars semi-transparent, and the UI
        // flag does not get cleared when the user interacts with the screen.
        // if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

    }


    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gotoImmersiveMode();
        super.setContentView(R.layout.activity_main);

        // Retrieve layout
        this.layout = super.findViewById(R.id.constrained);

        this.gridLength = 10;

        this.grid = new Square[gridLength][gridLength];


        // Complete example:
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        this.createGrid();
    }

    // Getters
    // ==========
    // ==========

    int getGridLength() {
        return this.gridLength;
    }

    Square[][] getGrid() {
        return this.grid;
    }

}
