package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import groovy.transform.CompileStatic
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.portal.sampletracking.data.*
import life.qbic.portal.sampletracking.data.database.DatabaseSession
import life.qbic.portal.sampletracking.old.datasources.Credentials
import life.qbic.portal.sampletracking.view.MainView
import life.qbic.portal.sampletracking.view.projects.ProjectStatusComponentProvider
import life.qbic.portal.sampletracking.view.projects.ProjectView
import life.qbic.portal.sampletracking.view.samples.SampleStatusComponentProvider
import life.qbic.portal.sampletracking.view.samples.SampleView
import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
@CompileStatic
class DependencyManager {
    private ConfigurationManager configurationManager
    private final PortalUser portalUser

    private Subscriber subscriptionUser

    private static final int PROJECT_LOADING_THREAD_COUNT = 2
    private static final int SAMPLE_LOADING_THREAD_COUNT = 2
    private final ExecutorService projectLoadingExecutor = Executors.newFixedThreadPool(PROJECT_LOADING_THREAD_COUNT)
    private final ExecutorService sampleLoadingExecutor = Executors.newFixedThreadPool(SAMPLE_LOADING_THREAD_COUNT)


  private OpenBisConnector openBisConnector
  private ProjectStatusComponentProvider projectStatusComponentProvider
  private SampleStatusComponentProvider sampleStatusComponentProvider
  private DummyTrackingConnector dummyTrackingConnector = new DummyTrackingConnector()
  private SampleTrackingConnector sampleTrackingConnector

  DependencyManager(PortalUser user) {
        portalUser = user
        subscriptionUser = subscriberFor(portalUser)
        // Load the app environment configuration
        configurationManager = ConfigurationManagerFactory.getInstance()

        initializeDependencies()
    }

    private static Subscriber subscriberFor(PortalUser portalUser) {
        return new Subscriber(portalUser.firstName,
                portalUser.lastName,
                portalUser.title.value,
                portalUser.emailAddress)
    }


    private void initializeDependencies() {
      setupDatabaseConnections()
      setupOpenBisConnection()
      setupSampleTracking()
    }


    private void setupDatabaseConnections() {
        String user = requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
        String password = requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
        String host = requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
        String port = requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
        String sqlDatabase = requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)
    }

  private void setupOpenBisConnection() {
    Credentials openBisCredentials = new Credentials(
            user: configurationManager.getDataSourceUser(),
            password: configurationManager.getDataSourcePassword()
    )
    this.openBisConnector = new OpenBisConnector(openBisCredentials, portalUser, configurationManager.getDataSourceUrl() + "/openbis/openbis")
  }

  private void setupSampleTracking() {
    if (Objects.nonNull(sampleTrackingConnector)) {
      return
    }
    def credentials = new Credentials(configurationManager.getServiceUser().name,
            configurationManager.getServiceUser().password)
    sampleTrackingConnector = new SampleTrackingConnector(configurationManager.getSampleTrackingServiceUrl(),
            "/v2/samples",
            "/status",
            "/v2/projects",
            credentials)
  }

    /**
     * @return the main view of the application
     * @since 1.0.0
     */
    VerticalLayout getPortletView() {
      def projectView = new ProjectView(getSampleStatusSummaryProvider(), getSubscriptionServiceProvider(), getProjectRepository())
      def sampleView = new SampleView(getSampleRepository(), getSampleStatusComponentProvider())
      return new MainView(projectView, sampleView)
    }

  ProjectRepository getProjectRepository() {
//    ProjectRepository projectRepository = () -> [new Project("QSTTS", "bla"), new Project("QABCD", "bla 2")]
//    return projectRepository
    return openBisConnector
  }

  SampleRepository getSampleRepository() {
    return openBisConnector
  }

  SampleStatusComponentProvider getSampleStatusComponentProvider() {
    if (Objects.nonNull(sampleStatusComponentProvider)) {
      return sampleStatusComponentProvider
    }
    sampleStatusComponentProvider = new SampleStatusComponentProvider(sampleLoadingExecutor, getSampleStatusProvider())
    return sampleStatusComponentProvider
  }

  ProjectStatusComponentProvider getSampleStatusSummaryProvider() {
    if (Objects.nonNull(projectStatusComponentProvider)) {
      return projectStatusComponentProvider
    }
    projectStatusComponentProvider = new ProjectStatusComponentProvider(projectLoadingExecutor, getProjectStatusProvider())
    return projectStatusComponentProvider
  }
  SampleStatusProvider getSampleStatusProvider() {
    return sampleTrackingConnector
  }

  ProjectStatusProvider getProjectStatusProvider() {
    return sampleTrackingConnector
  }

  SubscriptionStatusProvider getSubscriptionServiceProvider() {
    SubscriptionStatusProvider provider = it -> new Random().nextBoolean()
    return provider
  }
}
