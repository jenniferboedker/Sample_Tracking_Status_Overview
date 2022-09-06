package life.qbic.portal.sampletracking.view;

import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.sampletracking.view.notifications.NotificationCenter;
import life.qbic.portal.sampletracking.view.projects.ProjectView;
import life.qbic.portal.sampletracking.view.samples.SampleView;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class MainView extends VerticalLayout {

  private final ProjectView projectView;
  private final SampleView sampleView;

  public MainView(ProjectView projectView, SampleView sampleView,
      NotificationCenter notificationCenter) {
    this.projectView = projectView;
    this.sampleView = sampleView;
    projectView.setVisible(true);
    sampleView.setVisible(false);
    this.setMargin(false);
    this.addComponents(projectView, sampleView, notificationCenter);
    makeThisScrollable();
    projectView.addSampleViewRequestedListener(
        it -> showSampleView(it.projectCode()));
    sampleView.addProjectViewRequestedListener(it -> showProjectView());
  }

  private void makeThisScrollable() {
    this.setSizeFull();
    this.addStyleName("scrollable-layout");
  }

  private void showSampleView(String projectCode) {
    sampleView.setProjectCode(projectCode);
    projectView.setVisible(false);
    sampleView.setVisible(true);
  }

  private void showProjectView() {
    sampleView.setVisible(false);
    projectView.setVisible(true);
  }
}
