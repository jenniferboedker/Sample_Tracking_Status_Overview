package life.qbic.portal.sampletracking.view.notifications;

import com.vaadin.server.Page;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A vaadin component that has no view of itself. It can display notifications if attached to a {@link Page}
 *
 * @since 1.1.4
 */
public class NotificationCenter extends CustomComponent implements NotificationHandler {

  private static final Logger log = LogManager.getLogger(NotificationCenter.class);

  @Override
  public void handleNotification(String message, NotificationType notificationType) {
    if (!isAttached()) {
      log.warn(String.format("Component not attached. Failed to show [%s]: %s", notificationType, message));
    }
    Notification.Type type;
    switch (notificationType) {
      case INFO:
        type = Type.ASSISTIVE_NOTIFICATION;
        break;
      case FAILURE:
        type = Type.ERROR_MESSAGE;
        break;
      case SUCCESS:
        type = Type.HUMANIZED_MESSAGE;
        break;
      default:
        throw new RuntimeException(String.format("Enum value %s not handled", notificationType));
    }
    Page page = Page.getCurrent();
    StyledNotification styledNotification = new StyledNotification(message, type);
    styledNotification.show(page);
  }
}
