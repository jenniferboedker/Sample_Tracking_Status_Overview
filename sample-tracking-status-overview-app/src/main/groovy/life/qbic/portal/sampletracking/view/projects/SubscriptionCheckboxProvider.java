package life.qbic.portal.sampletracking.view.projects;

import com.vaadin.ui.CheckBox;
import java.util.HashMap;
import java.util.Map;
import life.qbic.portal.sampletracking.data.Subscription;
import life.qbic.portal.sampletracking.data.SubscriptionRepository;
import life.qbic.portal.sampletracking.view.notifications.NotificationHandler;
import life.qbic.portal.sampletracking.view.notifications.NotificationHandler.NotificationStyle;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SubscriptionCheckboxProvider {

  private static final String SUCCESSFULLY_SUBSCRIBED = "Subscription to %s was successful. You will receive emails informing you about updates on %s.";
  private static final String SUCCESSFULLY_UNSUBSCRIBED = "Unsubscribed successfully from %s. You will no longer receive emails informing you about updates on %s.";

  private final SubscriptionRepository subscriptionRepository;
  private final NotificationHandler notificationHandler;

  private final Map<String, CheckBox> checkboxes = new HashMap<>();

  public SubscriptionCheckboxProvider(SubscriptionRepository subscriptionRepository,
      NotificationHandler notificationHandler) {
    this.subscriptionRepository = subscriptionRepository;
    this.notificationHandler = notificationHandler;
  }

  public CheckBox getForProject(Project project) {
    if (checkboxes.containsKey(project.code())) {
      return checkboxes.get(project.code());
    }
    CheckBox checkBox = new CheckBox();
    checkBox.setValue(project.subscribed());
    checkBox.addValueChangeListener(it -> {
      if (it.isUserOriginated()) {
        if (it.getValue()) {
          boolean isAdded = subscriptionRepository.add(new Subscription(project.code()));
          if (isAdded) {
            notificationHandler.handleNotification(String.format(
                    SUCCESSFULLY_SUBSCRIBED, project.code(), project.code()),
                NotificationStyle.SUCCESS);
          }
          checkBox.setValue(isAdded);
        } else {
          boolean isRemoved = subscriptionRepository.remove(new Subscription(project.code()));
          if (isRemoved) {
            notificationHandler.handleNotification(String.format(
                    SUCCESSFULLY_UNSUBSCRIBED, project.code(), project.code()),
                NotificationStyle.SUCCESS);
          }
          checkBox.setValue(!isRemoved);
        }
      }
    });
    checkboxes.put(project.code(), checkBox);
    return checkBox;
  }

}
