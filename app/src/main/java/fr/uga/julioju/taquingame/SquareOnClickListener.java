package fr.uga.julioju.taquingame;

import android.view.View;
import android.widget.Toast;

// https://developer.android.com/guide/topics/ui/ui-events
/**
  * Manage actions when we click on a Square
  */
public class SquareOnClickListener implements View.OnClickListener {

    private final MainActivity activity;

    /** Index in Array squareArray of the Square to the EAST of those clicked */
    private int squareArrayIndexEast;
    /** Index in Array squareArray of the Square to the NORTH of those clicked*/
    private int squareArrayIndexNorth;
    /** Index in Array squareArray of the Square to the WEST of those clicked */
    private int squareArrayIndexWest;
    /** Index in Array squareArray of the Square to the SOUTH of those clicked*/
    private int squareArrayIndexSouth;

    /**
      * If a Neighbour of the Square clicked is the
      * "Empty Square" (<code>square.getOrder() == 0</code>)
      * this variable will become the Empty Square, otherwise stay null
      * If exists, initialized in this.getNeighbour().
      */
    private Square neighbourSquareEmpty = null;

    /**
      * Retrieve Square at the East, North, West, South of the Square clicked
      * @param squareArrayIndex in activity.squareArray, index of the Square
      *     clicked;
      */
    private void retrieveSquareNeighbour(int squareArrayIndex) {
        if (squareArrayIndex % this.activity.getGridLength() == 0) {
            this.squareArrayIndexEast = -1;
        }
        else {
            this.squareArrayIndexEast = squareArrayIndex - 1;
        }
        if (squareArrayIndex - this.activity.getGridLength() < 0) {
            this.squareArrayIndexNorth = -1;
        }
        else {
            this.squareArrayIndexNorth =
                squareArrayIndex - this.activity.getGridLength();
        }
        if ((squareArrayIndex + 1) % this.activity.getGridLength() == 0) {
            this.squareArrayIndexWest = -1;
        }
        else {
            this.squareArrayIndexWest = squareArrayIndex + 1;
        }
        if ((squareArrayIndex + this.activity.getGridLength() >=
                    this.activity.getGridNumberOfSquares())) {
            this.squareArrayIndexSouth = -1;
        }
        else {
            this.squareArrayIndexSouth =
                squareArrayIndex + this.activity.getGridLength();
        }
    }

    /**
      * Parse Neighbour Square.
      * Initialize <code>this.neighbourSquareEmpty</code>
      * @param squareArrayIndexNeighbour of the Neighbour Square.
      * @return the indexOf this neighbour Square in <code>
      *     activity.unorderedList</code>
      */
    private int getNeighbour (int squareArrayIndexNeighbour) {
        if (squareArrayIndexNeighbour == -1) {
            return -1;
        }
        else {
            Square squareNeighbour =
                this.activity.getSquareArray().get(squareArrayIndexNeighbour);
            int unorderedListIndexNeighbour =
                squareNeighbour.getOrder();
            if (unorderedListIndexNeighbour == 0) {
                this.neighbourSquareEmpty = squareNeighbour;
            }
            return unorderedListIndexNeighbour;
        }
    }

    /**
     * Implement the OnClickListener callback.
     * Should be seen as the second constructor.
     * When we click in a Square
     * 1) Retrieve information of the Square and display it in Logcat.
     * 2) Display a Toast that say if a neighbour is the
     *      "Empty Square" (<code>square.getOrder() == 0</code>)
     * 3) If a neighbour is the "Empty Square" switch place in
     *      activity.unorderedList
     */
    @Override
    public void onClick(View v) {
        // Square clicked. Initialize in `onClick()'
        Square squareClicked = (Square) v;

        int squareArrayIndexOfSquareClicked =
            this.activity.getSquareArray().indexOf(squareClicked);

        this.retrieveSquareNeighbour(squareArrayIndexOfSquareClicked);

        int unorderedListIndexEast =
            this.getNeighbour(this.squareArrayIndexEast);
        int unorderedListIndexNorth =
            this.getNeighbour(this.squareArrayIndexNorth);
        int unorderedListIndexWest =
            this.getNeighbour(this.squareArrayIndexWest);
        int unorderedListIndexSouth =
            this.getNeighbour(this.squareArrayIndexSouth);

        android.util.Log.i("clicked",
                "`squareClicked.getId()`: " + squareClicked.getId() +
                "\nIndex of the Square clicked in `activity.squareArray': "   +
                squareArrayIndexOfSquareClicked                            +
                "\nSquare order Number:"                                   +
                squareClicked.getOrder()                                   +
                "\nIndex in `squareArray': " +
                "\n\t"  + this.squareArrayIndexEast   + " is to the east, "   +
                "\n\t"  + this.squareArrayIndexNorth  + " is to the north, "  +
                "\n\t"  + this.squareArrayIndexWest   + " is to the west, "   +
                "\n\t"  + this.squareArrayIndexSouth  + " is to the south "   +
                "\nIndex in `unorderedList': " +
                "\n\t"  + unorderedListIndexEast   + " is to the east, "    +
                "\n\t"  + unorderedListIndexNorth  + " is to the north, "   +
                "\n\t"  + unorderedListIndexWest   + " is to the west, "    +
                "\n\t"  + unorderedListIndexSouth  + " is to the south ");
        if (this.neighbourSquareEmpty != null) {
            this.neighbourSquareEmpty.setOrder(squareClicked.getOrder());
            squareClicked.setOrder(0);
            CharSequence text = "Around the Square clicked, there is a" +
                "Square that is the Empty Square. The Square clicked" +
                " is moved on it";
            Toast.makeText(this.activity, text, Toast.LENGTH_SHORT).show();
        }
        else {
            CharSequence text = "Around the Square clicked, there is "  +
                "Not a Square that is the Empty Square. NOTHING DONE.";
            Toast.makeText(this.activity, text, Toast.LENGTH_SHORT).show();
        }
    }

    SquareOnClickListener(MainActivity activity) {
        this.activity = activity;
    }

}
