package fr.uga.julioju.taquingame;

import android.view.View;
import android.widget.Toast;

// https://developer.android.com/guide/topics/ui/ui-events
/**
  * Manage actions when we click on a Square
  */
public class SquareOnClickListener implements View.OnClickListener {

    private final MainActivity activity;

    /**
      * If a Neighbour of the Square clicked is the
      * "Empty Square" (<code>square.getOrderOfTheContent() == 0</code>)
      * this variable will become the Empty Square, otherwise stay null
      * If exists, initialized in this.getNeighbour().
      */
    private Square neighbourSquareEmpty = null;

    /**
      * Parse Neighbour Square.
      * Initialize <code>this.neighbourSquareEmpty</code>
      * @param squareNeighbour Neighbour Square.
      * @param cardinalDirection west, north, east or south
      * @return a String that could be logged.
      */
    private String parseSquareNeighbour(Square squareNeighbour,
            String cardinalDirection) {
        if (squareNeighbour != null) {
            if (squareNeighbour.getOrderOfTheContent() == 0) {
                this.neighbourSquareEmpty = squareNeighbour;
            }
            return "The Square with row " + squareNeighbour.getRow() +
                " with column " + squareNeighbour.getColumn() + " is to the " +
                cardinalDirection + ". Its content has the order " +
                squareNeighbour.getOrderOfTheContent();
        }
        else {
            return "There is a border to the " + cardinalDirection;
        }
    }

    /**
     * Implement the OnClickListener callback.
     * Should be seen as the second constructor.
     * When we click in a Square
     * 1) Retrieve information of the Square and display it in Logcat.
     * 2) Display a Toast that say if a neighbour is the
     *      "Empty Square" (<code>square.getOrderOfTheContent() == 0</code>)
     * 3) If a neighbour is the "Empty Square" switch their orderOfTheContent
     *      field.
     */
    @Override
    public void onClick(View v) {
        // Square clicked. Initialize in `onClick()'
        Square squareClicked = (Square) v;
        int squareClickedRow = squareClicked.getRow();
        int squareClickedColumn = squareClicked.getColumn();

        Square squareWest   = null;
        Square squareNorth  = null;
        Square squareEast   = null;
        Square squareSouth  = null;
        if (squareClickedRow > 0) {
            squareWest = this.activity.getGrid()[squareClickedColumn]
                [squareClickedRow - 1];
        }
        if (squareClickedColumn > 0) {
            squareNorth = this.activity.getGrid()[squareClickedColumn - 1]
                [squareClickedRow];
        }
        if (squareClickedRow < this.activity.getGridLength() - 1) {
            squareEast = this.activity.getGrid()[squareClickedColumn]
                [squareClickedRow + 1];
        }
        if (squareClickedColumn < this.activity.getGridLength() - 1) {
            squareSouth = this.activity.getGrid()[squareClickedColumn + 1]
                [squareClickedRow ];
        }

        android.util.Log.i("clicked",
                "Id of the Square clicked is: " + squareClicked.getId() +
                "\nCoordinate in the grid: row = "    +
                squareClicked.getRow()                +
                " column = "                          +
                squareClicked.getColumn()             +
                "\nSquare order of the content: "     +
                squareClicked.getOrderOfTheContent()  +
                "\nIndex in `squareArray': "          +
                "\n\t"  + parseSquareNeighbour(squareWest, "west")    +
                "\n\t"  + parseSquareNeighbour(squareNorth, "north")  +
                "\n\t"  + parseSquareNeighbour(squareEast, "east")    +
                "\n\t"  + parseSquareNeighbour(squareSouth, "south"));
        if (this.neighbourSquareEmpty != null) {
            this.neighbourSquareEmpty
                .setOrderOfTheContent(squareClicked.getOrderOfTheContent());
            squareClicked.setOrderOfTheContent(0);
            CharSequence text = "Around the Square clicked, there is a" +
                "Square that is the Empty Square. The Square clicked" +
                " is moved on it.";
            Toast.makeText(this.activity, text, Toast.LENGTH_SHORT).show();
            // BECAUSE IF THE INSTANCE OF THIS CLASS IS NOT GARBAGED
            // we continue to have `this.neighbourSquareEmpty != null`
            this.neighbourSquareEmpty = null;
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
