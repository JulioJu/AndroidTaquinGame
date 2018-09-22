package fr.uga.julioju.taquingame.taquin;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

// https://developer.android.com/guide/topics/ui/ui-events
/**
  * Manage actions when we click on a Square.
  * Quickly garbaged.
  *
  * If exists, `neighbourSquareEmpty` is the neighbour Square than has
  * Square.orderOfTheContent == 0.
  *
  * In this case performs:
  * ```
  * neighbourSquareEmpty.setOrderOfTheContent(
  *          squareClicked.orderOfTheContent,
  *          squareClicked.getBackground());
  * squareClicked.setEmptySquare();
  * ```
  * (currently done in class `SquareOnClickListenerAnimationListener`)
  */
class SquareOnClickListener implements View.OnClickListener {

    private final TaquinActivity taquinActivity;

    /**
      * If a Neighbour of the Square clicked is the
      * "Empty Square" (<code>square.getOrderOfTheContent() == 0</code>)
      * this variable will become the Empty Square, otherwise stay null
      * If exists, initialized in this.getNeighbour().
      */
    private Square neighbourSquareEmpty = null;

    private String neighbourSquareEmptyCardinalDirection;

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
                this.neighbourSquareEmptyCardinalDirection = cardinalDirection;
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

    private void translateAnimation(Square squareClicked,
            int photoGoOffFromXDelta, int photoGoOffToXDelta,
            int photoGoOffFromYDelta, int photoGoOffToYDelta,
            int photoGoInFromXDelta, int photoGoInToXDelta,
            int photoGoInFromYDelta, int photoGoInToYDelta
            ) {
        TranslateAnimation photoGoOff = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, photoGoOffFromXDelta,
                Animation.RELATIVE_TO_SELF, photoGoOffToXDelta,
                Animation.RELATIVE_TO_SELF, photoGoOffFromYDelta,
                Animation.RELATIVE_TO_SELF, photoGoOffToYDelta);
        photoGoOff.setDuration(4500);
        photoGoOff.setAnimationListener(
                new SquareOnClickListenerAnimationListener
                    (this.neighbourSquareEmpty, squareClicked,
                     this.taquinActivity, true,
                     photoGoInFromXDelta, photoGoInToXDelta,
                     photoGoInFromYDelta, photoGoInToYDelta));
        squareClicked.startAnimation(photoGoOff);
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
            squareWest = this.taquinActivity.getGrid()[squareClickedColumn]
                [squareClickedRow - 1];
        }
        if (squareClickedColumn > 0) {
            squareNorth = this.taquinActivity.getGrid()[squareClickedColumn - 1]
                [squareClickedRow];
        }
        if (squareClickedRow < this.taquinActivity.getGridLength() - 1) {
            squareEast = this.taquinActivity.getGrid()[squareClickedColumn]
                [squareClickedRow + 1];
        }
        if (squareClickedColumn < this.taquinActivity.getGridLength() - 1) {
            squareSouth = this.taquinActivity.getGrid()[squareClickedColumn + 1]
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

            switch (this.neighbourSquareEmptyCardinalDirection) {
                case "west":
                    this.translateAnimation(squareClicked,
                            1, 1, 0, 0, 1, 0, 0, 0);
                    break;
                case "north":
                    this.translateAnimation(squareClicked,
                            0, 0, 1, 1, 0, 0, 1, 0);
                    break;
                case "east":
                    this.translateAnimation(squareClicked,
                            0, 1, 0, 0, 1, 1, 0, 0);
                    break;

                case "south":
                    this.translateAnimation(squareClicked,
                            0, 0, 0, 1, 0, 0, 1, 1);
                    break;
            }

            CharSequence text = "Around the Square clicked, there is a" +
                "Square that is the Empty Square. The Square clicked" +
                " is moved on it.";
            Toast.makeText(this.taquinActivity, text, Toast.LENGTH_SHORT).show();

            // BECAUSE IF THE INSTANCE OF THIS CLASS IS NOT GARBAGED
            // we continue to have `this.neighbourSquareEmpty != null`
            this.neighbourSquareEmpty = null;
        }
        else {
            CharSequence text = "Around the Square clicked, there is "  +
                "Not a Square that is the Empty Square. NOTHING DONE.";
            Toast.makeText(this.taquinActivity, text, Toast.LENGTH_SHORT).show();
        }
    }

    SquareOnClickListener(TaquinActivity taquinActivity) {
        this.taquinActivity = taquinActivity;
    }

}
