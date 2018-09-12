package fr.uga.julioju.taquingame.picture;

import android.view.View;
import android.view.ViewGroup;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.support.design.widget.Snackbar;
import android.widget.TextView;

public class PictureActivityException extends Exception {

    private String messageError;

    // Constructs a new throwable with the specified detail message.
    public PictureActivityException(String messageError) {
        super(messageError);
        this.messageError = messageError;
    }

    // Constructs a new throwable with the specified detail message and cause.
    public PictureActivityException(String messageError, Throwable cause) {
        super(messageError, cause);
        this.messageError = messageError;
    }

    static void displayError(ViewGroup layout,
            PictureActivityException exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        android.util.Log.e("Exception",  sw.toString());
        // https://stackoverflow.com/questions/30705607/android-multiline-snackbar
        Snackbar snackbar = Snackbar.make(layout, exception.messageError,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", (v) -> snackbar.dismiss());
        View snackView = snackbar.getView();
        TextView snackTextView = snackView.
            findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setMaxLines(6);
        snackbar.show();
    }

}
