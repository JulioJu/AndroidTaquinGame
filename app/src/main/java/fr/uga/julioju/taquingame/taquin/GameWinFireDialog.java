package fr.uga.julioju.taquingame.taquin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.DialogFragment;

import fr.uga.julioju.taquingame.main.MainActivity;

public class GameWinFireDialog extends DialogFragment {

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
                       GameWinFireDialog.this.getActivity().finish();
                   }
               })
               .setNegativeButton("Stop the game",
                       new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Intent intent = new Intent(Intent.ACTION_MAIN);
                       intent.addCategory(Intent.CATEGORY_HOME );
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       GameWinFireDialog.super.startActivity(intent);
                   }
               });
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        // https://stackoverflow.com/questions/42254443/alertdialog-disappears-when-touch-is-outside-android
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
