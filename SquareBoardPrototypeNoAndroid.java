// Draw a square in console (no used by Android)
// To execute it, type in a console:
// `javac SquareBoardPrototypeNoAndroid.java &&
// java SquareBoardPrototypeNoAndroid'

// It's constructs with recursivity.
public class SquareBoardPrototypeNoAndroid {

    private class Square {

        int row;
        int col;

        String myString;

        Square north;
        Square east;
        Square south;
        Square west;

        public Square (int row, int col, String myString, Square north,
                Square east, Square south, Square west) {
            this.row       = row;
            this.col       = col;
            this.myString  = myString;
            this.north     = north;
            this.east      = east;
            this.south     = south;
            this.west      = west;
        }

    }

    public Square instanciateSquare(int row, int col, int boardLength) {
        if (row == boardLength || col == boardLength) {
            return null;
        }
        else {
            return new Square(row, col , "aa", null,
                    this.instanciateSquare(row + 1, col, boardLength),
                    this.instanciateSquare(row, col + 1,  boardLength), null);
        }
    }

    public void printSquare(Square squareInit) {
        Square squareFirstCol = squareInit;
        while (squareFirstCol != null) {
            Square squareCurrent = squareFirstCol;
            while (squareCurrent != null) {
                System.out.print(squareCurrent.row + " " +
                        squareCurrent.col + "|");
                squareCurrent = squareCurrent.east;
            }
            System.out.println("");
            squareFirstCol = squareFirstCol.south;
        }
    }

    public static void main (String args[]){

        int boardLength = 5;

        SquareBoardPrototypeNoAndroid squareBoard =
            new SquareBoardPrototypeNoAndroid();

        Square square = squareBoard.instanciateSquare(0, 0, boardLength);

        squareBoard.printSquare(square);

    }


}
