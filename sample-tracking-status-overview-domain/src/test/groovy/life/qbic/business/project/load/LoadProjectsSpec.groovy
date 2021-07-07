package life.qbic.business.project.load

import life.qbic.business.DataSourceException
import life.qbic.datamodel.dtos.projectmanagement.Project
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
