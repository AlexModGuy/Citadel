package com.github.alexthe666.citadel.client.game;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

public class Tetris {

    protected final RandomSource random = RandomSource.create();

    private boolean started = false;
    private int score;
    private int renderTime = 0;
    private int keyCooldown;
    private static int HEIGHT = 20;
    private TetrominoShape fallingShape;
    private BlockState fallingBlock;
    private float fallingX;
    private float prevFallingY;
    private float fallingY;
    private Rotation fallingRotation;
    private BlockState[][] settledBlocks = new BlockState[10][HEIGHT];
    private boolean gameOver = false;

    private TetrominoShape nextShape;
    private BlockState nextBlock;

    private boolean[] flashingLayer = new boolean[HEIGHT];
    private int flashFor = 0;

    private final Block[] allRegisteredBlocks = ForgeRegistries.BLOCKS.getValues().stream().toArray(Block[]::new);

    public Tetris() {
        reset();
    }

    public void tick() {
        renderTime++;
        prevFallingY = fallingY;
        if (keyCooldown > 0) {
            keyCooldown--;
        }
        if (started && !gameOver) {
            if (fallingShape == null) {
                generateTetromino();
                generateNextTetromino();
            } else if (groundedTetromino()) {
                groundTetromino();
                fallingShape = null;
            } else {
                float f = 0.15F;
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_DOWN)) {
                    f = 1F;
                }
                fallingY += f;
                if (keyPressed(InputConstants.KEY_LEFT) && !isBlocksInOffset(-1, 0)) {
                    fallingX = restrictTetrominoX((int) (Math.floor(fallingX) - 1));
                }
                if (keyPressed(InputConstants.KEY_RIGHT) && !isBlocksInOffset(1, 0)) {
                    fallingX = restrictTetrominoX((int) (Math.ceil(fallingX) + 1));
                }
                if (keyPressed(InputConstants.KEY_UP) && fallingRotation != null && fallingShape != TetrominoShape.SQUARE) {
                    fallingRotation = fallingRotation.getRotated(Rotation.CLOCKWISE_90);
                    fallingX = restrictTetrominoX((int) (Math.floor(fallingX)));
                }
            }
        }
        if (flashFor > 0) {
            flashFor--;
            if (flashFor == 0) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (flashingLayer[j]) {
                        for (int k = j; k < HEIGHT; k++) {
                            for (int i = 0; i < 10; i++) {
                                settledBlocks[i][k] = k < HEIGHT - 1 ? settledBlocks[i][k + 1] : null;
                            }
                        }
                    }
                }
                int cleared = 0;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0F));
                for (int i = 0; i < flashingLayer.length; i++) {
                    if (flashingLayer[i]) {
                        cleared++;
                    }
                    flashingLayer[i] = false;
                }
                if (cleared == 1) {
                    score += 40;
                } else if (cleared == 2) {
                    score += 100;
                } else if (cleared == 3) {
                    score += 300;
                } else if (cleared >= 4) {
                    score += 1200 * (cleared - 3);
                }
            }
        }
        if (keyPressed(InputConstants.KEY_T)) {
            started = true;
            reset();
        }
    }

    private boolean groundedTetromino() {
        for (Vec3i vec : fallingShape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, fallingRotation, Vec3i.ZERO);
            int x = Math.round(fallingX) + vec2.getX();
            int y = HEIGHT - (int) Math.ceil(fallingY) - vec2.getY();
            if (y < 0) {
                return true;
            }
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y <= 0 || settledBlocks[x][y - 1] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void groundTetromino() {
        for (Vec3i vec : fallingShape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, fallingRotation, Vec3i.ZERO);
            int x = Math.round(fallingX) + vec2.getX();
            int y = HEIGHT - (int) Math.ceil(fallingY) - vec2.getY();
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y >= HEIGHT - 1) {
                    gameOver = true;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_DEATH, 1.0F));
                }
                if (settledBlocks[x][y] == null) {
                    settledBlocks[x][y] = fallingBlock;
                }
            }
        }
        boolean flag = false;
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < 10; i++) {
                if (settledBlocks[i][j] == null) {
                    break;
                }
                if (i == 9) {
                    flashingLayer[j] = true;
                    flag = true;
                }
            }
        }
        if (flag) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
            flashFor = 20;
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(fallingBlock.getSoundType().getPlaceSound(), 1.0F));
    }

    private boolean isBlocksInOffset(int xOffset, int yOffset) {
        for (Vec3i vec : fallingShape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, fallingRotation, Vec3i.ZERO);
            int x = Math.round(fallingX) + vec2.getX() + xOffset;
            int y = HEIGHT - (int) Math.ceil(fallingY) - vec2.getY() + yOffset;
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y <= 0 || settledBlocks[x][y] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isStarted() {
        return started;
    }

    private boolean keyPressed(int keyId) {
        if (keyCooldown == 0 && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyId)) {
            keyCooldown = 4;
            return true;
        }
        return false;
    }

    private void generateNextTetromino() {
        BlockState randomState = Blocks.DIRT.defaultBlockState();
        for (int tries = 0; tries < 5; tries++) {
            if (allRegisteredBlocks.length > 1) {
                BlockState block = allRegisteredBlocks[random.nextInt(allRegisteredBlocks.length - 1)].defaultBlockState();
                BakedModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(block);
                if (!block.is(Blocks.GLOWSTONE) && !blockModel.isCustomRenderer() && blockModel.getRenderTypes(block, random, ModelData.EMPTY).contains(RenderType.solid())) {
                    randomState = block;
                    break;
                }
            }
        }
        nextShape = TetrominoShape.getRandom(random);
        nextBlock = randomState;
    }

    private void generateTetromino() {
        fallingShape = nextShape;
        fallingBlock = nextBlock;
        fallingRotation = Rotation.getRandom(random);
        fallingX = restrictTetrominoX(random.nextInt(10));
        prevFallingY = 0;
        fallingY = -2;
    }

    private int restrictTetrominoX(int xIn) {
        int minShapeX = 0;
        int maxShapeX = 0;
        for (Vec3i vec : fallingShape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, fallingRotation, Vec3i.ZERO);
            if (vec2.getX() < minShapeX) {
                minShapeX = vec2.getX();
            }
            if (vec2.getX() > maxShapeX) {
                maxShapeX = vec2.getX();
            }
        }
        if (xIn + minShapeX < 0) {
            xIn = Math.max(xIn - minShapeX, minShapeX);
        }
        if (xIn + maxShapeX > 9) {
            xIn = Math.min(xIn - maxShapeX, 9 - maxShapeX);
        }
        return xIn;
    }

    private void renderTetromino(TetrominoShape shape, BlockState blockState, Rotation fallingRotation, float x, float y, float scale, float offsetX, float offsetY) {
        for (Vec3i vec : shape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, fallingRotation, Vec3i.ZERO);
            renderBlockState(blockState, offsetX + (x + vec2.getX()) * scale, offsetY + (y + vec2.getY()) * scale, scale);
        }
    }

    private void renderBlockState(BlockState state, float offsetX, float offsetY, float size) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(state).getParticleIcon(ModelData.EMPTY);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float f = size * 0.5F;
        bufferbuilder.vertex(-f + offsetX, f + offsetY, 80.0D).uv(sprite.getU0(), sprite.getV1()).endVertex();
        bufferbuilder.vertex(f + offsetX, f + offsetY, 80.0D).uv(sprite.getU1(), sprite.getV1()).endVertex();
        bufferbuilder.vertex(f + offsetX, -f + offsetY, 80.0D).uv(sprite.getU1(), sprite.getV0()).endVertex();
        bufferbuilder.vertex(-f + offsetX, -f + offsetY, 80.0D).uv(sprite.getU0(), sprite.getV0()).endVertex();
        tesselator.end();

    }

    public void render(TitleScreen screen, PoseStack poseStack, float partialTick) {
        float scale = Math.min(screen.width / 15F, screen.height / (float) HEIGHT);
        float offsetX = screen.width / 2F - scale * 5F;
        float offsetY = scale * 0.5F;
        if (started) {
            GuiComponent.fill(poseStack, (int) (screen.width * 0.05F), (int) (screen.height * 0.3F), (int) (screen.width * 0.05F) + 70, (int) (screen.height * 0.5F), -1873784752);
            GuiComponent.fill(poseStack, (int) (screen.width * 0.7F), (int) (screen.height * 0.3F), (int) (screen.width * 0.7F) + 130, (int) (screen.height * 0.84F), -1873784752);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            for (int i = 0; i < settledBlocks.length; i++) {
                int max = settledBlocks[i].length;
                for (int j = 0; j < max; j++) {
                    BlockState state = settledBlocks[i][j];
                    if (flashingLayer[j] && renderTime % 4 < 2) {
                        state = Blocks.GLOWSTONE.defaultBlockState();
                    }
                    if (state != null) {
                        renderBlockState(state, offsetX + i * scale, offsetY + (max - j - 1) * scale, scale);
                    }
                }
            }
            if (fallingShape != null) {
                float lerpedFallingY = prevFallingY + (fallingY - prevFallingY) * partialTick;
                renderTetromino(fallingShape, fallingBlock, fallingRotation, fallingX, lerpedFallingY, scale, offsetX, offsetY);
            }
            if (nextShape != null) {
                renderTetromino(nextShape, nextBlock, Rotation.NONE, 0, 0, scale, screen.width * 0.85F, screen.height * 0.4F);
            }
            float hue = (System.currentTimeMillis() % 6000) / 6000f;
            int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
            poseStack.pushPose();
            poseStack.scale(2, 2, 2);
            GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, "SCORE", (int) (screen.width * 0.065F), (int) (screen.height * 0.175F), rainbow);
            GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, "" + score, (int) (screen.width * 0.065F), (int) (screen.height * 0.175F) + 10, rainbow);
            poseStack.popPose();
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "[LEFT ARROW] move left", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F), rainbow);
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "[RIGHT ARROW] move right", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 10, rainbow);
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "[UP ARROW] rotate", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 20, rainbow);
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "[DOWN ARROW] quick drop", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 30, rainbow);
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "[T] start over", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 50, rainbow);
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Happy april fools from Citadel", 5, 5, rainbow);
            if(gameOver){
                poseStack.pushPose();
                poseStack.translate((int) (screen.width * 0.5F), (int) (screen.height * 0.5F), 150);
                poseStack.scale(3 + (float) Math.sin(hue * Math.PI) * 0.4F, 3 + (float) Math.sin(hue * Math.PI) * 0.4F, 3 + (float) Math.sin(hue * Math.PI) * 0.4F);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) Math.sin(hue * Math.PI) * 10));
                GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, "GAME OVER", 0, 0, rainbow);
                poseStack.popPose();
            }
        }
    }

    public void reset() {
        score = 0;
        for (int i = 0; i < settledBlocks.length; i++) {
            for (int j = 0; j < settledBlocks[i].length; j++) {
                settledBlocks[i][j] = null;
            }
        }
        gameOver = false;
        for (int i = 0; i < flashingLayer.length; i++) {
            flashingLayer[i] = false;
        }
        generateNextTetromino();
        generateTetromino();
        generateNextTetromino();
    }

    private static Vec3i transform(Vec3i vec3i, Rotation rotation, Vec3i relativeTo) {
        int i = vec3i.getX();
        int k = vec3i.getY();
        int j = vec3i.getZ();
        boolean flag = true;

        int l = relativeTo.getX();
        int i1 = relativeTo.getY();
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
                return new Vec3i(l - i1 + k, l + i1 - i, j);
            case CLOCKWISE_90:
                return new Vec3i(l + i1 - k, i1 - l + i, j);
            case CLOCKWISE_180:
                return new Vec3i(l + l - i, i1 + i1 - k, j);
            default:
                return flag ? new Vec3i(i, k, j) : vec3i;
        }
    }

}
