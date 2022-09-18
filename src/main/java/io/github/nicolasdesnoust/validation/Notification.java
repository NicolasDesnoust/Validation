package io.github.nicolasdesnoust.validation;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;

public record Notification(String message, Severity severity) implements Serializable {

    @Serial
    private static final long serialVersionUID = -2338626292552177485L;

    public static Notification info(String message, Object... args) {
        return new Notification(String.format(message, args), Severity.INFO);
    }

    public static Notification warn(String message, Object... args) {
        return new Notification(String.format(message, args), Severity.WARNING);
    }

    public static Notification error(String message, Object... args) {
        return new Notification(String.format(message, args), Severity.ERROR);
    }

    @Transient
    public boolean isAnError() {
        return Severity.ERROR.equals(severity);
    }
}
