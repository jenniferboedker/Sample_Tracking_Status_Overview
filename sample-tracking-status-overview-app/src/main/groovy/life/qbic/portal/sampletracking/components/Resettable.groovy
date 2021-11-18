package life.qbic.portal.sampletracking.components

/**
 * <b>Should be implemented by all views that are resettable</b>
 *
 * @since 1.0.0
 */
interface Resettable {

    /**
     * Resets the underlying viewmodel of the implementing view
     */
    void reset()

}