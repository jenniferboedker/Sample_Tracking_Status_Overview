package life.qbic.portal.sampletracking.components

import com.vaadin.server.Sizeable
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.Grid
import com.vaadin.ui.JavaScript
import com.vaadin.ui.JavaScriptFunction
import com.vaadin.ui.Panel
import elemental.json.JsonArray
import elemental.json.impl.JreJsonNumber
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
     * @param AbstractComponent gridHostingLayout the {@link com.vaadin.ui.abstractComponent} into which the grid is hosted,
     * @param Grid grid the {@link com.vaadin.ui.Grid},
     *
     * @since 1.0.2
     */
    static void makeGridResponsiveToResize(AbstractComponent gridHostingLayout, Grid grid) {
        //The grid width should adjust to the resizing preferences of the user

        double maximumBrowserWindowWidth = 0

        try {
            grid.addAttachListener(event -> {
                maximumBrowserWindowWidth = computeGridLayoutWidth(gridHostingLayout)
                JavaScript.getCurrent().execute("getAbsoluteWidth(document.getElementById('Test').clientWidth);")
            })
            grid.addColumnResizeListener(columnResizeEvent -> {
                float columnsWidth = 0
                grid.getColumns().each { column ->
                    columnsWidth += column.getWidth()
                }
                if (columnsWidth <= maximumBrowserWindowWidth) {
                    gridHostingLayout.setWidth(columnsWidth, Sizeable.Unit.PIXELS)
                } else {
                    //If projectGrid width is bigger than screen adjust grid to max screen size
                    gridHostingLayout.setWidth(100, Sizeable.Unit.PERCENTAGE)
                    grid.setWidth(100,Sizeable.Unit.PERCENTAGE)
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

    private static double computeGridLayoutWidth(AbstractComponent abstractComponent) {
        try {
            abstractComponent.setId("Test")
            double gridLayoutWidth = 0
            JavaScriptFunction foo = new JavaScriptFunction() {
                @Override
                void call(JsonArray arguments) {
                    JreJsonNumber width = arguments.get(0)
                    gridLayoutWidth = width.getNumber()
                    println(gridLayoutWidth)
                }
            }
            JavaScript.getCurrent().addFunction("getAbsoluteWidth", foo)
            return gridLayoutWidth
        } catch (Exception exception) {
            log.error("Unable to compute grid maximum width $exception.message")
            log.debug("An error occured during the computation of initial maximum grid width $exception.message", exception)
        }
    }

}
