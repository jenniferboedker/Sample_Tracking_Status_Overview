package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
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
    Button postmanLink

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(ProjectOverviewViewModel viewModel){
        this.viewModel = viewModel

        initLayout()
        setupButton()
        fillGrid()
        setupListeners()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)

        projectGrid = new Grid<>()

        HorizontalLayout buttonLayout = new HorizontalLayout()
        postmanLink = new Button()
        buttonLayout.addComponent(buttonLayout)
        //todo add download button next to postman link
        this.addComponents(titleLabel,buttonLayout, projectGrid)
    }

    private void setupButton(){
        postmanLink.setIcon(VaadinIcons.QUESTION_CIRCLE)
        postmanLink.setStyleName(ValoTheme.BUTTON_ICON_ONLY + " " + ValoTheme.BUTTON_SMALL + " square")
        postmanLink.setDescription("A manifest is a text file passed to download clients to download selected files of interest. <br>" +
                "Use <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">qpostman</a> to download the project data", ContentMode.HTML)
    }

    private void setupListeners(){
        postmanLink.addClickListener({
            getUI().getPage().open("https://github.com/qbicsoftware/postman-cli#provide-a-file-with-several-qbic-ids","_blank")
        })
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
