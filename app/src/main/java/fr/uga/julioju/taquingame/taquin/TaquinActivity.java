package fr.uga.julioju.taquingame.taquin;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import android.support.design.widget.Snackbar;
import fr.uga.julioju.taquingame.R;
import fr.uga.julioju.taquingame.main.MainActivity;
import fr.uga.julioju.taquingame.picture.PictureActivity;
import fr.uga.julioju.taquingame.picture.PictureActivityException;
import fr.uga.julioju.taquingame.util.ImageUtil;


/** Build the Main Activity, it contains a Grid constructed thanks a
  * ConstraintLayout (better than Grid View).
  * The goal of the game is than for each
  * <code>index</code> ((Square) grid.get(index)).getOrderOfTheContent() == 0
  * Each <code>Square</code> of the <code>grid</code> doesn't change:
  * only <code((Square) grid.get(index)).orderOfTheContent</code> change
  */
public class TaquinActivity extends AppCompatActivity {

    public static final String INTENT_IS_END_OF_APP =
        "fr.uga.julioju.taquingame.taquin.IS_END_OF_APP";

    /** Do not use GridLayout */
    private ConstraintLayout layout;

    /** Length of the grid. SHOULD NOT BE < 2 */
    private int gridLength;

    /** Square of the Grid */
    private Square[][] grid;

    /**
      * unordered sequence of numbers
      * between 0 and (this.gridLength * this.gridLength - 1).
      * This param is not needed only for the instantiation of grid[][]. After
      * this instantiation, it could be deduced from grid[][].
      * The goal of the game is to ordered this Array.
      * This Array IS UPDATED ONLY only in
      *     this.onSaveInstanceState(Bundle bundle)
      */
    private int[] unorderedSequence;

    private static final String UNORDERED_SEQUENCE_SAVED = "unorderedSequence";

    /**
      * Array of size gridLength * 2 that contains images.
      * backgroundArray[0] is never displayed during the game.
      * Therefore it is null.
      */
    private Drawable backgroundArray[];

    /**
      * Create an Array, with values that are aun unordered sequence of
      * number between 0 and (gridNumberOfSquares - 1).
      * if this.gridLength == 2, return always the same sequence.
      */
    private int[] createUnorderedSequence() {
        if (this.gridLength == 2) {
            return new int[] {1, 0, 2, 3};
        }
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

    /**
      * ConstraintSet.connect() for each Square
      */
    private void constraintSet() {

        ConstraintSet set = new ConstraintSet();
        set.clone(this.layout);

        int constraintDirectionHorizontal = ConstraintSet.TOP;
        int toConstraintDirectionHorizontal ;

        int constraintDirectionVertical = ConstraintSet.LEFT;
        int toConstraintDirectionVertical ;

        // ConstraintSet.connect between:
        // 1) grid[0][row].getId()
        // 2) AND vertical ConstraintSet.PARENT_ID
        toConstraintDirectionHorizontal = ConstraintSet.TOP;
        for (int row = 0 ; row < this.gridLength ; row++) {
            set.connect(this.grid[0][row].getId(),
                    constraintDirectionHorizontal,
                    ConstraintSet.PARENT_ID, toConstraintDirectionHorizontal,
                    0);
        }

        // ConstraintSet.connect between:
        // 1) grid[column][0].getId()
        // 2) AND vertical ConstraintSet.PARENT_ID
        toConstraintDirectionVertical = ConstraintSet.LEFT;
        for (int column = 0 ; column < this.gridLength ; column++) {
            set.connect(this.grid[column][0].getId(),
                    constraintDirectionVertical,
                    ConstraintSet.PARENT_ID, toConstraintDirectionVertical,
                    0);
        }

        toConstraintDirectionHorizontal = ConstraintSet.BOTTOM;
        toConstraintDirectionVertical = ConstraintSet.RIGHT;
        for (int column = 0 ; column < this.gridLength ; column++) {
            for (int row = 0 ; row < this.gridLength ; row++) {
                if (column != 0) {
                    set.connect(this.grid[column][row].getId(),
                            constraintDirectionHorizontal,
                            this.grid[column - 1][row].getId(),
                            toConstraintDirectionHorizontal,
                            0);
                }
                if (row != 0) {
                set.connect(this.grid[column][row].getId(),
                        constraintDirectionVertical,
                        this.grid[column][row - 1].getId(),
                        toConstraintDirectionVertical,
                        0);
                }
            }
        }

        // Apply to layout
        // =========
        set.applyTo(this.layout);

    }

    /**
      * Create a grid like below.
      * If this.gridLength < 3, draw with letters and "."
      */
    private void createGrid(Point screenSize) {

        // Create Square of the grid
        // ============================
        // ============================

        short squareNumberIndex = 0;

        int squareWidth = screenSize.x / this.gridLength;
        int squareHeight = screenSize.y / this.gridLength;

        int marginLeftFirstColumn = screenSize.x % this.gridLength / 2;
        int marginTopFirstRow = screenSize.y % this.gridLength / 2;
        // Loop that populate array above
        for (int column = 0 ; column  < this.gridLength ; column++) {
            for (int row = 0 ; row < this.gridLength ; row++) {
            // squareText is the String text displayed in the Square.
            // Square.getText() of a row could be
            //      longer than Square.getText() of the row above.
            // We could see the interest of the Barrier
            // object : Barrier is aligned on the longer String)
            int marginLeft = row == 0
                ? marginLeftFirstColumn
                : 0;
            int marginTop = column == 0
                ? marginTopFirstRow
                : 0;

            Square square = new Square (this, this.layout,
                    this.unorderedSequence[squareNumberIndex], row, column,
                    squareWidth, squareHeight,
                    this.backgroundArray[this
                        .unorderedSequence[squareNumberIndex]],
                    marginLeft, marginTop);

            this.grid[column][row] = square;
            // https://developer.android.com/guide/topics/ui/ui-events
            square.setOnClickListener(new SquareOnClickListener(this));
            squareNumberIndex++;
            }
        }

        // ConstraintSet between each Square
        // ============================================
        // ============================================
        this.constraintSet();

    }

    /**
      * Goto Immersive mode
      * Use https://developer.android.com/training/system-ui/status#java
      * See also https://developer.android.com/training/system-ui/immersive
      * Commented code is inspired from:
      *     https://github.com/googlesamples/android-ImmersiveMode/blob/master/Application/src/main/java/com/example/android/immersivemode/ImmersiveModeFragment.java
      */
    private void gotoImmersiveMode () {
        // // The UI options currently enabled are represented by a bitfield.
        // // getSystemUiVisibility() gives us that bitfield.
        // int newUiOptions = this.getWindow().getDecorView()
        //     .getSystemUiVisibility();
        // // Navigation bar hiding:  Backwards compatible to ICS.
        // // if (Build.VERSION.SDK_INT >= 14) {
        //     newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        // // }
        //
        // // Status bar hiding: Backwards compatible to Jellybean
        // // if (Build.VERSION.SDK_INT >= 16) {
        //     newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        // // }
        //
        // // Immersive mode: Backward compatible to KitKat.
        // // Note that this flag doesn't do anything by itself, it only augments
        // // the behavior of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the
        // // purposes of this sample all three flags are being toggled together.
        // // Note that there are two immersive mode UI flags, one of which is
        // // referred to as "sticky".  Sticky immersive mode differs in that it
        // // makes the navigation and status bars semi-transparent, and the UI
        // // flag does not get cleared when the user interacts with the screen.
        // // if (Build.VERSION.SDK_INT >= 18) {
        //     newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // // }
        //
        // this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        View decorView = super.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
      * Could also use `this.unorderedSequence[]' if it was updated.
      * But at this point, generally it isn't updated.
      */
    void displayDialogIfGameIsWin() {
        short squareNumberIndex = 0;
        for (int column = 0 ; column < this.gridLength ; column++) {
            for (int row = 0 ; row < this.gridLength ; row++) {
                if (this.grid[column][row].getOrderOfTheContent()
                        != squareNumberIndex  ) {
                    return ;
                }
                squareNumberIndex++;
            }
        }
        new GameWinFireDialog()
            .show(super.getSupportFragmentManager(), "");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        short squareNumberIndex = 0;
        for (int column = 0 ; column < this.gridLength ; column++) {
            for (int row = 0 ; row < this.gridLength ; row++) {
                this.unorderedSequence[squareNumberIndex] =
                    this.grid[column][row]
                        .getOrderOfTheContent();
                squareNumberIndex++;
            }
        }
        outState.putIntArray(TaquinActivity.UNORDERED_SEQUENCE_SAVED,
                this.unorderedSequence);
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intentIncome = super.getIntent();
        this.gridLength = intentIncome.getIntExtra(MainActivity
                        .INTENT_GRID_LENGTH, 10);
        Uri uriImage = intentIncome.getParcelableExtra(PictureActivity
                .INTENT_IMAGE_URI);

        if (savedInstanceState == null) {
            // This Array is updated only in
            //      `this.onSaveInstanceState(Bundle bundle);'
            this.unorderedSequence = this.createUnorderedSequence();
        }
        else {
            this.unorderedSequence = savedInstanceState
                .getIntArray(UNORDERED_SEQUENCE_SAVED);
            if (this.unorderedSequence == null
                    || this.unorderedSequence.length
                        != (this.gridLength * this.gridLength)) {
                android.util.Log.e("Retrieve state error",
                        "this.unorderedSequence couldn't be retrieve.");
                this.unorderedSequence = this.createUnorderedSequence();
            }
        }

        this.grid = new Square[gridLength][gridLength];

        // Complete example:
        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        this.layout = new ConstraintLayout(this);
        this.layout.setId(View.generateViewId());
        this.layout.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
        this.layout.setBackground(super.getDrawable(R.drawable.back));
        super.setContentView(layout);

        Point screenSize = ImageUtil.screenSize(this);
        int[] originalImageSize = new int[2];
        try {
            originalImageSize = ImageUtil.isGoodImage(this, uriImage);
        }
        catch (PictureActivityException e){
            Toast.makeText(this, "Error when decode bitmap"
                , Toast.LENGTH_LONG)
                .show();
        }
        Toast.makeText(this, "Screen:" + screenSize.toString() +
                "\nImage original: " +
                    originalImageSize[0] + " " + originalImageSize[1]
                , Toast.LENGTH_LONG)
            .show();

        try {
            this.backgroundArray = ImageUtil
                .generateBitmapDrawableArray(this, this.gridLength,
                        screenSize.x, screenSize.y, uriImage);
        } catch (IOException | InterruptedException e) {
            String messageError = "Error when try to retrieve selected" +
                " picture." +
                " No picture is displayed in Squares.";
            android.util.Log.e("Decode image error", messageError);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            android.util.Log.e("Exception",  sw.toString());
            Snackbar.make(this.layout, messageError,
                    Snackbar.LENGTH_INDEFINITE).show();
            this.backgroundArray =
                new Drawable[this.gridLength * this.gridLength];
            for (int backgroundArrayIndex = 0 ;
                    backgroundArrayIndex < backgroundArray.length ;
                    backgroundArrayIndex++) {
                this.backgroundArray[backgroundArrayIndex] = super
                    .getDrawable(R.drawable.back);
            }
        } finally {
            this.createGrid(screenSize);
            // If drawn in right order
            this.displayDialogIfGameIsWin();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        this.gotoImmersiveMode();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.d("TaquinActivity finished",
                "TaquinActivity FINISHED");
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

    Drawable[] getBackgroundArray() {
        return backgroundArray;
    }

}
