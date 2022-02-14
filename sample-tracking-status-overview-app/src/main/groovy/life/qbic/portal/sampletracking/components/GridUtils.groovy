package life.qbic.portal.sampletracking.components

import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.Grid
import com.vaadin.ui.UI
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <version tag>
 *
 */
class GridUtils {

    private static Logging log = Logger.getLogger(this.class)

    /**
     * Adds responsiveness to an abstractComponent
     *
     * <p>This applies the css class style .responsive-grid-layout to the provided abstract allowing it to respond to specific width breakpoints.</p>
     *
     * @param AbstractComponent the {@link com.vaadin.ui.AbstractComponent}, where the css style and responsiveness should be added
     * @since 1.0.2
     */
    static void setupLayoutResponsiveness(AbstractComponent abstractComponent) {
        try {
            abstractComponent.addStyleName("responsive-grid-layout")
            abstractComponent.setResponsive(true)
        } catch (IllegalStateException illegalStateException) {
            log.error("Provided component could not be made responsive . $illegalStateException.message")
            log.debug("Provided component does not support being set to responsive. $illegalStateException.message", illegalStateException)
        }
    }

    /**
     * Adjusts the width of the grid and it's associated hotbar layout when the grid is manually resized by the user
     *
     * @param AbstractComponent hotbarLayout the {@link com.vaadin.ui.AbstractComponent}
     * @param Grid grid the {@link com.vaadin.ui.Grid},
     *
     * @since 1.0.2
     */
    static void makeGridResponsiveToResize(Grid grid, AbstractComponent hotbarLayout) {

        //The grid width should adjust to the resizing preferences of the user
        try {
            grid.addColumnResizeListener(columnListener -> {
                int maximumPortletWidth = computeMaximumLiferayPortletWidth()
                double columnsWidth = 0
                grid.getColumns().each { column ->
                    columnsWidth += column.getWidth()
                }
                if (columnsWidth <= maximumPortletWidth) {
                    grid.setWidth(Math.floor(columnsWidth).toString())

                } else {
                    //If projectGrid width is bigger than screen adjust grid to max screen size
                    grid.setWidth(maximumPortletWidth.toString())
                }
                if (hotbarLayout) {
                    if (columnsWidth <= maximumPortletWidth) {
                        hotbarLayout.setWidth(Math.floor(columnsWidth).toString())
                    } else {
                        hotbarLayout.setWidth(maximumPortletWidth.toString())
                    }
                }
            })
        } catch (Exception exception) {
            log.error("An error occured during manual grid resizing. $exception.message")
            log.debug("An error occured during manual grid resizing. $exception.message", exception)
        }
    }

    /**
     * Computes the maximum width a component can have in the liferay portal
     * @since 1.0.2
     * @returns maximumPortletWidth maximum width a portlet can take accounting for the liferay margins and paddings
     */

    private static int computeMaximumLiferayPortletWidth() {
        try {
            int browserWindowWidth = UI.getCurrent().getPage().getBrowserWindowWidth()
            //We need to adjust the grid width to the liferay padding
            int maximumPortletWidth = Math.floor(0.88 * browserWindowWidth).toInteger()
            return maximumPortletWidth
        } catch (Exception exception) {
            log.error("Unable to compute browser page width $exception.message")
            log.debug("An error occured during the compuation of maximum possible portlet width $exception.message", exception)
        }
    }

}
