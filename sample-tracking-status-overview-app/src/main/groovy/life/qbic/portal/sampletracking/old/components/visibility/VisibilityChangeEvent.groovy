package life.qbic.portal.sampletracking.old.components.visibility

import com.vaadin.ui.Component

/**
 * <b>Signifies a change of visibility</b>
 *
 * <p>The visibility of the source component changed from oldValue to newValue</p>
 *
 * @since 1.0.0
 */
class VisibilityChangeEvent {
    final Component source
    final boolean oldValue, newValue

    VisibilityChangeEvent(Component source, boolean oldValue, boolean newValue) {
        this.source = source
        this.oldValue = oldValue
        this.newValue = newValue
    }
}
