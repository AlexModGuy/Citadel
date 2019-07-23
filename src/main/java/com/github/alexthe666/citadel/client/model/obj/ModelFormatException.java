package com.github.alexthe666.citadel.client.model.obj;

/**
 * Thrown if there is a problem parsing the model
 *
 * @author cpw
 * @since 1.0.0
 */
public class ModelFormatException extends RuntimeException {

    private static final long serialVersionUID = 2023547503969671835L;

    public ModelFormatException() {
        super();
    }

    public ModelFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFormatException(String message) {
        super(message);
    }

    public ModelFormatException(Throwable cause) {
        super(cause);
    }

}
