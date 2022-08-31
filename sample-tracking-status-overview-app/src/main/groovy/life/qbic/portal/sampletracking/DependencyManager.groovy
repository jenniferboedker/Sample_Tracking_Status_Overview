package life.qbic.portal.sampletracking

import com.vaadin.ui.VerticalLayout
import groovy.transform.CompileStatic
import life.qbic.business.project.load.LastChangedDateDataSource
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.business.project.load.SubscribedProjectsDataSource
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.business.samples.info.GetSamplesInfoDataSource
import life.qbic.business.samples.info.SampleStatusDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.portal.sampletracking.components.projects.OpenBisProjectRepository
import life.qbic.portal.sampletracking.components.projects.ProjectRepository
import life.qbic.portal.sampletracking.components.projects.ProjectView
import life.qbic.portal.sampletracking.components.projects.SampleStatusSummaryProvider
import life.qbic.portal.sampletracking.components.projects.viewmodel.ProjectStatus
import life.qbic.portal.sampletracking.components.samples.viewmodel.SampleStatus
import life.qbic.portal.sampletracking.old.datasources.Credentials
import life.qbic.portal.sampletracking.old.datasources.database.DatabaseSession
import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory

import java.time.Instant
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
    private VerticalLayout portletView
    private ConfigurationManager configurationManager
    private final PortalUser portalUser

    private LoadProjectsDataSource loadProjectsDataSource
    private LastChangedDateDataSource lastChangedDateDataSource
    private CountSamplesDataSource countSamplesDataSource
    private GetSamplesInfoDataSource getSamplesInfoDataSource
    private DownloadSamplesDataSource downloadSamplesDataSource
    private SubscriptionDataSource subscriptionDataSource
    private SubscribedProjectsDataSource subscribedProjectsDataSource
    private SampleStatusDataSource sampleStatusDataSource

    private Subscriber subscriptionUser

    private static final int PROJECT_LOADING_THREAD_COUNT = 2;
    private final ExecutorService executorService = Executors.newFixedThreadPool(PROJECT_LOADING_THREAD_COUNT)

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
    }


    private void setupDatabaseConnections() {
        String user = requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
        String password = requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
        String host = requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
        String port = requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
        String sqlDatabase = requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)
    }

    /**
     * @return the main view of the application
     * @since 1.0.0
     */
    VerticalLayout getPortletView() {
        return new ProjectView(Executors.newFixedThreadPool(1), getSampleStatusSummaryProvider(), getProjectRepository())
    }

  public static class DummyTrackingProvider implements TrackingStatusProvider {

    private final Map<String, ProjectStatus> knownStatuses = new HashMap<>();

    @Override
    public ProjectStatus getForProject(String projectCode) {
      def knownStatus = knownStatuses.get(projectCode);
      if (Objects.nonNull(knownStatus)) {
        return knownStatus;
      }
      sleep(new Random().nextInt(100) * 10)
      def status = randomStatus()
      knownStatuses.put(projectCode, status)
      return status
    }

    private ProjectStatus randomStatus() {
      def someNumber = new Random().nextInt(10) * new Random().nextInt(100)
      if (new Random().nextBoolean()) {
        return new ProjectStatus(someNumber, someNumber, 1, 0, 0, 0, Instant.MIN)
      } else {
        return new ProjectStatus(someNumber, someNumber, someNumber, someNumber, someNumber, someNumber, Instant.MIN)

      }
    }

    @Override
    public SampleStatus getForSample(String sampleCode) {
      return new SampleStatus("METADATA_REGISTERED")
    }
  }

    ProjectRepository getProjectRepository() {
        Credentials openBisCredentials = new Credentials(
                user: configurationManager.getDataSourceUser(),
                password: configurationManager.getDataSourcePassword()
        )
        return new OpenBisProjectRepository(openBisCredentials, portalUser, configurationManager.getDataSourceUrl() + "/openbis/openbis")
//        ProjectRepository projectRepository = () -> [new Project("QABCD", "bla test project")]
//        return projectRepository
    }

  SampleStatusSummaryProvider getSampleStatusSummaryProvider() {
    return new SampleStatusSummaryProvider(executorService, getTrackingStatusProvider())
  }

  TrackingStatusProvider getTrackingStatusProvider() {
    return new DummyTrackingProvider()
  }
}
