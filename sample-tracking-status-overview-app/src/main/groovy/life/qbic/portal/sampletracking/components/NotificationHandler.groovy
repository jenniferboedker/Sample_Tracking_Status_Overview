package life.qbic.portal.sampletracking.components

import com.vaadin.server.Page
import com.vaadin.ui.Notification.Type
import groovy.util.logging.Log4j2
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.communication.notification.NotificationService

/**
 * <p>Subscribes to notifications and displays them to the user.</p>
 *
 * @since 1.0.0
 */
@Log4j2
class NotificationHandler {

    private final NotificationService notificationService

    NotificationHandler(NotificationService notificationService) {
        this.notificationService = notificationService
        subscribeToService()
    }

    private static void showMessage(String message, Type type) {
        StyledNotification notification = new StyledNotification(message, type)
        if (Page.getCurrent()) {
            notification.show(Page.getCurrent())
        } else {
            log.info("No page loaded. Tried to show [$type]: $message")
        }
    }


    void subscribeToService() {
        notificationService.subscribe({ showMessage(it, Type.ERROR_MESSAGE) }, Topic.NOTIFICATION_FAILURE)
        notificationService.subscribe({ showMessage(it, Type.HUMANIZED_MESSAGE) }, Topic.NOTIFICATION_SUCCESS)
        notificationService.subscribe({ showMessage(it, Type.ASSISTIVE_NOTIFICATION) }, Topic.NOTIFICATION_INFO)
    }
}
