package life.qbic.portal.sampletracking.components

import com.vaadin.server.ClientConnector
import com.vaadin.ui.Grid

/**
 * A helper class with static utility functions for Vaadin Grids.
 *
 * @since 1.0.2
 */
class GridUtilsImpl implements GridUtils {

    @Override
    void enableResizableColumns(Grid grid) {
        grid.getColumns().each { it ->
            {
                it.setResizable(true)
            }
        }
    }

    @Override
    void disableResizableColumns(Grid grid) {
        grid.getColumns().each { it ->
            {
                it.setResizable(false)
            }
        }
    }

    @Override
    void enableDynamicResizing(Grid grid) {
        grid.addAttachListener(attachEvent -> {
            grid.getUI().getCurrent().getPage().addBrowserWindowResizeListener(resizeEvent -> {
                grid.recalculateColumnWidths()
            })
        })
    }

    @Override
    void disableDynamicResizing(Grid grid) {
        Collection<ClientConnector.AttachListener> attachListeners = grid.getListeners(ClientConnector.AttachListener) as Collection<ClientConnector.AttachListener>
        attachListeners.each {attachListener -> {
            grid.removeAttachListener(attachListener)
        }}
    }
}
