package life.qbic.portal.sampletracking.view.projects;

import com.vaadin.ui.CheckBox;
import java.util.HashMap;
import java.util.Map;
import life.qbic.portal.sampletracking.data.Subscription;
import life.qbic.portal.sampletracking.data.SubscriptionRepository;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SubscriptionCheckboxProvider {

  private final SubscriptionRepository subscriptionRepository;

  private final Map<String, CheckBox> checkboxes = new HashMap<>();

  public SubscriptionCheckboxProvider(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
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
          boolean successfullyAdded = subscriptionRepository.add(new Subscription(project.code()));
          checkBox.setValue(successfullyAdded);
        } else {
          boolean successfullyRemoved = subscriptionRepository.remove(new Subscription(project.code()));
          checkBox.setValue(!successfullyRemoved);
        }
      }
    });
    checkboxes.put(project.code(), checkBox);
    return checkBox;
  }

}
