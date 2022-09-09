package life.qbic.portal.sampletracking.view.notifications;

/**
 * Handles notifications of interest to the user
 *
 * @since 1.1.4
 */
public interface NotificationHandler {

  enum NotificationType {
    SUCCESS,
    FAILURE,
    INFO
  }

  /**
   * handles notifications and makes the user aware of them
   * @param message the message to notify about
   * @param notificationType the notification type
   */
  void handleNotification(String message, NotificationType notificationType);
}
