package life.qbic.portal.sampletracking;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import java.util.Objects;
import java.util.Optional;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.system.SystemContext;
import life.qbic.portal.sampletracking.system.TestingSystemContext;

@Theme("mytheme")
public class StatusOverviewApp extends QBiCPortletUI {

  @Override
  protected Layout getPortletContent(VaadinRequest request) {
    DependencyManager dependencyManager = new DependencyManager(loadUser());
    return dependencyManager.getPortletView();
  }

  private static PortalUser loadUser() {
    return determinePortalUser().orElseThrow(() ->
        new RuntimeException("Could not determine portal user.")
    );
  }

  private static Optional<PortalUser> determinePortalUser() {
    if(Objects.equals(System.getProperty("environment"), "testing")) {
      return TestingSystemContext.getUser();
    } else {
      return SystemContext.getUser();
    }
  }
}
