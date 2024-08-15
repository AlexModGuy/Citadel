package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.gui.data.*;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.registries.ForgeRegistries;
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

    private static final ResourceLocation BOOK_PAGE_TEXTURE = new ResourceLocation("citadel:textures/gui/book/book_pages.png");
    private static final ResourceLocation BOOK_BINDING_TEXTURE = new ResourceLocation("citadel:textures/gui/book/book_binding.png");
    private static final ResourceLocation BOOK_WIDGET_TEXTURE = new ResourceLocation("citadel:textures/gui/book/widgets.png");
    private static final ResourceLocation BOOK_BUTTONS_TEXTURE = new ResourceLocation("citadel:textures/gui/book/link_buttons.png");
    protected final List<LineData> lines = new ArrayList<>();
    protected final List<LinkData> links = new ArrayList<>();
    protected final List<ItemRenderData> itemRenders = new ArrayList<>();
    protected final List<RecipeData> recipes = new ArrayList<>();
    protected final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
    protected final List<EntityRenderData> entityRenders = new ArrayList<>();
    protected final List<EntityLinkData> entityLinks = new ArrayList<>();
    protected final List<ImageData> images = new ArrayList<>();
    protected final List<Whitespace> yIndexesToSkip = new ArrayList<>();
    private final Map<String, TabulaModel> renderedTabulaModels = new HashMap<>();
    private final Map<String, Entity> renderedEntites = new HashMap<>();
    private final Map<String, ResourceLocation> textureMap = new HashMap<>();
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
    protected String writtenTitle = "";
    protected int preservedPageIndex = 0;
    protected String entityTooltip;
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
            model.renderToBuffer(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
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
        guiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float)scale, (float)scale, (float)(-scale)));
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
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, guiGraphics.pose(), bufferSource, 240);
        });
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
        this.buttonPreviousPage = this.addRenderableWidget(new BookPageButton(this, k + 10, l + 180, false, (p_214208_1_) -> {
            this.onSwitchPage(false);
        }, true));
        this.buttonNextPage = this.addRenderableWidget(new BookPageButton(this, k + 365, l + 180, true, (p_214205_1_) -> {
            this.onSwitchPage(true);
        }, true));
    }

    private void addLinkButtons() {
        this.renderables.clear();
        this.clearWidgets();
        addNextPreviousButtons();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (LinkData linkData : links) {
            if (linkData.getPage() == this.currentPageCounter) {
                int maxLength = Math.max(100, Minecraft.getInstance().font.width(linkData.getTitleText()) + 20);
                yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - maxLength / 2, linkData.getY(), 100, 20));
                this.addRenderableWidget(new LinkButton(this, k + linkData.getX() - maxLength / 2, l + linkData.getY(), maxLength, 20, Component.translatable(linkData.getTitleText()), linkData.getDisplayItem(), (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = new ResourceLocation(getTextFileDirectory() + linkData.getLinkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
            }
        }

        for (EntityLinkData linkData : entityLinks) {
            if (linkData.getPage() == this.currentPageCounter) {
                yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - 12, linkData.getY(), 100, 20));
                this.addRenderableWidget(new EntityLinkButton(this, linkData, k, l, (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = new ResourceLocation(getTextFileDirectory() + linkData.getLinkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
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
                if (this.internalPage != null && !this.internalPage.getParent().isEmpty()) {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = new ResourceLocation(getTextFileDirectory() + this.internalPage.getParent());
                    currentPageCounter = preservedPageIndex;
                    preservedPageIndex = 0;
                }
            }
        }
        refreshSpacing();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        this.mouseX = x;
        this.mouseY = y;
        int bindingColor = getBindingColor();
        int bindingR = bindingColor >> 16 & 255;
        int bindingG = bindingColor >> 8 & 255;
        int bindingB = bindingColor & 255;
        this.renderBackground(guiGraphics);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        BookBlit.blitWithColor(guiGraphics, getBookBindingTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize, bindingR, bindingG, bindingB, 255);
        BookBlit.blitWithColor(guiGraphics, getBookPageTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize,  255, 255, 255, 255);
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
            guiGraphics.renderTooltip(font, Minecraft.getInstance().font.split(Component.translatable(entityTooltip), Math.max(this.width / 2 - 43, 170)), x, y);
            entityTooltip = null;
            guiGraphics.pose().popPose();
        }
    }

    private void refreshSpacing() {
        if (internalPage != null) {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
            currentPageText = new ResourceLocation(getTextFileDirectory() + lang + "/" + internalPage.getTextFileToReadFrom());
            boolean invalid = false;
            try {
                //test if it exists. if no exception, then the language is supported
                InputStream is = Minecraft.getInstance().getResourceManager().open(currentPageText);
                is.close();
            } catch (Exception e) {
                invalid = true;
                Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
                currentPageText = new ResourceLocation(getTextFileDirectory() + "en_us/" + internalPage.getTextFileToReadFrom());
            }

            readInPageWidgets(internalPage);
            addWidgetSpacing();
            addLinkButtons();
            readInPageText(currentPageText);
        }
    }

    private Item getItemByRegistryName(String registryName) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
    }

    private Recipe getRecipeByName(String registryName) {
        try {
            RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
            if (manager.byKey(new ResourceLocation(registryName)).isPresent()) {
                return manager.byKey(new ResourceLocation(registryName)).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addWidgetSpacing() {
        yIndexesToSkip.clear();
        for (ItemRenderData itemRenderData : itemRenders) {
            Item item = getItemByRegistryName(itemRenderData.getItem());
            if (item != null) {
                yIndexesToSkip.add(new Whitespace(itemRenderData.getPage(), itemRenderData.getX(), itemRenderData.getY(), (int) (itemRenderData.getScale() * 17), (int) (itemRenderData.getScale() * 15)));

            }
        }
        for (RecipeData recipeData : recipes) {
            Recipe recipe = getRecipeByName(recipeData.getRecipe());
            if (recipe != null) {
                yIndexesToSkip.add(new Whitespace(recipeData.getPage(), recipeData.getX(), recipeData.getY() - (int) (recipeData.getScale() * 15), (int) (recipeData.getScale() * 35), (int) (recipeData.getScale() * 60), true));
            }
        }
        for (ImageData imageData : images) {
            if (imageData != null) {
                yIndexesToSkip.add(new Whitespace(imageData.getPage(), imageData.getX(), imageData.getY(), (int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
            }
        }
        if (!writtenTitle.isEmpty()) {
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
            if (imageData.getPage() == this.currentPageCounter) {
                if (imageData != null) {
                    ResourceLocation tex = textureMap.get(imageData.getTexture());
                    if (tex == null) {
                        tex = new ResourceLocation(imageData.getTexture());
                        textureMap.put(imageData.getTexture(), tex);
                    }
                    // yIndexesToSkip.put(imageData.getPage(), new Whitespace(imageData.getX(), imageData.getY(),(int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
                    float scale = (float) imageData.getScale();
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(k + imageData.getX(), l + imageData.getY(), 0);
                    guiGraphics.pose().scale(scale, scale, scale);
                    guiGraphics.blit(tex, 0, 0, imageData.getU(), imageData.getV(), imageData.getWidth(), imageData.getHeight());
                    guiGraphics.pose().popPose();
                }
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(k + recipeData.getX(), l + recipeData.getY(), 0);
                float scale = (float) recipeData.getScale();
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.blit(getBookWidgetTexture(), 0, 0, 0, 88, 116, 53);
                guiGraphics.pose().popPose();
            }
        }

        for (TabulaRenderData tabulaRenderData : tabulaRenders) {
            if (tabulaRenderData.getPage() == this.currentPageCounter) {
                TabulaModel model = null;
                ResourceLocation texture;
                if (textureMap.get(tabulaRenderData.getTexture()) != null) {
                    texture = textureMap.get(tabulaRenderData.getTexture());
                } else {
                    texture = textureMap.put(tabulaRenderData.getTexture(), new ResourceLocation(tabulaRenderData.getTexture()));
                }
                if (renderedTabulaModels.get(tabulaRenderData.getModel()) != null) {
                    model = renderedTabulaModels.get(tabulaRenderData.getModel());
                } else {
                    try {
                        model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/" + tabulaRenderData.getModel().split(":")[0] + "/" + tabulaRenderData.getModel().split(":")[1]));
                    } catch (Exception e) {
                        Citadel.LOGGER.warn("Could not load in tabula model for book at " + tabulaRenderData.getModel());
                    }
                    renderedTabulaModels.put(tabulaRenderData.getModel(), model);
                }

                if (model != null && texture != null) {
                    float scale = (float) tabulaRenderData.getScale();
                    drawTabulaModelOnScreen(guiGraphics, model, texture, k + tabulaRenderData.getX(), l + tabulaRenderData.getY(), 30 * scale, tabulaRenderData.isFollow_cursor(), tabulaRenderData.getRot_x(), tabulaRenderData.getRot_y(), tabulaRenderData.getRot_z(), mouseX, mouseY);
                }
            }
        }
        for (EntityRenderData data : entityRenders) {
            if (data.getPage() == this.currentPageCounter) {
                Entity model = null;
                EntityType type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(data.getEntity()));
                if (type != null) {
                    model = renderedEntites.putIfAbsent(data.getEntity(), type.create(Minecraft.getInstance().level));
                }
                if (model != null) {
                    float scale = (float) data.getScale();
                    model.tickCount = Minecraft.getInstance().player.tickCount;
                    if (data.getEntityData() != null) {
                        try {
                            CompoundTag tag = TagParser.parseTag(data.getEntityData());
                            model.load(tag);
                        } catch (CommandSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    drawEntityOnScreen(guiGraphics, guiGraphics.bufferSource(), k + data.getX(), l + data.getY(), 1050F, 30 * scale, data.isFollow_cursor(), data.getRot_x(), data.getRot_y(), data.getRot_z(), mouseX, mouseY, model);
                }
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                Recipe recipe = getRecipeByName(recipeData.getRecipe());
                if (recipe != null) {
                    renderRecipe(guiGraphics, recipe, recipeData, k, l);
                }
            }
        }
        for (ItemRenderData itemRenderData : itemRenders) {
            if (itemRenderData.getPage() == this.currentPageCounter) {
                Item item = getItemByRegistryName(itemRenderData.getItem());
                if (item != null) {
                    float scale = (float) itemRenderData.getScale();
                    ItemStack stack = new ItemStack(item);
                    if (itemRenderData.getItemTag() != null && !itemRenderData.getItemTag().isEmpty()) {
                        CompoundTag tag = null;
                        try {
                            tag = TagParser.parseTag(itemRenderData.getItemTag());
                        } catch (CommandSyntaxException e) {
                            e.printStackTrace();
                        }
                        stack.setTag(tag);
                    }
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(k, l, 0);
                    guiGraphics.pose().scale(scale, scale, scale);
                    guiGraphics.renderItem(stack, itemRenderData.getX(), itemRenderData.getY());
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    protected void renderRecipe(GuiGraphics guiGraphics, Recipe recipe, RecipeData recipeData, int k, int l) {
        int playerTicks = Minecraft.getInstance().player.tickCount;
        float scale = (float) recipeData.getScale();
        NonNullList<Ingredient> ingredients = recipe instanceof SpecialRecipeInGuideBook ? ((SpecialRecipeInGuideBook)recipe).getDisplayIngredients() : recipe.getIngredients();
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
                guiGraphics.pose().translate((int) (recipeData.getX() + (i % 3) * 20 * scale), (int) (recipeData.getY() + (i / 3) * 20 * scale), 0);
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.renderItem(stack, 0, 0);
                guiGraphics.pose().popPose();
            }
            displayedStacks.add(i, stack);
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(k, l, 32.0F);
        float finScale = scale * 1.5F;
        guiGraphics.pose().translate(recipeData.getX() + 70 * finScale, recipeData.getY() + 10 * finScale, 0);
        guiGraphics.pose().scale(finScale, finScale, finScale);
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        if(recipe instanceof SpecialRecipeInGuideBook){
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
            if (line.getPage() == this.currentPageCounter) {
                guiGraphics.drawString(font, line.getText(), k + 10 + line.getxIndex(), l + 10 + line.getyIndex() * 12, getTextColor(), false);
            }
        }
        if (this.currentPageCounter == 0 && !writtenTitle.isEmpty()) {
            String actualTitle = I18n.get(writtenTitle);
            guiGraphics.pose().pushPose();
            float scale = 2F;
            if (font.width(actualTitle) > 80) {
                scale = 2.0F - Mth.clamp((font.width(actualTitle) - 80) * 0.011F, 0, 1.95F);
            }
            guiGraphics.pose().translate(k + 10, l + 10, 0);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.drawString(font, actualTitle, 0, 0, getTitleColor(), false);
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

    public abstract String getTextFileDirectory();

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
        Optional<Resource> resource = null;
        BookPage page = null;
        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            try {
                resource = Minecraft.getInstance().getResourceManager().getResource(res);
                if (resource.isPresent()) {
                    BufferedReader inputstream = resource.get().openAsReader();
                    page = BookPage.deserialize(inputstream);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
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
        links.addAll(page.getLinkedButtons());
        entityLinks.addAll(page.getLinkedEntities());
        itemRenders.addAll(page.getItemRenders());
        recipes.addAll(page.getRecipes());
        tabulaRenders.addAll(page.getTabulaRenders());
        entityRenders.addAll(page.getEntityRenders());
        images.addAll(page.getImages());
        writtenTitle = page.generateTitle();
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
                        int indexPage = indexes.getPage();
                        if (indexPage == page) {
                            int buttonX = indexes.getX();
                            int buttonY = indexes.getY();
                            int width = indexes.getWidth();
                            int height = indexes.getHeight();
                            if (indexes.isDown()) {
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
                        lineToPrint = "" + (word.equals("<NEWLINE>") ? "" : word);
                    } else {
                        lineToPrint = lineToPrint + " " + word;
                        if (last) {
                            linesFromPrinting++;
                            this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                            yIndex++;
                            actualTextX = 0;
                            if (newline) {
                                yIndex++;
                            }
                        }
                    }
                }
                maxPagesFromPrinting = page;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: " + res);
        }
    }

    public void setEntityTooltip(String hoverText) {
        this.entityTooltip = hoverText;
    }

    public ResourceLocation getBookButtonsTexture(){
        return BOOK_BUTTONS_TEXTURE;
    }
}
