package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.resource.ResourceMessage
import spock.lang.Specification

/**
 * <p>Tests that the project resource service functions as expected</p>
 */
class ProjectResourceServiceSpec extends Specification {

    ProjectResourceService projectResourceService = new ProjectResourceService()
    Subscriber<ResourceMessage<Project>> subscriber1 = Mock()
    Subscriber<ResourceMessage<Project>> subscriber2 = Mock()

    def "Clear on the service informs all subscribers"() {
        when: "the service resources are cleared"
        projectResourceService.addToResource(getFakeProject("project1"))
        projectResourceService.addToResource(getFakeProject("project2"))
        projectResourceService.subscribe(subscriber1)
        projectResourceService.subscribe(subscriber2)
        projectResourceService.clear()
        then: "all subscribers subscribed to the service are informed"
        2 * subscriber1.receive(_ as ProjectRemovedMessage)
        2 * subscriber2.receive(_ as ProjectRemovedMessage)
    }

    def "Items adds all items and removes all old items and informs all subscribers"() {
        given: "a service that has content and subscribers"
        projectResourceService.addToResource(getFakeProject("project1"))
        projectResourceService.addToResource(getFakeProject("project2"))
        projectResourceService.subscribe(subscriber1)
        projectResourceService.subscribe(subscriber2)

        when: "the service resources are repopulated"
        def newProjects = [getFakeProject("project3")]
        projectResourceService.items(newProjects)

        then: "all subscribers subscribed to the service are informed"
        def contentAfterManipulation = projectResourceService.iterator().toList()
        2 * subscriber1.receive(_ as ProjectRemovedMessage)
        1 * subscriber1.receive(_ as ProjectAddedMessage)
        2 * subscriber2.receive(_ as ProjectRemovedMessage)
        1 * subscriber2.receive(_ as ProjectAddedMessage)
        and: "the service only holds the last project"
        contentAfterManipulation.size() == newProjects.size()
        projectResourceService.iterator().collect().containsAll(newProjects)
    }

    static Project getFakeProject(String name) {
        ProjectSpace projectSpace = new ProjectSpace("TEST_SPACE")
        ProjectCode projectCode = new ProjectCode("QABCD")
        Project.Builder projectBuilder = new Project.Builder(new ProjectIdentifier(projectSpace, projectCode), "test")
        return projectBuilder.build()
    }
}
