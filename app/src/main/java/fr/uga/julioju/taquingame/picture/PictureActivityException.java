package fr.uga.julioju.taquingame.picture;

import android.view.ViewGroup;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.support.design.widget.Snackbar;

class PictureActivityException extends Exception {

    private String messageError;

    // Constructs a new throwable with the specified detail message.
    PictureActivityException(String messageError) {
        super(messageError);
        this.messageError = messageError;
    }

    // Constructs a new throwable with the specified detail message and cause.
    PictureActivityException(String messageError, Throwable cause) {
        super(messageError, cause);
        this.messageError = messageError;
    }

    static void displayError(ViewGroup layout,
            PictureActivityException exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        android.util.Log.e("Exception",  sw.toString());
        Snackbar.make(layout, exception.messageError,
                Snackbar.LENGTH_LONG).show();
    }

}
