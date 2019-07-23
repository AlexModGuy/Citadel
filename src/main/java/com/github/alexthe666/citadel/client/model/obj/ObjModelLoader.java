package com.github.alexthe666.citadel.client.model.obj;

import net.minecraft.util.ResourceLocation;

public class ObjModelLoader implements IModelObjLoader {

    @Override
    public String getType() {
        return "OBJ model";
    }

    private static final String[] types = {"obj"};

    @Override
    public String[] getSuffixes() {
        return types;
    }

    @Override
    public IModelObj loadInstance(ResourceLocation resource) throws ModelFormatException {
        return new WavefrontObject(resource);
    }
}