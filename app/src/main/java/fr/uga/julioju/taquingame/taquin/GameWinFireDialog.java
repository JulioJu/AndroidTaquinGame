package fr.uga.julioju.taquingame.taquin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.DialogFragment;

import fr.uga.julioju.taquingame.camera.CameraActivity;

public class GameWinFireDialog extends DialogFragment {

    private void finishActivityOrApp(boolean isEndOfApp) {
        Activity activity = super.getActivity();
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(TaquinActivity.EXTRA_MESSAGE_IS_END_OF_APP, isEndOfApp);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Congratulation !!! Game turns wins !!!")
               .setPositiveButton("Play again !",
                       new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       GameWinFireDialog.this.finishActivityOrApp(false);
                   }
               })
               .setNegativeButton("Stop the game",
                       new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       GameWinFireDialog.this.finishActivityOrApp(true);
                   }
               });
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        // https://stackoverflow.com/questions/42254443/alertdialog-disappears-when-touch-is-outside-android
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
