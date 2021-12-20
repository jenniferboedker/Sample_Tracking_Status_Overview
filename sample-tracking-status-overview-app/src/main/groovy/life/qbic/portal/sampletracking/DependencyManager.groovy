package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import life.qbic.business.project.Project
import life.qbic.business.project.load.*
import life.qbic.business.project.subscribe.SubscribeProject
import life.qbic.business.project.subscribe.SubscribeProjectOutput
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource
import life.qbic.business.samples.count.CountSamples
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.business.samples.count.CountSamplesOutput
import life.qbic.business.samples.count.StatusCount
import life.qbic.business.samples.download.DownloadSamples
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.business.samples.download.DownloadSamplesOutput
import life.qbic.business.samples.info.GetSamplesInfo
import life.qbic.business.samples.info.GetSamplesInfoDataSource
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.business.samples.info.SampleStatusDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.portal.sampletracking.communication.notification.MessageBroker
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.AppView
import life.qbic.portal.sampletracking.components.NotificationHandler
import life.qbic.portal.sampletracking.components.projectoverview.CountSamplesPresenter
import life.qbic.portal.sampletracking.components.projectoverview.LoadProjectsPresenter
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.download.ManifestPresenter
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesController
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesView
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectPresenter
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewView
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewController
import life.qbic.portal.sampletracking.datasources.Credentials
import life.qbic.portal.sampletracking.datasources.OpenBisConnector
import life.qbic.portal.sampletracking.datasources.database.DatabaseSession
import life.qbic.portal.sampletracking.datasources.samples.SamplesDbConnector
import life.qbic.portal.sampletracking.datasources.subscriptions.SubscriptionsDbConnector
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService
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
    private LastChangedDateDataSource lastChangedDateDataSource
    private CountSamplesDataSource countSamplesDataSource
    private GetSamplesInfoDataSource getSamplesInfoDataSource
    private DownloadSamplesDataSource downloadSamplesDataSource
    private SubscriptionDataSource subscriptionDataSource
    private SubscribedProjectsDataSource subscribedProjectsDataSource
    private SampleStatusDataSource sampleStatusDataSource

    private ResourceService<Project> projectResourceService
    private ResourceService<StatusCount> statusCountService
    private NotificationService notificationService
    private Subscriber subscriptionUser

    DependencyManager(PortalUser user) {
        portalUser = user
        subscriptionUser = subscriberFor(portalUser)
        // Load the app environment configuration
        configurationManager = ConfigurationManagerFactory.getInstance()

        initializeDependencies()
        notificationHandler = new NotificationHandler(notificationService)

        populateProjectService()
        portletView = setupPortletView()
        populateStatusCountService()
    }

    private static Subscriber subscriberFor(PortalUser portalUser) {
        return new Subscriber(portalUser.firstName,
                portalUser.lastName,
                portalUser.title.value,
                portalUser.emailAddress)
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
        lastChangedDateDataSource = samplesDbConnector
        sampleStatusDataSource = samplesDbConnector

        Credentials openBisCredentials = new Credentials(
                user: configurationManager.getDataSourceUser(),
                password: configurationManager.getDataSourcePassword()
        )
        OpenBisConnector openBisConnector = new OpenBisConnector(openBisCredentials, portalUser, configurationManager.getDataSourceUrl() + "/openbis/openbis")
        loadProjectsDataSource = openBisConnector
        lastChangedDateDataSource = samplesDbConnector

        subscriptionDataSource = new SubscriptionsDbConnector(DatabaseSession.getInstance())
        getSamplesInfoDataSource = openBisConnector


        SubscriptionsDbConnector subscriptionsDbConnector = new SubscriptionsDbConnector(DatabaseSession.getInstance())
        subscriptionDataSource = subscriptionsDbConnector
        subscribedProjectsDataSource = subscriptionsDbConnector
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
        SampleOverviewView sampleOverviewView = new SampleOverviewView(notificationService)
        SampleOverviewController projectSamplesController = setupProjectSamplesUseCase(sampleOverviewView.getPresenter())


        AppView mainView = new AppView(projectOverviewView, sampleOverviewView, projectSamplesController)
        return mainView
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
        ProjectOverviewViewModel viewModel = new ProjectOverviewViewModel(projectResourceService, statusCountService,
                this.subscriptionUser)
        SubscribeProjectController subscribeProjectController = setupSubscribeProjectUseCase()
        DownloadProjectController downloadController = setupDownloadProjectUseCase(viewModel)

        FailedQCSamplesView failedQCSamplesView = new FailedQCSamplesView(notificationService)
        FailedQCSamplesController failedQCSamplesController = setupFailedQCUseCase(failedQCSamplesView.getPresenter())

        ProjectOverviewView view = new ProjectOverviewView(notificationService,
                viewModel,
                downloadController,
                failedQCSamplesView,
                failedQCSamplesController,
                subscribeProjectController)
        return view
    }

    private SampleOverviewController setupProjectSamplesUseCase(GetSamplesInfoOutput output) {
        GetSamplesInfo getSamplesInfo = new GetSamplesInfo(sampleStatusDataSource, downloadSamplesDataSource, getSamplesInfoDataSource, output)
        return new SampleOverviewController(getSamplesInfo)
    }

    private FailedQCSamplesController setupFailedQCUseCase(GetSamplesInfoOutput output){
        GetSamplesInfo getSamplesInfo = new GetSamplesInfo(sampleStatusDataSource, downloadSamplesDataSource,getSamplesInfoDataSource, output)
        return new FailedQCSamplesController(getSamplesInfo)
    }

    private DownloadProjectController setupDownloadProjectUseCase(ProjectOverviewViewModel viewModel) {
        DownloadSamplesOutput output = new ManifestPresenter(notificationService, viewModel)
        DownloadSamples downloadSamples = new DownloadSamples(downloadSamplesDataSource, output)
        
        return new DownloadProjectController(downloadSamples)
    }

    private SubscribeProjectController setupSubscribeProjectUseCase() {
        SubscribeProjectOutput output = new SubscribeProjectPresenter(projectResourceService, notificationService)
        SubscribeProject subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        return new SubscribeProjectController(subscribeProject)
    }

    /**
     * Triggers the project loading initially to have data in the service
     * This is to be called after the view was initialized
     */
    private void populateProjectService() {
        LoadProjectsOutput output = new LoadProjectsPresenter(projectResourceService, notificationService)
        LoadProjectsInput loadProjects = new LoadProjects(loadProjectsDataSource, output, lastChangedDateDataSource, subscribedProjectsDataSource )
        loadProjects.withSubscriptions(subscriptionUser)
    }

    /**
     * Triggers the project status count loading initially to have data in the service
     */
    private void populateStatusCountService() {
        CountSamplesOutput output = new CountSamplesPresenter(notificationService, statusCountService)
        CountSamples countSamples = new CountSamples(countSamplesDataSource, output)
        List<String> projectCodes = projectResourceService.iterator().collect {
            return it.code
        }
        projectCodes.each {
            countSamples.countSamplesPerStatus(it)
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
