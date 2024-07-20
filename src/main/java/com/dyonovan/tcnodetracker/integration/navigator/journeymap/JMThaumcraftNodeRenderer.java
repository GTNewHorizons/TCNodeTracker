package com.dyonovan.tcnodetracker.integration.navigator.journeymap;

import java.util.ArrayList;
import java.util.List;

import com.dyonovan.tcnodetracker.integration.navigator.ThaumcraftNodeLocation;
import com.gtnewhorizons.navigator.api.journeymap.render.JMInteractableLayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;
import com.gtnewhorizons.navigator.api.model.steps.RenderStep;

public class JMThaumcraftNodeRenderer extends JMInteractableLayerRenderer {

    public JMThaumcraftNodeRenderer(InteractableLayerManager manager) {
        super(manager);
    }

    @Override
    protected List<? extends RenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
        final List<ThaumcraftNodeDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream().map(element -> (ThaumcraftNodeLocation) element)
                .forEach(location -> drawSteps.add(new ThaumcraftNodeDrawStep(location)));
        return drawSteps;
    }
}
