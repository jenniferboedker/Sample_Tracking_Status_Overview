package life.qbic.portal.sampletracking.components.download

import com.vaadin.ui.TextArea
import com.vaadin.ui.VerticalLayout

/**
 * <b>View presenting the output for download requests</b>
 *
 * <p>This view presents the output for a download request. It displays a postman manifest file.</p>
 *
 * @since 0.3.0
 */
class DownloadView extends VerticalLayout {

    private final TextArea manifestContent

    DownloadView() {
        this.manifestContent = createManifestContent()
        this.setMargin(false)
        this.setSpacing(false)
    }

    private static TextArea createManifestContent() {
        TextArea manifestContent = new TextArea()
        manifestContent.setWordWrap(false)
        manifestContent.setReadOnly(true)
    }


}
