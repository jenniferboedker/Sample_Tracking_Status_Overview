package life.qbic.portal.sampletracking.resource.status

import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <b>A resource service holding data on sample status counts per project</b>
 *
 * <p>This service offers functionality to publish status counts for a specific project.</p>
 *
 * @since 1.0.0
 */
class StatusCountResourceService extends ResourceService<StatusCount>{

    StatusCountResourceService() {
        this.addTopic(Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
        this.addTopic(Topic.SAMPLE_PASSED_QC_COUNT_UPDATE)
        this.addTopic(Topic.SAMPLE_FAILED_QC_COUNT_UPDATE)
        this.addTopic(Topic.SAMPLE_DATA_AVAILABLE_COUNT_UPDATE)
        this.addTopic(Topic.SAMPLE_LIBRARY_PREP_FINISHED)
    }

    /**
     * {@inheritDoc}
     * <p>An {@link IllegalArgumentException} is thrown in case the status is unknown to the service</p>
     * @param statusCount the status count to add
     * @throws IllegalArgumentException in case the status is unknown to the service
     */
    @Override
    void addToResource(StatusCount statusCount) throws IllegalArgumentException {
        switch (statusCount.status) {
            case Status.SAMPLE_RECEIVED:
                publish(statusCount, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
                content.add(statusCount)
                break
            case Status.SAMPLE_QC_PASS:
                publish(statusCount, Topic.SAMPLE_PASSED_QC_COUNT_UPDATE)
                content.add(statusCount)
                break
            case Status.SAMPLE_QC_FAIL:
                publish(statusCount, Topic.SAMPLE_FAILED_QC_COUNT_UPDATE)
                content.add(statusCount)
                break
            case Status.DATA_AVAILABLE:
                publish(statusCount, Topic.SAMPLE_DATA_AVAILABLE_COUNT_UPDATE)
                content.add(statusCount)
                break
            case Status.LIBRARY_PREP_FINISHED:
                publish(statusCount, Topic.SAMPLE_LIBRARY_PREP_FINISHED)
                content.add(statusCount)
                break
            default:
                throw new IllegalArgumentException("Could not process status count for status: $statusCount.status")
                break
        }
    }

    /**
     * {@inheritDoc}
     * <p>An {@link IllegalArgumentException} is thrown in case the status is unknown to the service</p>
     * @param statusCount the status count to remove
     * @throws IllegalArgumentException in case the status is unknown to the service
     */
    @Override
    void removeFromResource(StatusCount statusCount) throws IllegalArgumentException {
        switch (statusCount.status) {
            case Status.SAMPLE_RECEIVED:
                publish(statusCount, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
                content.remove(statusCount)
                break
            case Status.SAMPLE_QC_PASS:
                publish(statusCount, Topic.SAMPLE_PASSED_QC_COUNT_UPDATE)
                content.remove(statusCount)
                break
            case Status.SAMPLE_QC_FAIL:
                publish(statusCount, Topic.SAMPLE_FAILED_QC_COUNT_UPDATE)
                content.remove(statusCount)
                break
            case Status.DATA_AVAILABLE:
                publish(statusCount, Topic.SAMPLE_DATA_AVAILABLE_COUNT_UPDATE)
                content.remove(statusCount)
                break
            case Status.LIBRARY_PREP_FINISHED:
                publish(statusCount, Topic.SAMPLE_LIBRARY_PREP_FINISHED)
                content.remove(statusCount)
                break
            default:
                throw new IllegalArgumentException("Could not process status count for status: $statusCount.status")
                break
        }
    }
}
