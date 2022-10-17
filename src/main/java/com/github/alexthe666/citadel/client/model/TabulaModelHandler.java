package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.container.TabulaCubeContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeGroupContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaModelBlock;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author pau101
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public enum TabulaModelHandler implements JsonDeserializationContext {
    INSTANCE;

    private Gson gson = new GsonBuilder().registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).create();
    private JsonParser parser = new JsonParser();
    private TabulaModelBlock.Deserializer TabulaModelBlockDeserializer = new TabulaModelBlock.Deserializer();
    private ResourceManager manager;
    private final Set<String> enabledDomains = new HashSet<>();

    public void addDomain(String domain) {
        this.enabledDomains.add(domain.toLowerCase(Locale.ROOT));
        //LLibrary.LOGGER.info("TabulaModelHandler: Domain {} has been added.", domain.toLowerCase(Locale.ROOT));
    }

    /**
     * Load a {@link TabulaModelContainer} from the path. A slash will be added if it isn't in the path already.
     *
     * @param path the model path
     * @return the new {@link TabulaModelContainer} instance
     * @throws IOException if the file can't be found
     */
    public TabulaModelContainer loadTabulaModel(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith(".tbl")) {
            path += ".tbl";
        }
        InputStream stream = TabulaModelHandler.class.getResourceAsStream(path);
        return TabulaModelHandler.INSTANCE.loadTabulaModel(this.getModelJsonStream(path, stream));
    }

    /**
     * Load a {@link TabulaModelContainer} from the model.json input stream.
     *
     * @param stream the model.json input stream
     * @return the new {@link TabulaModelContainer} instance
     */
    public TabulaModelContainer loadTabulaModel(InputStream stream) {
        return this.gson.fromJson(new InputStreamReader(stream), TabulaModelContainer.class);
    }

    /**
     * @param name  the cube name
     * @param model the model container
     * @return the cube
     */
    public TabulaCubeContainer getCubeByName(String name, TabulaModelContainer model) {
        List<TabulaCubeContainer> allCubes = this.getAllCubes(model);

        for (TabulaCubeContainer cube : allCubes) {
            if (cube.getName().equals(name)) {
                return cube;
            }
        }

        return null;
    }

    /**
     * @param identifier the cube identifier
     * @param model      the model container
     * @return the cube
     */
    public TabulaCubeContainer getCubeByIdentifier(String identifier, TabulaModelContainer model) {
        List<TabulaCubeContainer> allCubes = this.getAllCubes(model);

        for (TabulaCubeContainer cube : allCubes) {
            if (cube.getIdentifier().equals(identifier)) {
                return cube;
            }
        }

        return null;
    }

    /**
     * @param model the model container
     * @return an array with all cubes of the model
     */
    public List<TabulaCubeContainer> getAllCubes(TabulaModelContainer model) {
        List<TabulaCubeContainer> cubes = new ArrayList<>();

        for (TabulaCubeGroupContainer cubeGroup : model.getCubeGroups()) {
            cubes.addAll(this.traverse(cubeGroup));
        }

        for (TabulaCubeContainer cube : model.getCubes()) {
            cubes.addAll(this.traverse(cube));
        }

        return cubes;
    }

    private List<TabulaCubeContainer> traverse(TabulaCubeGroupContainer group) {
        List<TabulaCubeContainer> retCubes = new ArrayList<>();

        for (TabulaCubeContainer child : group.getCubes()) {
            retCubes.addAll(this.traverse(child));
        }

        for (TabulaCubeGroupContainer child : group.getCubeGroups()) {
            retCubes.addAll(this.traverse(child));
        }

        return retCubes;
    }

    private List<TabulaCubeContainer> traverse(TabulaCubeContainer cube) {
        List<TabulaCubeContainer> retCubes = new ArrayList<>();

        retCubes.add(cube);

        for (TabulaCubeContainer child : cube.getChildren()) {
            retCubes.addAll(this.traverse(child));
        }

        return retCubes;
    }

  /*  @Override
    public void onResourceManagerReload(IResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return this.enabledDomains.contains(modelLocation.getNamespace()) && modelLocation.getPath().endsWith(".tbl");
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
        String modelPath = modelLocation.getPath();
        modelPath = modelPath.substring(0, modelPath.lastIndexOf('.')) + ".json";
        IResource resource = this.manager.getResource(new ResourceLocation(modelLocation.getPath(), modelPath));
        InputStreamReader jsonStream = new InputStreamReader(resource.getInputStream());
        JsonElement json = this.parser.parse(jsonStream);
        jsonStream.close();
        TabulaModelBlock TabulaModelBlock = this.TabulaModelBlockDeserializer.deserialize(json, TabulaModelBlock.class, this);
        String tblLocationStr = json.getAsJsonObject().get("tabula").getAsString() + ".tbl";
        ResourceLocation tblLocation = new ResourceLocation(tblLocationStr);
        IResource tblResource = this.manager.getResource(tblLocation);
        InputStream modelStream = this.getModelJsonStream(tblLocation.toString(), tblResource.getInputStream());
        TabulaModelContainer modelJson = TabulaModelHandler.INSTANCE.loadTabulaModel(modelStream);
        modelStream.close();
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        int layer = 0;
        String texture;
        while ((texture = TabulaModelBlock.textures.get("layer" + layer++)) != null) {
            builder.add(new ResourceLocation(texture));
        }
        String particle = TabulaModelBlock.textures.get("particle");
        return new VanillaTabulaModel(modelJson, particle != null ? new ResourceLocation(particle) : null, builder.build(), PerspectiveMapWrapper.getTransforms(TabulaModelBlock.getAllTransforms()));
    }

   */

    private InputStream getModelJsonStream(String name, InputStream file) throws IOException {
        ZipInputStream zip = new ZipInputStream(file);
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().equals("model.json")) {
                return zip;
            }
        }
        throw new RuntimeException("No model.json present in " + name);
    }

    @Override
    public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
        return this.gson.fromJson(json, type);
    }
}