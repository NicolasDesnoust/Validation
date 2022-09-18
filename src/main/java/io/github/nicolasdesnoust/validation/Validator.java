package io.github.nicolasdesnoust.validation;

@FunctionalInterface
public interface Validator<T, S> {
    Notifications validate(T toValidate, S context);

    interface ValidationContext {
    }

    class ValidationException extends RuntimeException {

        private final transient Notifications notifications;

        ValidationException(Notifications notifications) {
            super(notifications.toString());
            this.notifications = notifications;
        }

        public Notifications getNotifications() {
            return notifications;
        }
    }
}
