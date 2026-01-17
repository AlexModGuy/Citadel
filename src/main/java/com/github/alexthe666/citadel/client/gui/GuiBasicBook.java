package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.gui.data.*;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class GuiBasicBook extends Screen {

    private static final ResourceLocation BOOK_PAGE_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/book_pages.png");
    private static final ResourceLocation BOOK_BINDING_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/book_binding.png");
    private static final ResourceLocation BOOK_WIDGET_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/widgets.png");
    private static final ResourceLocation BOOK_BUTTONS_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/link_buttons.png");
    protected final List<LineData> lines = new ArrayList<>();
    protected final List<LinkData> links = new ArrayList<>();
    protected final List<ItemRenderData> itemRenders = new ArrayList<>();
    protected final List<RecipeData> recipes = new ArrayList<>();
    protected final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
    protected final List<EntityRenderData> entityRenders = new ArrayList<>();
    protected final List<EntityLinkData> entityLinks = new ArrayList<>();
    protected final List<ImageData> images = new ArrayList<>();
    protected final List<Whitespace> yIndexesToSkip = new ArrayList<>();
    private final Map<ResourceLocation, TabulaModel> renderedTabulaModels = new HashMap<>();
    private final Map<EntityType<?>, Entity> renderedEntities = new HashMap<>();
    protected ItemStack bookStack;
    protected int xSize = 390;
    protected int ySize = 320;
    protected int currentPageCounter = 0;
    protected int maxPagesFromPrinting = 0;
    protected int linesFromJSON = 0;
    protected int linesFromPrinting = 0;
    protected ResourceLocation prevPageJSON;
    protected ResourceLocation currentPageJSON;
    protected ResourceLocation currentPageText = null;
    protected BookPageButton buttonNextPage;
    protected BookPageButton buttonPreviousPage;
    protected BookPage internalPage = null;
    protected Component writtenTitle = Component.empty();
    protected int preservedPageIndex = 0;
    protected Component entityTooltip;
    private int mouseX;
    private int mouseY;

    public GuiBasicBook(ItemStack bookStack, Component title) {
        super(title);
        this.bookStack = bookStack;
        this.currentPageJSON = getRootPage();
    }

    public static void drawTabulaModelOnScreen(GuiGraphics guiGraphics, TabulaModel model, ResourceLocation tex, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY) {
        float f = (float) Math.atan(mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        PoseStack matrixstack = new PoseStack();
        matrixstack.translate((float) posX, (float) posY, 120.0D);
        matrixstack.scale(scale, scale, scale);
        Quaternionf quaternion = Axis.ZP.rotationDegrees(0.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
        if (follow) {
            quaternion.mul(quaternion1);
        }
        matrixstack.mulPose(quaternion);
        if (follow) {
            matrixstack.mulPose(Axis.YP.rotationDegrees(180.0F + f * 40.0F));
        }
        matrixstack.mulPose(Axis.XP.rotationDegrees((float) -xRot));
        matrixstack.mulPose(Axis.YP.rotationDegrees((float) yRot));
        matrixstack.mulPose(Axis.ZP.rotationDegrees((float) zRot));
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(RenderType.entityCutoutNoCull(tex));
            model.resetToDefaultPose();
            model.renderToBuffer(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, -1);
        });
        Lighting.setupFor3DItems();
    }

    public void drawEntityOnScreen(GuiGraphics guiGraphics, MultiBufferSource bufferSource, int posX, int posY, float zOff, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float customYaw = posX - mouseX;
        float customPitch = posY - mouseY;
        float f = (float) Math.atan(customYaw / 40.0F);
        float f1 = (float) Math.atan(customPitch / 40.0F);

        if (follow) {
            float setX = f1 * 20.0F;
            float setY = f * 20.0F;
            entity.setXRot(setX);
            entity.setYRot(setY);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).yBodyRot = setY;
                ((LivingEntity) entity).yBodyRotO = setY;
                ((LivingEntity) entity).yHeadRot = setY;
                ((LivingEntity) entity).yHeadRotO = setY;
            }
        } else {
            f = 0;
            f1 = 0;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(posX, posY, zOff);
        guiGraphics.pose().mulPose((new Matrix4f()).scaling(scale, scale, -scale));
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        quaternion.mul(Axis.XN.rotationDegrees((float) xRot));
        quaternion.mul(Axis.YP.rotationDegrees((float) yRot));
        quaternion.mul(Axis.ZP.rotationDegrees((float) zRot));
        guiGraphics.pose().mulPose(quaternion);

        Vector3f light0 = new Vector3f(1, -1.0F, -1.0F).normalize();
        Vector3f light1 = new Vector3f(-1, 1.0F, 1.0F).normalize();
        RenderSystem.setShaderLights(light0, light1);
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, guiGraphics.pose(), bufferSource, 240));
        entityrenderdispatcher.setRenderShadow(true);
        entity.setYRot(0);
        entity.setXRot(0);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).yBodyRot = 0;
            ((LivingEntity) entity).yHeadRotO = 0;
            ((LivingEntity) entity).yHeadRot = 0;
        }

        guiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    protected void init() {
        super.init();
        playBookOpeningSound();
        addNextPreviousButtons();
        addLinkButtons();
    }

    private void addNextPreviousButtons() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        this.buttonPreviousPage = this.addRenderableWidget(new BookPageButton(this, k + 10, l + 180, false, (p_214208_1_) -> this.onSwitchPage(false), true));
        this.buttonNextPage = this.addRenderableWidget(new BookPageButton(this, k + 365, l + 180, true, (p_214205_1_) -> this.onSwitchPage(true), true));
    }

    private void addLinkButtons() {
        this.renderables.clear();
        this.clearWidgets();
        addNextPreviousButtons();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (LinkData linkData : links) {
            if (linkData.page() == this.currentPageCounter) {
                int maxLength = Math.max(100, Minecraft.getInstance().font.width(linkData.titleText()) + 20);
                yIndexesToSkip.add(new Whitespace(linkData.page(), linkData.x() - maxLength / 2, linkData.y(), 100, 20));
                this.addRenderableWidget(new LinkButton(this, k + linkData.x() - maxLength / 2, l + linkData.y(), maxLength, 20, linkData.titleText(), linkData.displayItem(), (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = getTextFileDirectory().withSuffix(linkData.linkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.page() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.page();
            }
        }

        for (EntityLinkData linkData : entityLinks) {
            if (linkData.page() == this.currentPageCounter) {
                yIndexesToSkip.add(new Whitespace(linkData.page(), linkData.x() - 12, linkData.y(), 100, 20));
                this.addRenderableWidget(new EntityLinkButton(this, linkData, k, l, (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = getTextFileDirectory().withSuffix(linkData.linkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.page() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.page();
            }
        }
    }

    private void onSwitchPage(boolean next) {
        if (next) {
            if (currentPageCounter < maxPagesFromPrinting) {
                currentPageCounter++;
            }
        } else {
            if (currentPageCounter > 0) {
                currentPageCounter--;
            } else {
                if (this.internalPage != null && this.internalPage.parent().isPresent()) {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = getTextFileDirectory().withSuffix(this.internalPage.parent().orElseThrow());
                    currentPageCounter = preservedPageIndex;
                    preservedPageIndex = 0;
                }
            }
        }
        refreshSpacing();
    }

    /**
     * Override to disable the blur effect that was added in Minecraft 1.21
     * Without this override, the screen content appears blurry
     */
    @Override
    protected void renderBlurredBackground(float partialTick) {
        // Do nothing - this prevents the blur effect from being applied
    }

    /**
     * Override to disable the menu background that was added in Minecraft 1.21
     * Without this override, the book appears darker due to the overlay
     */
    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics) {
        // Do nothing - this prevents the dark menu background from being rendered
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        this.mouseX = x;
        this.mouseY = y;
        int bindingColor = getBindingColor();
        int bindingR = bindingColor >> 16 & 255;
        int bindingG = bindingColor >> 8 & 255;
        int bindingB = bindingColor & 255;
        this.renderBackground(guiGraphics, x, y, partialTicks);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        BookBlit.blitWithColor(guiGraphics, getBookBindingTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize, bindingR, bindingG, bindingB, 255);
        BookBlit.blitWithColor(guiGraphics, getBookPageTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize, 255, 255, 255, 255);
        if (internalPage == null || currentPageJSON != prevPageJSON || prevPageJSON == null) {
            internalPage = generatePage(currentPageJSON);
            if (internalPage != null) {
                refreshSpacing();
            }
        }
        if (internalPage != null) {
            writePageText(guiGraphics, x, y);
        }
        super.render(guiGraphics, x, y, partialTicks);
        prevPageJSON = currentPageJSON;
        if (internalPage != null) {
            guiGraphics.pose().pushPose();
            renderOtherWidgets(guiGraphics, x, y, internalPage);
            guiGraphics.pose().popPose();
        }
        if (this.entityTooltip != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 550);
            guiGraphics.renderTooltip(font, Minecraft.getInstance().font.split(entityTooltip, Math.max(this.width / 2 - 43, 170)), x, y);
            entityTooltip = null;
            guiGraphics.pose().popPose();
        }
    }

    private void refreshSpacing() {
        if (internalPage != null) {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
            currentPageText = ResourceLocation.parse(getTextFileDirectory() + lang + "/" + internalPage.textFileToReadFrom());
            boolean invalid = false;
            try {
                //test if it exists. if no exception, then the language is supported
                InputStream is = Minecraft.getInstance().getResourceManager().open(currentPageText);
                is.close();
            } catch (Exception e) {
                invalid = true;
                Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
                currentPageText = ResourceLocation.parse(getTextFileDirectory() + "en_us/" + internalPage.textFileToReadFrom());
            }

            readInPageWidgets(internalPage);
            addWidgetSpacing();
            addLinkButtons();
            readInPageText(currentPageText);
        }
    }

    private Recipe getRecipeByName(ResourceLocation registryName) {
        try {
            RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
            if (manager.byKey(registryName).isPresent()) {
                return manager.byKey(registryName).get().value();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addWidgetSpacing() {
        yIndexesToSkip.clear();
        for (ItemRenderData itemRenderData : itemRenders) {
            yIndexesToSkip.add(new Whitespace(itemRenderData.page(), itemRenderData.x(), itemRenderData.y(), (int) (itemRenderData.scale() * 17), (int) (itemRenderData.scale() * 15)));

        }
        for (RecipeData recipeData : recipes) {
            Recipe recipe = getRecipeByName(recipeData.recipe());
            if (recipe != null) {
                yIndexesToSkip.add(new Whitespace(recipeData.page(), recipeData.x(), recipeData.y() - (int) (recipeData.scale() * 15), (int) (recipeData.scale() * 35), (int) (recipeData.scale() * 60), true));
            }
        }
        for (ImageData imageData : images) {
            if (imageData != null) {
                yIndexesToSkip.add(new Whitespace(imageData.page(), imageData.x(), imageData.y(), (int) (imageData.scale() * imageData.width()), (int) (imageData.scale() * imageData.height() * 0.8F)));
            }
        }
        if (!writtenTitle.getString().isEmpty()) {
            yIndexesToSkip.add(new Whitespace(0, 20, 5, 70, 15));
        }
    }

    private void renderOtherWidgets(GuiGraphics guiGraphics, int x, int y, BookPage page) {
        int color = getBindingColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (ImageData imageData : images) {
            if (imageData.page() == this.currentPageCounter) {
                ResourceLocation tex = imageData.texture();
                // yIndexesToSkip.put(imageData.getPage(), new Whitespace(imageData.getX(), imageData.getY(),(int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
                float scale = (float) imageData.scale();
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(k + imageData.x(), l + imageData.y(), 0);
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.blit(tex, 0, 0, imageData.u(), imageData.v(), imageData.width(), imageData.height());
                guiGraphics.pose().popPose();
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.page() == this.currentPageCounter) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(k + recipeData.x(), l + recipeData.y(), 0);
                float scale = (float) recipeData.scale();
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.blit(getBookWidgetTexture(), 0, 0, 0, 88, 116, 53);
                guiGraphics.pose().popPose();
            }
        }

        for (TabulaRenderData tabulaRenderData : tabulaRenders) {
            if (tabulaRenderData.page() == this.currentPageCounter) {
                TabulaModel model = null;
                ResourceLocation texture = tabulaRenderData.texture();
                if (renderedTabulaModels.get(tabulaRenderData.model()) != null) {
                    model = renderedTabulaModels.get(tabulaRenderData.model());
                } else {
                    try {
                        model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/" + tabulaRenderData.model().getNamespace() + "/" + tabulaRenderData.model().getPath()));
                    } catch (Exception e) {
                        Citadel.LOGGER.warn("Could not load in tabula model for book at {}", tabulaRenderData.model());
                    }
                    renderedTabulaModels.put(tabulaRenderData.model(), model);
                }

                if (model != null && texture != null) {
                    float scale = (float) tabulaRenderData.scale();
                    drawTabulaModelOnScreen(guiGraphics, model, texture, k + tabulaRenderData.x(), l + tabulaRenderData.y(), 30 * scale, tabulaRenderData.followCursor(), tabulaRenderData.rotX(), tabulaRenderData.rotY(), tabulaRenderData.rotZ(), mouseX, mouseY);
                }
            }
        }
        for (EntityRenderData data : entityRenders) {
            if (data.page() == this.currentPageCounter) {
                Entity model;
                try {
                    model = renderedEntities.computeIfAbsent(data.entity(), entityType -> {
                        Entity entity = entityType.create(Minecraft.getInstance().level);
                        if (data.entityData().isPresent()) {
                            try {
                                CompoundTag tag = NbtUtils.snbtToStructure(data.entityData().orElseThrow());
                                entity.load(tag);
                            } catch (CommandSyntaxException e) {
                                e.printStackTrace();
                            }
                        }

                        return entity;
                    });
                } catch (Exception e) {
                    Citadel.LOGGER.warn("Failed to create entity '{}' for book rendering, skipping.", data.entity(), e);
                    continue;
                }
                if (model != null) {
                    float scale = (float) data.scale();
                    model.tickCount = Minecraft.getInstance().player.tickCount;
                    drawEntityOnScreen(guiGraphics, guiGraphics.bufferSource(), k + data.x(), l + data.y(), 1050F, 30 * scale, data.followCursor(), data.rotX(), data.rotY(), data.rotZ(), mouseX, mouseY, model);
                }
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.page() == this.currentPageCounter) {
                Recipe recipe = getRecipeByName(recipeData.recipe());
                if (recipe != null) {
                    renderRecipe(guiGraphics, recipe, recipeData, k, l);
                }
            }
        }
        for (ItemRenderData itemRenderData : itemRenders) {
            if (itemRenderData.page() == this.currentPageCounter) {
                Holder<Item> item = itemRenderData.item();
                float scale = (float) itemRenderData.scale();
                ItemStack stack = new ItemStack(item, 1, itemRenderData.components());

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(k, l, 0);
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.renderItem(stack, itemRenderData.x(), itemRenderData.y());
                guiGraphics.pose().popPose();
            }
        }
    }

    protected void renderRecipe(GuiGraphics guiGraphics, Recipe recipe, RecipeData recipeData, int k, int l) {
        int playerTicks = Minecraft.getInstance().player.tickCount;
        float scale = (float) recipeData.scale();
        NonNullList<Ingredient> ingredients = recipe instanceof SpecialRecipeInGuideBook ? ((SpecialRecipeInGuideBook) recipe).getDisplayIngredients() : recipe.getIngredients();
        NonNullList<ItemStack> displayedStacks = NonNullList.create();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            ItemStack stack = ItemStack.EMPTY;
            if (!ing.isEmpty()) {
                if (ing.getItems().length > 1) {
                    int currentIndex = (int) ((playerTicks / 20F) % ing.getItems().length);
                    stack = ing.getItems()[currentIndex];
                } else {
                    stack = ing.getItems()[0];
                }
            }
            if (!stack.isEmpty()) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(k, l, 32.0F);
                guiGraphics.pose().translate((int) (recipeData.x() + (i % 3) * 20 * scale), (int) (recipeData.y() + (i / 3) * 20 * scale), 0);
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.renderItem(stack, 0, 0);
                guiGraphics.pose().popPose();
            }
            displayedStacks.add(i, stack);
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(k, l, 32.0F);
        float finScale = scale * 1.5F;
        guiGraphics.pose().translate(recipeData.x() + 70 * finScale, recipeData.y() + 10 * finScale, 0);
        guiGraphics.pose().scale(finScale, finScale, finScale);
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        if (recipe instanceof SpecialRecipeInGuideBook) {
            result = ((SpecialRecipeInGuideBook) recipe).getDisplayResultFor(displayedStacks);
        }
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        guiGraphics.renderItem(result, 0, 0);
        guiGraphics.pose().popPose();
    }

    protected void writePageText(GuiGraphics guiGraphics, int x, int y) {
        Font font = this.font;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        for (LineData line : this.lines) {
            if (line.page() == this.currentPageCounter) {
                guiGraphics.drawString(font, line.text(), k + 10 + line.xIndex(), l + 10 + line.yIndex() * 12, getTextColor(), false);
            }
        }
        if (this.currentPageCounter == 0 && !writtenTitle.getString().isEmpty()) {
            guiGraphics.pose().pushPose();
            float scale = 2F;
            if (font.width(writtenTitle) > 80) {
                scale = 2.0F - Mth.clamp((font.width(writtenTitle) - 80) * 0.011F, 0, 1.95F);
            }
            guiGraphics.pose().translate(k + 10, l + 10, 0);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.drawString(font, writtenTitle, 0, 0, getTitleColor(), false);
            guiGraphics.pose().popPose();
        }
        this.buttonNextPage.visible = currentPageCounter < maxPagesFromPrinting;
        this.buttonPreviousPage.visible = currentPageCounter > 0 || !currentPageJSON.equals(this.getRootPage());
    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void playBookOpeningSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    protected void playBookClosingSound() {
    }

    protected abstract int getBindingColor();

    protected int getWidgetColor() {
        return getBindingColor();
    }

    protected int getTextColor() {
        return 0X303030;
    }

    protected int getTitleColor() {
        return 0XBAAC98;
    }

    public abstract ResourceLocation getRootPage();

    public abstract ResourceLocation getTextFileDirectory();

    protected ResourceLocation getBookPageTexture() {
        return BOOK_PAGE_TEXTURE;
    }

    protected ResourceLocation getBookBindingTexture() {
        return BOOK_BINDING_TEXTURE;
    }

    protected ResourceLocation getBookWidgetTexture() {
        return BOOK_WIDGET_TEXTURE;
    }

    protected void playPageFlipSound() {
    }

    @Nullable
    protected BookPage generatePage(ResourceLocation res) {
        Optional<Resource> resource;
        BookPage page = null;
        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resource.isPresent()) {
                BufferedReader inputstream = resource.get().openAsReader();
                page = BookPage.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(inputstream)).getOrThrow().getFirst();
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
        return page;
    }

    protected void readInPageWidgets(BookPage page) {
        links.clear();
        itemRenders.clear();
        recipes.clear();
        tabulaRenders.clear();
        entityRenders.clear();
        images.clear();
        entityLinks.clear();
        links.addAll(page.linkedButtons());
        entityLinks.addAll(page.linkedEntities());
        itemRenders.addAll(page.itemRenders());
        recipes.addAll(page.recipes());
        tabulaRenders.addAll(page.tabulaRenders());
        entityRenders.addAll(page.entityRenders());
        images.addAll(page.images());
        writtenTitle = page.title();
    }

    protected void readInPageText(ResourceLocation res) {
        Resource resource = null;
        int xIndex = 0;
        int actualTextX = 0;
        int yIndex = 0;
        try {
            BufferedReader bufferedreader = Minecraft.getInstance().getResourceManager().openAsReader(res);
            try {
                List<String> readStrings = IOUtils.readLines(bufferedreader);
                this.linesFromJSON = readStrings.size();
                this.lines.clear();
                List<String> splitBySpaces = new ArrayList<>();
                for (String line : readStrings) {
                    splitBySpaces.addAll(Arrays.asList(line.split(" ")));
                }
                String lineToPrint = "";
                linesFromPrinting = 0;
                int page = 0;
                for (int i = 0; i < splitBySpaces.size(); i++) {
                    String word = splitBySpaces.get(i);
                    int cutoffPoint = xIndex > 100 ? 30 : 35;
                    boolean newline = word.equals("<NEWLINE>");
                    for (Whitespace indexes : yIndexesToSkip) {
                        int indexPage = indexes.page();
                        if (indexPage == page) {
                            int buttonX = indexes.x();
                            int buttonY = indexes.y();
                            int width = indexes.width();
                            int height = indexes.height();
                            if (indexes.down()) {
                                if (yIndex >= (buttonY) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex += 2;
                                    }
                                }
                            } else {
                                if (yIndex >= (buttonY - height) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex++;
                                    }
                                }
                            }
                        }
                    }
                    boolean last = i == splitBySpaces.size() - 1;
                    actualTextX += word.length() + 1;
                    if (lineToPrint.length() + word.length() + 1 >= cutoffPoint || newline) {
                        linesFromPrinting++;
                        if (yIndex > 13) {
                            if (xIndex > 0) {
                                page++;
                                xIndex = 0;
                                yIndex = 0;
                            } else {
                                xIndex = 200;
                                yIndex = 0;
                            }
                        }
                        if (last) {
                            lineToPrint = lineToPrint + " " + word;
                        }
                        this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                        yIndex++;
                        actualTextX = 0;
                        if (newline) {
                            yIndex++;
                        }
                        lineToPrint = word.equals("<NEWLINE>") ? "" : word;
                    } else {
                        lineToPrint = lineToPrint + " " + word;
                        if (last) {
                            linesFromPrinting++;
                            this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                            yIndex++;
                            actualTextX = 0;
                        }
                    }
                }
                maxPagesFromPrinting = page;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: {}", res);
        }
    }

    public void setEntityTooltip(Component hoverText) {
        this.entityTooltip = hoverText;
    }

    public ResourceLocation getBookButtonsTexture() {
        return BOOK_BUTTONS_TEXTURE;
    }
}
