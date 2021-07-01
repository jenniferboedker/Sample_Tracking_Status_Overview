package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.resource.ResourceMessage

/**
 * <p>This message is to be expected to be sent
 * whenever an item was added to a resource holding projects.</p>
 *
 * @since 1.0.0
 */
class ProjectAddedMessage extends ResourceMessage<Project> {

    ProjectAddedMessage(Project payload) {
        super(payload, MessageType.ADDED)
    }
}
