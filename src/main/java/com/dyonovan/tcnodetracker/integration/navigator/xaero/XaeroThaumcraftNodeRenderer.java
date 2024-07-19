package com.dyonovan.tcnodetracker.integration.navigator.xaero;

import com.dyonovan.tcnodetracker.integration.navigator.ThaumcraftNodeLocation;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;
import com.gtnewhorizons.navigator.api.xaero.renderers.XaeroInteractableLayerRenderer;

import java.util.ArrayList;
import java.util.List;

public class XaeroThaumcraftNodeRenderer extends XaeroInteractableLayerRenderer {

    public XaeroThaumcraftNodeRenderer(InteractableLayerManager manager) {
        super(manager);
    }

    @Override
    protected List<ThaumcraftNodeRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
        final List<ThaumcraftNodeRenderStep> renderSteps = new ArrayList<>();
        visibleElements.stream().map(element -> (ThaumcraftNodeLocation) element)
                .forEach(location -> renderSteps.add(new ThaumcraftNodeRenderStep(location)));
        return renderSteps;
    }
}
