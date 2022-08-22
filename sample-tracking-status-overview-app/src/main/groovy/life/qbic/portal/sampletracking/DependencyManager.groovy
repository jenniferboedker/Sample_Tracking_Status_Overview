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
import life.qbic.portal.sampletracking.components.MainPage
import life.qbic.portal.sampletracking.components.NotificationHandler
import life.qbic.portal.sampletracking.components.ViewModel
import life.qbic.portal.sampletracking.components.projectoverview.CountSamplesPresenter
import life.qbic.portal.sampletracking.components.projectoverview.LoadProjectsPresenter
import life.qbic.portal.sampletracking.components.projectoverview.ProjectView
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.download.ManifestPresenter
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectPresenter
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewController
import life.qbic.portal.sampletracking.components.sampleoverview.SampleView
import life.qbic.portal.sampletracking.datasources.Credentials
import life.qbic.portal.sampletracking.datasources.OpenBisConnector
import life.qbic.portal.sampletracking.datasources.database.DatabaseSession
import life.qbic.portal.sampletracking.datasources.samples.SamplesDbConnector
import life.qbic.portal.sampletracking.datasources.subscriptions.SubscriptionsDbConnector
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService
import life.qbic.portal.sampletracking.resource.status.StatusCountResourceService
import life.qbic.portal.sampletracking.services.sample.SampleTracking
import life.qbic.portal.sampletracking.services.sample.SampleTrackingService
import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory

import static java.util.Objects.requireNonNull

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
        String user = requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
        String password = requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
        String host = requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
        String port = requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
        String sqlDatabase = requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)
        SampleTrackingService service = setUpTrackingService()
        SamplesDbConnector samplesDbConnector = new SamplesDbConnector(DatabaseSession.getInstance(),service)
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

    private SampleTrackingService setUpTrackingService(){
        String serviceURL = requireNonNull(configurationManager.getServicesRegistryUrl())
        def user = requireNonNull(configurationManager.getServiceUser())

        Credentials credentials = new Credentials(
                user: user.name,
                password: user.password
        )

        return new SampleTracking(serviceURL, credentials)
    }

    /**
     * @return the main view of the application
     * @since 1.0.0
     */
    VerticalLayout getPortletView() {
        return portletView
    }

    private VerticalLayout setupPortletView() {
        ViewModel viewModel = new ViewModel(projectResourceService, statusCountService)
        SampleView sampleView = new SampleView(viewModel, notificationService)
        ProjectView projectView = new ProjectView(viewModel, setupSubscribeProjectUseCase(), notificationService, subscriptionUser, setupDownloadProjectUseCase(viewModel))
        SampleOverviewController sampleOverviewController = setupProjectSamplesUseCase(sampleView.getPresenter())

        MainPage mainPage = new MainPage(projectView, sampleView, viewModel, sampleOverviewController)

        return mainPage
    }

    private SampleOverviewController setupProjectSamplesUseCase(GetSamplesInfoOutput output) {
        GetSamplesInfo getSamplesInfo = new GetSamplesInfo(sampleStatusDataSource, downloadSamplesDataSource, getSamplesInfoDataSource, output)
        return new SampleOverviewController(getSamplesInfo)
    }

    private DownloadProjectController setupDownloadProjectUseCase(ViewModel viewModel) {
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
        LoadProjectsInput loadProjects = new LoadProjects(loadProjectsDataSource, output, lastChangedDateDataSource, subscribedProjectsDataSource)
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
