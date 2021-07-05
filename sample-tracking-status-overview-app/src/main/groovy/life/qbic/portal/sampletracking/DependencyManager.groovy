package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory

/**
 * <h1>Class that manages all the dependency injections and class instance creations</h1>
 *
 * <p>This class has access to all classes that are instantiated at setup. It is responsible to construct
 * and provide every instance with it's dependencies injected. The class should only be accessed once upon
 * portlet creation and shall not be used later on in the control flow.</p>
 *
 * @since 1.0.0
 *
*/
class DependencyManager {
    VerticalLayout portletView
    ConfigurationManager configurationManager
    PortalUser portalUser

    DependencyManager(PortalUser user) {
        // Load the app environment configuration
        portalUser = user
        configurationManager = ConfigurationManagerFactory.getInstance()
        ProjectOverviewViewModel viewModel = new ProjectOverviewViewModel()
        portletView = new ProjectOverviewView(viewModel)
    }

    VerticalLayout getPortletView() {
        return portletView
    }


}