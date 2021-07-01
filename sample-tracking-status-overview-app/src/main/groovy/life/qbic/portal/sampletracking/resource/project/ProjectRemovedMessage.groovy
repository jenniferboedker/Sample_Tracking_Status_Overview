package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.resource.ResourceMessage

/**
 * <p>This message is to be expected to be sent
 * whenever an item was removed from a resource holding projects.</p>
 *
 * @since 1.0.0
 */
class ProjectRemovedMessage extends ResourceMessage<Project> {
    ProjectRemovedMessage(Project payload) {
        super(payload, MessageType.REMOVED)
    }
}
