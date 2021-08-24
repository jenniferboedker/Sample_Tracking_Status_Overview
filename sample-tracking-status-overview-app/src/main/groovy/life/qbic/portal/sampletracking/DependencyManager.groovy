package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import life.qbic.business.project.load.LoadProjects
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.business.project.load.LoadProjectsInput
import life.qbic.business.project.load.LoadProjectsOutput
import life.qbic.business.samples.count.CountSamples
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.business.samples.count.CountSamplesOutput
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.notification.MessageBroker
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.NotificationHandler
import life.qbic.portal.sampletracking.components.projectoverview.CountSamplesPresenter
import life.qbic.portal.sampletracking.components.projectoverview.LoadProjectsPresenter
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.sampletracking.datasources.Credentials
import life.qbic.portal.sampletracking.datasources.OpenBisConnector
import life.qbic.portal.sampletracking.datasources.database.DatabaseSession
import life.qbic.portal.sampletracking.datasources.samples.SamplesDbConnector
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService
import life.qbic.portal.sampletracking.resource.status.StatusCount
import life.qbic.portal.sampletracking.resource.status.StatusCountResourceService
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
    private VerticalLayout portletView
    private ConfigurationManager configurationManager
    private final PortalUser portalUser
    private final NotificationHandler notificationHandler

    private LoadProjectsDataSource loadProjectsDataSource
    private CountSamplesDataSource countSamplesDataSource
    private DownloadSamplesDataSource downloadSamplesDataSource

    private ResourceService<Project> projectResourceService
    private ResourceService<StatusCount> statusCountService
    private NotificationService notificationService

    DependencyManager(PortalUser user) {
        portalUser = user
        // Load the app environment configuration
        configurationManager = ConfigurationManagerFactory.getInstance()

        initializeDependencies()
        notificationHandler = new NotificationHandler(notificationService)

        populateProjectService()
        portletView = setupPortletView()
        populateStatusCountService()
    }


    private void initializeDependencies() {
        setupDatabaseConnections()
        setupServices()
    }

    private void setupServices() {
        projectResourceService = new ProjectResourceService()
        statusCountService = new StatusCountResourceService()
        notificationService = new MessageBroker()
    }

    private void setupDatabaseConnections() {
        String user = Objects.requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
        String password = Objects.requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
        String host = Objects.requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
        String port = Objects.requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
        String sqlDatabase = Objects.requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)
        SamplesDbConnector samplesDbConnector = new SamplesDbConnector(DatabaseSession.getInstance())
        countSamplesDataSource = samplesDbConnector
        downloadSamplesDataSource = samplesDbConnector

        Credentials openBisCredentials = new Credentials(
                user: configurationManager.getDataSourceUser(),
                password: configurationManager.getDataSourcePassword()
        )
        OpenBisConnector openBisConnector = new OpenBisConnector(openBisCredentials, portalUser, configurationManager.getDataSourceUrl() + "/openbis/openbis")
        loadProjectsDataSource = openBisConnector
    }

    /**
     * @return the main view of the application
     * @since 1.0.0
     */
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
     *     <li>{@link #statusCountService}</li>
     * </ul>
     * @return a new ProjectOverviewView
     */
    private ProjectOverviewView createProjectOverviewView() {
        ProjectOverviewViewModel viewModel = new ProjectOverviewViewModel(projectResourceService, statusCountService)
        ProjectOverviewView view =  new ProjectOverviewView(viewModel)
        return view
    }

    /**
     * Triggers the project loading initially to have data in the service
     * This is to be called after the view was initialized
     */
    private void populateProjectService() {
        LoadProjectsOutput output = new LoadProjectsPresenter(projectResourceService, notificationService)
        LoadProjectsInput loadProjects = new LoadProjects(loadProjectsDataSource, output)
        loadProjects.loadProjects()
    }

    /**
     * Triggers the project status count loading initially to have data in the service
     */
    private void populateStatusCountService() {
        CountSamplesOutput output = new CountSamplesPresenter(notificationService, statusCountService)
        CountSamples countSamples = new CountSamples(countSamplesDataSource, output)
        List<String> projectCodes = projectResourceService.iterator().collect {
            return it.projectId.projectCode.toString()
        }
        projectCodes.each {
            countSamples.countReceivedSamples(it)
            countSamples.countQcFailedSamples(it)
        }
    }
    
    /**
     * Triggers the download manifest loading when called
     */
    private void activateDownloadManifestService() {
      ComposeManifestOutput manifestPresenter = new ManifestPresenter(ProjectOverviewViewModel model)
        DownloadSamplesOutput output = new ComposeManifest(manifestPresenter)
        DownloadSamples countSamples = new DownloadSamples(downloadSamplesDataSource, output)
        
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)
        DownloadProjectController downloadController = new DownloadProjectCrontroller(downloadSamples)        
        
        
        //output -> model
        //CreateAffiliationOutput createAffiliationPresenter = new CreateAffiliationPresenter(sharedViewModel, createAffiliationViewModel)
        //use case -> datasource? output
        //CreateAffiliation createAffiliation = new CreateAffiliation(createAffiliationPresenter, dataSource)
        //controller -> use case
        //CreateAffiliationController createAffiliationController = new CreateAffiliationController(createAffiliation)
        //View -> Controller, Model
        //return new CreateAffiliationView(sharedViewModel, createAffiliationViewModel, createAffiliationController)
        
        //model -> presenter -> use case -> controller -> view
        //model -> (presenter -> use case2) -> use case -> controller -> view
    }

        /**
     * Returns the global notification center
     * @return a notification center that handles app notifications
     */
    NotificationHandler getNotificationCenter() {
        return notificationHandler
    }
}
