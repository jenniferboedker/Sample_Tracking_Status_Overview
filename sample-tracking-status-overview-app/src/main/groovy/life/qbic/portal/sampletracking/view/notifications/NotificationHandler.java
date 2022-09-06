package life.qbic.portal.sampletracking.view.notifications;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface NotificationHandler {

  enum NotificationStyle {
    SUCCESS,
    FAILURE,
    INFO
  }

  void handleNotification(String message, NotificationStyle notificationStyle);
}
