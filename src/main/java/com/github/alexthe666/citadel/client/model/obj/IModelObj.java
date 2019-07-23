package com.github.alexthe666.citadel.client.model.obj;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IModelObj {
    String getType();

    @OnlyIn(Dist.CLIENT)
    void renderAll();

    @OnlyIn(Dist.CLIENT)
    void renderOnly(String... groupNames);

    @OnlyIn(Dist.CLIENT)
    void renderPart(String partName);

    @OnlyIn(Dist.CLIENT)
    void renderAllExcept(String... excludedGroupNames);
}