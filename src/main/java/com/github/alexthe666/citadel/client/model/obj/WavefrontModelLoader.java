package com.github.alexthe666.citadel.client.model.obj;

import com.github.alexthe666.citadel.Citadel;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Map;

/**
 * Common interface for advanced model loading from files, based on file suffix
 * Model support can be queried through the {@link #getSupportedSuffixes()} method.
 * Instances can be created by calling loadModel
 * with a class-loadable-path
 *
 * @author cpw
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WavefrontModelLoader {
    private static Map<String, IModelObjLoader> instances = Maps.newHashMap();

    /**
     * Register a new model handler
     *
     * @param modelHandler The model handler to register
     */
    public static void registerModelHandler(IModelObjLoader modelHandler) {
        for (String suffix : modelHandler.getSuffixes()) {
            instances.put(suffix, modelHandler);
        }
    }

    /**
     * Load the model from the supplied classpath resolvable resource name
     *
     * @param resource The resource name
     * @return A model
     * @throws IllegalArgumentException if the resource name cannot be understood
     * @throws ModelFormatException     if the underlying model handler cannot parse the model format
     */
    public static IModelObj loadModel(ResourceLocation resource) throws IllegalArgumentException, ModelFormatException {
        String name = resource.getPath();
        int i = name.lastIndexOf('.');
        if (i == -1) {
            Citadel.LOGGER.warn("The resource name %s is not valid", resource);
            throw new IllegalArgumentException("The resource name is not valid");
        }
        String suffix = name.substring(i + 1);
        IModelObjLoader loader = instances.get(suffix);
        if (loader == null) {
            Citadel.LOGGER.warn("The resource name %s is not supported", resource);
            throw new IllegalArgumentException("The resource name is not supported");
        }

        return loader.loadInstance(resource);
    }

    public static Collection<String> getSupportedSuffixes() {
        return instances.keySet();
    }


    static {
        registerModelHandler(new ObjModelLoader());
    }
}