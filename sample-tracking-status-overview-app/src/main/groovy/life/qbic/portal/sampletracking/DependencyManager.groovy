package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import life.qbic.business.project.load.LoadProjects
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.business.project.load.LoadProjectsInput
import life.qbic.business.project.load.LoadProjectsOutput
import life.qbic.business.project.subscribe.SubscribeProject
import life.qbic.business.project.subscribe.SubscribeProjectOutput
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource
import life.qbic.business.samples.count.CountSamples
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.business.samples.count.CountSamplesOutput
import life.qbic.business.samples.download.DownloadSamples
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.business.samples.download.DownloadSamplesOutput
import life.qbic.business.samples.info.GetSamplesInfo
import life.qbic.business.samples.info.GetSamplesInfoDataSource
import life.qbic.business.samples.info.GetSamplesInfoInput
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.notification.MessageBroker
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.NotificationHandler
import life.qbic.portal.sampletracking.components.projectoverview.CountSamplesPresenter
import life.qbic.portal.sampletracking.components.projectoverview.LoadProjectsPresenter
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.download.ManifestPresenter
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectPresenter
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesView
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.ProjectOverviewController
import life.qbic.portal.sampletracking.datasources.Credentials
import life.qbic.portal.sampletracking.datasources.OpenBisConnector
import life.qbic.portal.sampletracking.datasources.database.DatabaseSession
import life.qbic.portal.sampletracking.datasources.samples.SamplesDbConnector
import life.qbic.portal.sampletracking.datasources.subscriptions.SubscriptionsDbConnector
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
    private GetSamplesInfoDataSource getSamplesInfoDataSource
    private DownloadSamplesDataSource downloadSamplesDataSource
    private SubscriptionDataSource subscriptionDataSource

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

        subscriptionDataSource = new SubscriptionsDbConnector(DatabaseSession.getInstance())
        getSamplesInfoDataSource = openBisConnector
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
        Subscriber currentUser = new Subscriber(portalUser.firstName, portalUser.lastName, portalUser.emailAddress)
        ProjectOverviewViewModel viewModel = new ProjectOverviewViewModel(projectResourceService, statusCountService, currentUser)
        SubscribeProjectController subscribeProjectController = setupSubscribeProjectUseCase()
        DownloadProjectController downloadController = setupDownloadProjectUseCase(viewModel)

        FailedQCSamplesView failedQCSamplesView = new FailedQCSamplesView(notificationService)
        ProjectOverviewController projectOverviewController = setupFailedQCUseCase(failedQCSamplesView.getPresenter())

        ProjectOverviewView view =  new ProjectOverviewView(notificationService, viewModel, downloadController, failedQCSamplesView, projectOverviewController, subscribeProjectController)
        return view
    }

    private ProjectOverviewController setupFailedQCUseCase(GetSamplesInfoOutput output){
        GetSamplesInfo getSamplesInfo = new GetSamplesInfo(downloadSamplesDataSource,getSamplesInfoDataSource, output)
        return new ProjectOverviewController(getSamplesInfo)
    }

    private DownloadProjectController setupDownloadProjectUseCase(ProjectOverviewViewModel viewModel) {
        DownloadSamplesOutput output = new ManifestPresenter(notificationService, viewModel)
        DownloadSamples downloadSamples = new DownloadSamples(downloadSamplesDataSource, output)
        
        return new DownloadProjectController(downloadSamples)
    }

    private SubscribeProjectController setupSubscribeProjectUseCase() {
        SubscribeProjectOutput output = new SubscribeProjectPresenter(notificationService)
        SubscribeProject subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        return new SubscribeProjectController(subscribeProject)
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
            countSamples.countAvailableDataSamples(it)
            countSamples.countLibraryPrepFinishedSamples(it)
        }
    }

        /**
     * Returns the global notification center
     * @return a notification center that handles app notifications
     */
    NotificationHandler getNotificationCenter() {
        return notificationHandler
    }
}
