package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.ExternalResource
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.Link
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme

/**
 * <h1>This class generates the layout for the ProductOverview use case</h1>
 *
 * <p>This view will be the entry point for the user and provides an overview of her projects. And the projects overall status.
 * From here, the user can navigate to the StatusOverview.</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewView extends VerticalLayout{

    private Label titleLabel
    private ProjectOverviewViewModel viewModel
    private Grid<ProjectSummary> projectGrid

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(ProjectOverviewViewModel viewModel){
        this.viewModel = viewModel

        initLayout()
        fillGrid()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        Link postmanLink = generateLink()

        projectGrid = new Grid<>()

        this.addComponents(titleLabel,postmanLink, projectGrid)
    }

    private static Link generateLink(){
        Link link = new Link("Download your data with qpostman", new ExternalResource("https://github.com/qbicsoftware/postman-cli#provide-a-file-with-several-qbic-ids"))
        link.setIcon(VaadinIcons.BOOK)
        link.setTargetName("_blank")

        return link
    }

    private void fillGrid(){
        projectGrid.addColumn({ it.code})
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(MAX_CODE_COLUMN_WIDTH)
        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle")
        projectGrid.addColumn({it.samplesReceived})
                .setCaption("Samples Received").setId("SamplesReceived")
        projectGrid.addColumn({it.samplesQcFailed})
                .setCaption("Samples Failed QC").setId("SamplesFailedQc")
        setupDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle")
                .setMinimumWidth(200)
        projectGrid.getColumn("SamplesReceived")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SamplesFailedQc")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
    }
}
