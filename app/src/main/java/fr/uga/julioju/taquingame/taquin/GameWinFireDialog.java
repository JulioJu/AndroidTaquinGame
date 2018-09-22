package fr.uga.julioju.taquingame.taquin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.DialogFragment;

import fr.uga.julioju.taquingame.picture.PictureActivity;

// SuppressWarnings because DialogFragment should be `public`
/**
  * Displayed if the Array of integer `TaquinActivity.grid.orderOfTheContent` is
  * ordered.
  *
  * SquareOnClickListenerAnimationListener call
  * `TaquinActivity.displayDialogIfGameIsWin()`, then if it's win
  * GameWinFireDialog is displayed.
  */
@SuppressWarnings("WeakerAccess")
public class GameWinFireDialog extends DialogFragment {

    private void finishActivityOrApp(boolean isEndOfApp) {
        Activity activity = super.getActivity();
        Intent intent = new Intent(activity, PictureActivity.class);
        intent.putExtra(TaquinActivity.INTENT_IS_END_OF_APP, isEndOfApp);
        activity.setResult(Activity.RESULT_OK, intent);
        if (isEndOfApp) {
            activity.finishAndRemoveTask();
        }
        else {
            activity.finish();
        }
    }

    // https://developer.android.com/reference/android/app/Dialog
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(super
                .getActivity());
        builder.setMessage("Congratulation !!! Game turns wins !!!")
               .setPositiveButton("Play again !", (dialog, id) ->
                       GameWinFireDialog.this.finishActivityOrApp(false))
               .setNegativeButton("Stop the game", (dialog, id) ->
                       GameWinFireDialog.this.finishActivityOrApp(true));
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        // https://stackoverflow.com/questions/42254443/alertdialog-disappears-when-touch-is-outside-android
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
