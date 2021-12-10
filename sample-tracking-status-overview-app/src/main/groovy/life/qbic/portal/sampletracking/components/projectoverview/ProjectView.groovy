package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.portal.sampletracking.components.ViewModel

class ProjectView extends ProjectDesign{

    private final ViewModel viewModel

    ProjectView(ViewModel viewModel) {
        super()
        this.viewModel = viewModel

        addClickListener()
    }

    private void addClickListener() {
        this.samplesButton.addClickListener({
            viewModel.projectViewEnabled = false
            viewModel.sampleViewEnabled = true
        })

        viewModel.addPropertyChangeListener({
            if(viewModel.projectViewEnabled){
                this.setVisible(true)
                this.samplesButton.setEnabled(true)
            }else{
                this.setVisible(false)
                this.samplesButton.setEnabled(false)
            }
        })
    }
}
