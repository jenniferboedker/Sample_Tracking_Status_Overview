package life.qbic.portal.sampletracking

import com.vaadin.annotations.Theme
import com.vaadin.server.Page
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Layout
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.portal.sampletracking.components.StyledNotification
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel


/**
 * Entry point for the application. This class derives from {@link life.qbic.portal.portlet.QBiCPortletUI}.
 *
 * @since: 0.1.0
 * @author: Jennifer Bödker
 * @see <a href=https://github.com/qbicsoftware/portal-utils-lib>portal-utils-lib</a>
 */
@Theme("mytheme")
@SuppressWarnings("serial")
@Log4j2
@CompileStatic
class StatusOverviewApp extends QBiCPortletUI {

    private DependencyManager dependencyManager

    StatusOverviewApp() {
        super()
        // The constructor MUST NOT fail since the user does not get any feedback otherwise.
        try {
            dependencyManager = new DependencyManager()
        } catch (Exception e) {
            log.error("Could not initialize {}", StatusOverviewApp.getCanonicalName(), e)
        } catch (Error error) {
            log.error("Unexpected runtime error.", error)
        }
    }

    @Override
    protected Layout getPortletContent(final VaadinRequest request) {
        def layout
        log.info "Generating content for class {}", StatusOverviewApp.getCanonicalName()
        try {
            //todo add the app layout here
            ProjectOverviewViewModel viewModel = new ProjectOverviewViewModel()
            layout = new ProjectOverviewView(viewModel) //this.dependencyManager.getPortletView()
        } catch (Exception e) {
            log.error("Failed generating content for class {}", StatusOverviewApp.getCanonicalName())
            log.error(e)
            String errorCaption = "Application not available"
            String errorMessage = "We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com."
            StyledNotification initializationErrorNotification = new StyledNotification
                    (errorCaption, errorMessage, Notification.Type.ERROR_MESSAGE)
            initializationErrorNotification.show(Page.getCurrent())
            layout = new VerticalLayout()
        }
        return layout
    }

}
