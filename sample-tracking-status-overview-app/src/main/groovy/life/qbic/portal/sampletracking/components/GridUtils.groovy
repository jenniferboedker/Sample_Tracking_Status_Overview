package life.qbic.portal.sampletracking.components

import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.Grid
import com.vaadin.ui.UI
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging


/**
 * A helper class with static utility functions for Vaadin Grids.
 *
 * @since 1.0.2
 */
class GridUtils {

    private static Logging log = Logger.getLogger(this.class)

    /**
     * Adds responsiveness to an abstractComponent
     *
     * <p>This applies the css class style .responsive-grid-layout to the provided abstractComponent allowing it to display it's content in a responsive manner</p>
     *
     * @param AbstractComponent the {@link com.vaadin.ui.AbstractComponent}, where the css style and responsiveness should be added
     * @since 1.0.2
     */
    static void setupLayoutResponsiveness(AbstractComponent abstractComponent) {
        try {
            abstractComponent.addStyleName("responsive-grid-layout")
            abstractComponent.setWidthFull()
        } catch (IllegalStateException illegalStateException) {
            log.error("Provided component could not be made responsive . $illegalStateException.message")
            log.debug("Provided component does not support being set to responsive. $illegalStateException.message", illegalStateException)
        }
    }

    /**
     * Disables the possibility of manually resizing vaadin columns in a grid
     *
     * Manual resizing of grid columns in vaadin 8 disables the automatic calculation of column width upon screen resizing,
     * which either causes the grid to flow outside of the screen if the column width is increased or leave empty grid space if column width is decreased
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, for which the columns should be non-resizable
     *
     * @since 1.0.2
     */
    static void makeGridNonResizable(Grid grid) {
        grid.getColumns().each { it ->
            {
                it.setResizable(false)
            }
        }
    }

    /**
     * Makes sure that the full grid width is used by the columns upon browser resizing
     *
     * Ensures that the grid width is allocated upon all the columns even if max-width attributes for individual columns are set
     * Without this recalculation the unused column width would show as empty space on the right side of the grid
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, which should be responsive to browser window resizing
     *
     * @since 1.0.2
     */

    static void makeGridResponsive(Grid grid) {
        grid.addAttachListener(attachEvent -> {
            UI.getCurrent().getPage().addBrowserWindowResizeListener(resizeEvent -> {
                grid.recalculateColumnWidths()
            })
        })
    }
}
