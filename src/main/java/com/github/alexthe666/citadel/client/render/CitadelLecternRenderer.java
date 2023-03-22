package com.github.alexthe666.citadel.client.render;

import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class CitadelLecternRenderer implements BlockEntityRenderer<CitadelLecternBlockEntity> {
    private final BookModel bookModel;
    public static final ResourceLocation BOOK_PAGE_TEXTURE =  new ResourceLocation("citadel:textures/entity/lectern_book_pages.png");
    public static final ResourceLocation BOOK_BINDING_TEXTURE = new ResourceLocation("citadel:textures/entity/lectern_book_binding.png");
    private static final LecternBooks.BookData EMPTY_BOOK_DATA = new LecternBooks.BookData(0XC58439, 0XF4E9BF);
    public CitadelLecternRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    public void render(CitadelLecternBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j) {
        BlockState blockstate = blockEntity.getBlockState();
        if (blockstate.getValue(LecternBlock.HAS_BOOK)) {
            LecternBooks.BookData bookData = LecternBooks.BOOKS.getOrDefault(ForgeRegistries.ITEMS.getKey(blockEntity.getBook().getItem()), EMPTY_BOOK_DATA);
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.0625D, 0.5D);
            float f = blockstate.getValue(LecternBlock.FACING).getClockWise().toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
            poseStack.translate(0.0D, -0.125D, 0.0D);
            this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
            int bindingR = (bookData.getBindingColor() & 0xFF0000) >> 16;
            int bindingG = (bookData.getBindingColor() & 0xFF00) >> 8;
            int bindingB = (bookData.getBindingColor() & 0xFF);
            int pageR = (bookData.getPageColor() & 0xFF0000) >> 16;
            int pageG = (bookData.getPageColor() & 0xFF00) >> 8;
            int pageB = (bookData.getPageColor() & 0xFF);
            VertexConsumer pages = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BOOK_PAGE_TEXTURE));
            this.bookModel.render(poseStack, pages, i, j, pageR / 255F, pageG / 255F, pageB / 255F, 1.0F);
            VertexConsumer binding = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BOOK_BINDING_TEXTURE));
            this.bookModel.render(poseStack, binding, i, j, bindingR / 255F, bindingG / 255F, bindingB / 255F, 1.0F);
            poseStack.popPose();
        }
    }
}