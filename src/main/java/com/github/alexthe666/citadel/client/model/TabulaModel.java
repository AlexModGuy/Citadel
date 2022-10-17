package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeGroupContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class TabulaModel extends AdvancedEntityModel {
    protected Map<String, AdvancedModelBox> cubes = new HashMap<>();
    protected List<AdvancedModelBox> rootBoxes = new ArrayList<>();
    protected ITabulaModelAnimator tabulaAnimator;
    public ModelAnimator llibAnimator;
    protected Map<String, AdvancedModelBox> identifierMap = new HashMap<>();
    protected double[] scale;

    public TabulaModel(TabulaModelContainer container, ITabulaModelAnimator tabulaAnimator) {
        this.texWidth = container.getTextureWidth();
        this.texHeight = container.getTextureHeight();
        this.tabulaAnimator = tabulaAnimator;
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
        this.updateDefaultPose();
        this.scale = container.getScale();
        this.llibAnimator = ModelAnimator.create();
    }

    public TabulaModel(TabulaModelContainer container) {
        this(container, null);
    }

    private void parseCubeGroup(TabulaCubeGroupContainer container) {
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
    }

    private void parseCube(TabulaCubeContainer cube, AdvancedModelBox parent) {
        AdvancedModelBox box = this.createBox(cube);
        this.cubes.put(cube.getName(), box);
        this.identifierMap.put(cube.getIdentifier(), box);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootBoxes.add(box);
        }
        for (TabulaCubeContainer child : cube.getChildren()) {
            this.parseCube(child, box);
        }
    }

    private AdvancedModelBox createBox(TabulaCubeContainer cube) {
        int[] textureOffset = cube.getTextureOffset();
        double[] position = cube.getPosition();
        double[] rotation = cube.getRotation();
        double[] offset = cube.getOffset();
        int[] dimensions = cube.getDimensions();
        float scaleIn = 0;
        AdvancedModelBox box = new AdvancedModelBox(this, cube.getName());
        box.setTextureOffset(textureOffset[0], textureOffset[1]);
        box.mirror = cube.isTextureMirrorEnabled();
        box.setPos((float) position[0], (float) position[1], (float) position[2]);
        box.addBox((float) offset[0], (float) offset[1], (float) offset[2], dimensions[0], dimensions[1], dimensions[2], scaleIn);
        box.rotateAngleX = (float) Math.toRadians(rotation[0]);
        box.rotateAngleY = (float) Math.toRadians(rotation[1]);
        box.rotateAngleZ = (float) Math.toRadians(rotation[2]);
        return box;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        if (this.tabulaAnimator != null) {
            this.tabulaAnimator.setRotationAngles(this, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, 1.0F);
        }
    }

    public AdvancedModelBox getCube(String name) {
        return this.cubes.get(name);
    }

    public AdvancedModelBox getCubeByIdentifier(String identifier) {
        return this.identifierMap.get(identifier);
    }

    public Map<String, AdvancedModelBox> getCubes() {
        return this.cubes;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.copyOf(rootBoxes);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.copyOf(cubes.values());
    }


}