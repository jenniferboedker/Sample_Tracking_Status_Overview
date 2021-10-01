package life.qbic.business.project.load

import life.qbic.business.DataSourceException
import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber
import spock.lang.Specification

/**
 * <b>Tests the load project use case</b>
 *
 * @since 1.0.0
 */
class LoadProjectsSpec extends Specification {


    def "successful execution of the use case lead to success notifications"() {
        given:
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> { new ArrayList<Project>() }
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, output)
        when:"the use case is run"
        loadProjects.loadProjects()
        then:"a successful message is send"
        1 * output.loadedProjects(_ as List<Project>)
        0 * output.failedExecution(_ as String)
    }

    def "LoadUserProjectsWithSubscriptionsFor successful execution lead to success notifications"() {
        given:
        Subscriber subscriber = new Subscriber("Test", "user", "123@invalid.com")
        SubscribedProjectsDataSource subscribedProjectsDataSource = Stub()
        LoadProjectsDataSource dataSource = Stub()
        Project subscribedProject = new Project("QABCD", "AwesomeProject")
        Project notSubscribedProject = new Project("QBCDE", "AwesomeProject")
        List<Project> projects = [subscribedProject, notSubscribedProject]
        dataSource.fetchUserProjects() >> projects
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber) >> [subscribedProject.code]
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(subscribedProjectsDataSource, dataSource, output)
        when:"the use case is run"
        loadProjects.loadUserProjectsWithSubscriptionsFor(subscriber)
        then:"a successful message is send"
        subscribedProject.hasSubscription && ! notSubscribedProject.hasSubscription
        1 * output.loadedProjects(projects)
        0 * output.failedExecution(_ as String)
    }

    def "unsuccessful execution of the use case lead to failure notifications"() {
        given:
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, output)
        when:"the use case is run"
        loadProjects.loadProjects()
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedProjects(_)
    }


    def "LoadUserProjectsWithSubscriptionsFor unsuccessful execution lead to failure"() {
        given:
        Subscriber subscriber = new Subscriber("Test", "user", "123@invalid.com")
        SubscribedProjectsDataSource subscribedProjectsDataSource = Stub()
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> {throw exception}
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber) >> {throw exception}
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(subscribedProjectsDataSource, dataSource, output)
        when:"the use case is run"
        loadProjects.loadUserProjectsWithSubscriptionsFor(subscriber)
        then:"a failure message is send"
        0 * output.loadedProjects(_ as List<String>)
        1 * output.failedExecution(_ as String)
        where:
        exception << [new RuntimeException(), new DataSourceException("Test")]
    }

    def "a DataSourceException leads to a failure notification and no projects being loaded"() {
        given:
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> {
            throw new DataSourceException("Testing data source exception")
        }
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, output)

        when:"the use case is run"
        loadProjects.loadProjects()

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedProjects(_)
    }
}
