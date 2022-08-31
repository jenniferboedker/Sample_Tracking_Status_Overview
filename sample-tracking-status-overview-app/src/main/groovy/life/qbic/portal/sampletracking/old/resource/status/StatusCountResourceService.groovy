package life.qbic.portal.sampletracking.old.resource.status

import life.qbic.business.samples.count.StatusCount
import life.qbic.portal.sampletracking.old.communication.Topic
import life.qbic.portal.sampletracking.old.resource.ResourceService

import java.util.function.Predicate
import java.util.function.UnaryOperator

/**
 * <b>A resource service holding data on sample status counts per project</b>
 *
 * <p>This service offers functionality to publish status counts for a specific project.</p>
 *
 * @since 1.0.0
 */
class StatusCountResourceService extends ResourceService<StatusCount>{

    StatusCountResourceService() {
        this.addTopic(Topic.SAMPLE_COUNT_UPDATE)
    }

    /**
     * {@inheritDoc}
     * <p>An {@link IllegalArgumentException} is thrown in case the status is unknown to the service</p>
     * @param statusCount the status count to add
     * @throws IllegalArgumentException in case the status is unknown to the service
     */
    @Override
    void addToResource(StatusCount statusCount) throws IllegalArgumentException {
        content.add(statusCount)
        publish(statusCount, Topic.SAMPLE_COUNT_UPDATE)
    }

    /**
     * {@inheritDoc}
     * <p>An {@link IllegalArgumentException} is thrown in case the status is unknown to the service</p>
     * @param statusCount the status count to remove
     * @throws IllegalArgumentException in case the status is unknown to the service
     */
    @Override
    void removeFromResource(StatusCount statusCount) throws IllegalArgumentException {
        content.remove(statusCount)
        publish(statusCount, Topic.SAMPLE_COUNT_UPDATE)
    }

    /**
     * <p><b>This method is not implemented. This service does not do anything when you call this method.</b></p>
     * @since 1.0.0
     */
    @Override
    void replace(Predicate<StatusCount> criteria, UnaryOperator<StatusCount> operator) {
        throw new UnsupportedOperationException("${this.class.getSimpleName()} does not support replacement.")
    }
}
