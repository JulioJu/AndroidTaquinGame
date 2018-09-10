package fr.uga.julioju.taquingame.picture;

class PictureActivityException extends Exception {

    String messageError;

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

    String getMessageError() {
        return messageError;
    }

}
