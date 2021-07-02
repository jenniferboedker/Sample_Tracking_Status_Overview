package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <p>This resource service holds projects.</p>
 * *
 * @since 1.0.0
 */
class ProjectResourceService extends ResourceService<Project> {

    @Override
    <RESOURCE extends Project> void addToResource(RESOURCE resourceItem) {
        content.add(resourceItem)
        ProjectAddedMessage projectAddedMessage = new ProjectAddedMessage(resourceItem)
        publish(projectAddedMessage)
    }

    @Override
    <RESOURCE extends Project> void removeFromResource(RESOURCE resourceItem) {
        ProjectRemovedMessage projectRemovedMessage = new ProjectRemovedMessage(resourceItem)
        publish(projectRemovedMessage)
        content.remove(resourceItem)
    }

    @Override
    void items(List<? extends Project> items) {
        clear()
        for (def item in items) {
            addToResource(item)
        }
    }
}
