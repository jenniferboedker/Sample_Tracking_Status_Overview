package life.qbic.portal.sampletracking.old.components.visibility

/**
 * <b>VisibilityChangeListener to handle changes in component visibility</b>
 *
 * @since 1.0.0
 */
@FunctionalInterface
interface VisibilityChangeListener {

    /**
     * Called when the visibility of a component has changed.
     * @param event an event containing information about the change
     * @since 1.0.0
     */
    void visibilityChanged(VisibilityChangeEvent event)
}
