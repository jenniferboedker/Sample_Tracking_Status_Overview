package life.qbic.portal.sampletracking.components

import com.vaadin.ui.HorizontalLayout

/**
 * <b>Provides hotbar controls to be shown in a managing view</b>
 *
 * @since 1.0.0
 */
interface HasHotbar {
    /**
     * Provides hotbar controls to be shown in a managing view
     * @return a horizontal layout with hotbar content
     */
    HorizontalLayout getHotbar()

}