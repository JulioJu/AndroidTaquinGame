package fr.uga.julioju.taquingame.taquin;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
  * Animate the move of a Drawable from a Square to another, therefore when a
  * Square clicked contains a neighbour with
  * `Square.getOrderOfTheContent() == 0`.
  * If the user click on another Square during the animation, no other new move
  * of Drawable could be triggered before the end of the current Animation.
  */
public class SquareOnClickListenerAnimationListener
    implements Animation.AnimationListener {

    private Square neighbourSquareEmpty;

    private Square squareClicked;

    private TaquinActivity taquinActivity;

    private boolean isPhotoGoOff;

    private int photoGoInFromXDelta;
    private int photoGoInToXDelta;
    private int photoGoInFromYDelta;
    private int photoGoInToYDelta;

    SquareOnClickListenerAnimationListener(Square neighbourSquareEmpty,
            Square squareClicked, TaquinActivity taquinActivity,
            boolean isPhotoGoOff,
            int photoGoInFromXDelta, int photoGoInToXDelta,
            int photoGoInFromYDelta, int photoGoInToYDelta) {
        this.neighbourSquareEmpty  = neighbourSquareEmpty;
        this.squareClicked         = squareClicked;
        this.taquinActivity        = taquinActivity;
        this.isPhotoGoOff          = isPhotoGoOff;

        this.photoGoInFromXDelta   = photoGoInFromXDelta;
        this.photoGoInToXDelta     = photoGoInToXDelta;
        this.photoGoInFromYDelta   = photoGoInFromYDelta;
        this.photoGoInToYDelta    = photoGoInToYDelta;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (this.isPhotoGoOff) {
            TranslateAnimation photoGoIn = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, this.photoGoInFromXDelta,
                    Animation.RELATIVE_TO_SELF, this.photoGoInToXDelta,
                    Animation.RELATIVE_TO_SELF, this.photoGoInFromYDelta,
                    Animation.RELATIVE_TO_SELF, this.photoGoInToYDelta);
            photoGoIn.setDuration(4500);
            photoGoIn.setAnimationListener(new
                    SquareOnClickListenerAnimationListener(this
                        .neighbourSquareEmpty, this.squareClicked,
                        this.taquinActivity, false, 0, 0 ,0, 0));
            this.neighbourSquareEmpty.startAnimation(photoGoIn);
        } else {
            this.neighbourSquareEmpty
                .setOrderOfTheContent(squareClicked.getOrderOfTheContent(),
                        squareClicked.getBackground());
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (this.isPhotoGoOff) {
            this.squareClicked.setEmptySquare();
            taquinActivity.displayDialogIfGameIsWin();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


}
