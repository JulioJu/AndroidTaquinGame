package fr.uga.julioju.taquingame;

import android.view.View;

import java.util.ArrayList;

// https://developer.android.com/guide/topics/ui/ui-events
/**
  * Manage actions when we click on a Square
  */
public class SquareOnClickListener implements View.OnClickListener {

    /** Length of the grid. SHOULD NOT BE < 2 */
    private final int gridLength;

    /** Number of square of the grid (this.gridLength * this.gridLength) */
    private final int gridNumberOfSquares;

    /** Square of the Grid */
    private final ArrayList<Square> squareArray;

    /** Index in Array squareArray of the Square to the EAST of those clicked */
    private int squareArrayIndexEast;
    /** Index in Array squareArray of the Square to the NORTH of those clicked*/
    private int squareArrayIndexNorth;
    /** Index in Array squareArray of the Square to the WEST of those clicked */
    private int squareArrayIndexWest;
    /** Index in Array squareArray of the Square to the SOUTH of those clicked*/
    private int squareArrayIndexSouth;

    private void retrieveSquareNeighbour(int squareArrayIndex) {
        if (squareArrayIndex % this.gridLength == 0) {
            this.squareArrayIndexEast = -1;
        }
        else {
            this.squareArrayIndexEast = squareArrayIndex - 1;
        }
        if (squareArrayIndex - this.gridLength < 0) {
            this.squareArrayIndexNorth = -1;
        }
        else {
            this.squareArrayIndexNorth = squareArrayIndex - this.gridLength;
        }
        if ((squareArrayIndex + 1) % this.gridLength == 0) {
            this.squareArrayIndexWest = -1;
        }
        else {
            this.squareArrayIndexWest = squareArrayIndex + 1;
        }
        if ((squareArrayIndex + this.gridLength >= gridNumberOfSquares)) {
            this.squareArrayIndexSouth = -1;
        }
        else {
            this.squareArrayIndexSouth = squareArrayIndex + this.gridLength;
        }
    }

    /**
      * Get index of ArrayList <code>unorderedList</code> from
      * index of ArrayList <code>squareArray</code>.
      * @param squareArrayIndex of squareArray
      */
    private int getUnorderedListIndexFromSquareArrayIndex (int
            squareArrayIndex) {
        if (squareArrayIndex == -1) {
            return -1;
        }
        else {
            return this.squareArray
                .get(squareArrayIndex).getUnorderedListIndex();
        }
    }

    /**
     * When we click in a Square, retrieve information of the Square and
     * display it in Logcat.
     * Implement the OnClickListener callback
     */
    @Override
    public void onClick(View v) {
        Square square = (Square) v;
        this.retrieveSquareNeighbour(square.getSquareArrayIndex());
        int unorderedListIndexEast =
            this.getUnorderedListIndexFromSquareArrayIndex(this
                    .squareArrayIndexEast);
        int unorderedListIndexNorth =
            this.getUnorderedListIndexFromSquareArrayIndex(this
                    .squareArrayIndexNorth);
        int unorderedListIndexWest =
            this.getUnorderedListIndexFromSquareArrayIndex(this
                    .squareArrayIndexWest);
        int unorderedListIndexSouth =
            this.getUnorderedListIndexFromSquareArrayIndex(this
                    .squareArrayIndexSouth);
        android.util.Log.i("clicked", "`square.getId()`: " + square.getId() +
                "\n`squareArray.getSquareArrayIndex()': "           +
                square.getSquareArrayIndex()                        +
                "\n`unorderedListIndex.getUnorderedListIndex()': "  +
                square.getUnorderedListIndex()                      +
                "\nIndex in `squareArray': " +
                "\n\t" + this.squareArrayIndexEast     + " is to the east, "  +
                "\n\t" + this.squareArrayIndexNorth    + " is to the north, " +
                "\n\t" + this.squareArrayIndexWest     + " is to the west, "  +
                "\n\t" + this.squareArrayIndexSouth    + " is to the south " +
                "\nIndex in `unorderedList': " +
                "\n\t"  + unorderedListIndexEast   + " is to the east, "    +
                "\n\t"  + unorderedListIndexNorth  + " is to the north, "   +
                "\n\t"  + unorderedListIndexWest   + " is to the west, "    +
                "\n\t"  + unorderedListIndexSouth  + " is to the south ");
    }

    SquareOnClickListener(int gridLength, ArrayList<Square> squareArray) {
        this.gridLength = gridLength;
        this.gridNumberOfSquares = gridLength * gridLength;
        this.squareArray = squareArray;
    }

}
