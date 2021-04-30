package Exceptions;

public class QCNException extends Exception {

    /**
     * Le constructeur des exceptions spéciales pour les RCQ.
     * @param message le message d'erreur
     */
    public QCNException(String message) {
        super(message);
    }
}
