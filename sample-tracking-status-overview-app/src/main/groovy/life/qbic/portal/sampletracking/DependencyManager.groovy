package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.sampletracking.datasources.Credentials
import life.qbic.portal.sampletracking.datasources.OpenBisConnector
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService
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

    LoadProjectsDataSource loadProjectsDataSource
    ResourceService<Project> projectResourceService

    private void initializeDependencies() {
        setupServices()
        setupDatabaseConnections()
    }

    private void setupServices() {
        projectResourceService = new ProjectResourceService()
    }

    DependencyManager(PortalUser user) {
        portalUser = user
        // Load the app environment configuration
        configurationManager = ConfigurationManagerFactory.getInstance()

        initializeDependencies()
        portletView = setupPortletView()

        //FIXME remove demo material
        demo()
    }

    private void setupDatabaseConnections() {
        Credentials openBisCredentials = new Credentials(
                user: configurationManager.getDataSourceUser(),
                password: configurationManager.getDataSourcePassword()
        )
        OpenBisConnector openBisConnector = new OpenBisConnector(openBisCredentials, portalUser, configurationManager.getDataSourceUrl() + "/openbis/openbis")
        loadProjectsDataSource = openBisConnector
    }

    VerticalLayout getPortletView() {
        return portletView
    }

    private VerticalLayout setupPortletView() {
        ProjectOverviewView projectOverviewView = createProjectOverviewView()
        return projectOverviewView
    }

    /**
     * Creates a new ProjectOverviewView using
     * <ul>
     *     <li>{@link #projectResourceService}</li>
     *     <li>{@link #loadProjectsDataSource}</li>
     * </ul>
     * @return a new ProjectOverviewView
     */
    private ProjectOverviewView createProjectOverviewView() {
        ProjectOverviewViewModel projectOverviewViewModel = new ProjectOverviewViewModel(projectResourceService)
        return new ProjectOverviewView(projectOverviewViewModel)
    }

    //FIXME remove
    private void demo() {
        Project project1 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 1"), new ProjectCode("QABCD")), "My Awesome Project1").build()
        Project project2 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 2"), new ProjectCode("QABCE")), "My Awesome Project2").build()
        Project project3 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 3"), new ProjectCode("QABCF")), "My Awesome Project3").build()

        projectResourceService.addToResource(project1)
        projectResourceService.addToResource(project2)
        projectResourceService.addToResource(project3)
    }

}
