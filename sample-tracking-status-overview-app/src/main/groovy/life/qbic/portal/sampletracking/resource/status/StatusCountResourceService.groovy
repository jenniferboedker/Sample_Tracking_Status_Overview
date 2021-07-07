package life.qbic.portal.sampletracking.resource.status

import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <b>A resource service holding data on status counts per project</b>
 *
 * <p>This service offers functionality to publish status counts for a specific project.</p>
 *
 * @since 1.0.0
 */
class StatusCountResourceService extends ResourceService<StatusCount>{

    StatusCountResourceService() {
        this.addTopic(Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
    }

    @Override
    void addToResource(StatusCount statusCount) {
        content.add(statusCount)
        switch (statusCount.status) {
            case Status.SAMPLE_RECEIVED:
                publish(statusCount, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
                break
            default:
                // this is an unknown status. nothing is to be done
                break
        }
    }

    @Override
    void removeFromResource(StatusCount statusCount) {
        content.remove(statusCount)
        switch (statusCount.status) {
            case Status.SAMPLE_RECEIVED:
                publish(statusCount, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
                break
            default:
                // this is an unknown status. nothing is to be done
                break
        }
    }
}
