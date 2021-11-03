package life.qbic.portal.sampletracking.components.projectoverview.visibility
/**
 * <b>VisibilityChangeListener to handle changes in component visibility</b>
 *
 * @since 1.0.0
 */
@FunctionalInterface
interface VisibilityChangeListener {

    /**
     * This event is fired when the visibility of a component is changed
     * @param newValue A boolean describing the new visibility value
     * @param oldValue A boolean describing the old visibility value
     */
    void visibilityChangeEvent(boolean newValue, boolean oldValue)
}