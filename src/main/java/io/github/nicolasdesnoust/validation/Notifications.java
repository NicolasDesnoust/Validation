package io.github.nicolasdesnoust.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record Notifications(List<Notification> notifications) {

    public static Notifications NONE = new Notifications(Collections.emptyList());

    public static NotificationsBuilder builder() {
        return new NotificationsBuilder();
    }

    public static Notifications singleInfo(String message, Object... args) {
        return Notifications.builder()
                .withNotification(Notification.info(message, args))
                .build();
    }

    public static Notifications singleWarn(String message, Object... args) {
        return Notifications.builder()
                .withNotification(Notification.warn(message, args))
                .build();
    }

    public static Notifications singleError(String message, Object... args) {
        return Notifications.builder()
                .withNotification(Notification.error(message, args))
                .build();
    }

    public Notifications throwOnAnyError() {
        if (hasAnyError()) {
            throw new Validator.ValidationException(this);
        }

        return this;
    }

    public boolean hasAnyError() {
        return notifications.stream()
                .anyMatch(Notification::isAnError);
    }

    public Notifications and(Notifications other) {
        List<Notification> concatenatedNotifications = Stream.concat(
                this.notifications.stream(),
                other.notifications.stream()
        ).toList();

        return new Notifications(concatenatedNotifications);
    }

    public static class NotificationsBuilder {
        private final List<Notification> notifications;

        private NotificationsBuilder() {
            this.notifications = new ArrayList<>();
        }

        public NotificationsBuilder withNotification(Notification notification) {
            this.notifications.add(notification);
            return this;
        }

        public NotificationsBuilder withNotifications(List<Notification> notifications) {
            this.notifications.addAll(notifications);
            return this;
        }

        public NotificationsBuilder withNotifications(Notification... notifications) {
            this.notifications.addAll(List.of(notifications));
            return this;
        }

        public Notifications build() {
            return new Notifications(notifications);
        }
    }
}
