package com.gwexhibits.timemachine.objects;

/**
 * Created by psyfu on 3/24/2016.
 */
public class EndAfterStartException extends Exception {
    public EndAfterStartException(String message) {
        super(message);
    }

    public EndAfterStartException() {
        super("End time should be after start");
    }
}
