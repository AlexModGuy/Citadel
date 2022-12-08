package com.github.alexthe666.citadel.client.model;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class TabulaModelRenderUtils {

    @OnlyIn(Dist.CLIENT)
    static class PositionTextureVertex {
        public final Vector3f position;
        public final float textureU;
        public final float textureV;

        public PositionTextureVertex(float p_i1158_1_, float p_i1158_2_, float p_i1158_3_, float p_i1158_4_, float p_i1158_5_) {
            this(new Vector3f(p_i1158_1_, p_i1158_2_, p_i1158_3_), p_i1158_4_, p_i1158_5_);
        }

        public TabulaModelRenderUtils.PositionTextureVertex setTextureUV(float p_78240_1_, float p_78240_2_) {
            return new TabulaModelRenderUtils.PositionTextureVertex(this.position, p_78240_1_, p_78240_2_);
        }

        public PositionTextureVertex(Vector3f p_i225952_1_, float p_i225952_2_, float p_i225952_3_) {
            this.position = p_i225952_1_;
            this.textureU = p_i225952_2_;
            this.textureV = p_i225952_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class TexturedQuad {
        public final TabulaModelRenderUtils.PositionTextureVertex[] vertexPositions;
        public final Vector3f normal;

        public TexturedQuad(TabulaModelRenderUtils.PositionTextureVertex[] p_i225951_1_, float p_i225951_2_, float p_i225951_3_, float p_i225951_4_, float p_i225951_5_, float p_i225951_6_, float p_i225951_7_, boolean p_i225951_8_, Direction p_i225951_9_) {
            this.vertexPositions = p_i225951_1_;
            float lvt_10_1_ = 0.0F / p_i225951_6_;
            float lvt_11_1_ = 0.0F / p_i225951_7_;
            p_i225951_1_[0] = p_i225951_1_[0].setTextureUV(p_i225951_4_ / p_i225951_6_ - lvt_10_1_, p_i225951_3_ / p_i225951_7_ + lvt_11_1_);
            p_i225951_1_[1] = p_i225951_1_[1].setTextureUV(p_i225951_2_ / p_i225951_6_ + lvt_10_1_, p_i225951_3_ / p_i225951_7_ + lvt_11_1_);
            p_i225951_1_[2] = p_i225951_1_[2].setTextureUV(p_i225951_2_ / p_i225951_6_ + lvt_10_1_, p_i225951_5_ / p_i225951_7_ - lvt_11_1_);
            p_i225951_1_[3] = p_i225951_1_[3].setTextureUV(p_i225951_4_ / p_i225951_6_ - lvt_10_1_, p_i225951_5_ / p_i225951_7_ - lvt_11_1_);
            if (p_i225951_8_) {
                int lvt_12_1_ = p_i225951_1_.length;

                for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_12_1_ / 2; ++lvt_13_1_) {
                    TabulaModelRenderUtils.PositionTextureVertex lvt_14_1_ = p_i225951_1_[lvt_13_1_];
                    p_i225951_1_[lvt_13_1_] = p_i225951_1_[lvt_12_1_ - 1 - lvt_13_1_];
                    p_i225951_1_[lvt_12_1_ - 1 - lvt_13_1_] = lvt_14_1_;
                }
            }

            this.normal = p_i225951_9_.step();
            if (p_i225951_8_) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ModelBox {
        public final TabulaModelRenderUtils.TexturedQuad[] quads;
        public final float posX1;
        public final float posY1;
        public final float posZ1;
        public final float posX2;
        public final float posY2;
        public final float posZ2;

        public ModelBox(int p_i225950_1_, int p_i225950_2_, float p_i225950_3_, float p_i225950_4_, float p_i225950_5_, float p_i225950_6_, float p_i225950_7_, float p_i225950_8_, float p_i225950_9_, float p_i225950_10_, float p_i225950_11_, boolean p_i225950_12_, float p_i225950_13_, float p_i225950_14_) {
            this.posX1 = p_i225950_3_;
            this.posY1 = p_i225950_4_;
            this.posZ1 = p_i225950_5_;
            this.posX2 = p_i225950_3_ + p_i225950_6_;
            this.posY2 = p_i225950_4_ + p_i225950_7_;
            this.posZ2 = p_i225950_5_ + p_i225950_8_;
            this.quads = new TabulaModelRenderUtils.TexturedQuad[6];
            float lvt_15_1_ = p_i225950_3_ + p_i225950_6_;
            float lvt_16_1_ = p_i225950_4_ + p_i225950_7_;
            float lvt_17_1_ = p_i225950_5_ + p_i225950_8_;
            p_i225950_3_ -= p_i225950_9_;
            p_i225950_4_ -= p_i225950_10_;
            p_i225950_5_ -= p_i225950_11_;
            lvt_15_1_ += p_i225950_9_;
            lvt_16_1_ += p_i225950_10_;
            lvt_17_1_ += p_i225950_11_;
            if (p_i225950_12_) {
                float lvt_18_1_ = lvt_15_1_;
                lvt_15_1_ = p_i225950_3_;
                p_i225950_3_ = lvt_18_1_;
            }

            TabulaModelRenderUtils.PositionTextureVertex lvt_18_2_ = new TabulaModelRenderUtils.PositionTextureVertex(p_i225950_3_, p_i225950_4_, p_i225950_5_, 0.0F, 0.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_19_1_ = new TabulaModelRenderUtils.PositionTextureVertex(lvt_15_1_, p_i225950_4_, p_i225950_5_, 0.0F, 8.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_20_1_ = new TabulaModelRenderUtils.PositionTextureVertex(lvt_15_1_, lvt_16_1_, p_i225950_5_, 8.0F, 8.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_21_1_ = new TabulaModelRenderUtils.PositionTextureVertex(p_i225950_3_, lvt_16_1_, p_i225950_5_, 8.0F, 0.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_22_1_ = new TabulaModelRenderUtils.PositionTextureVertex(p_i225950_3_, p_i225950_4_, lvt_17_1_, 0.0F, 0.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_23_1_ = new TabulaModelRenderUtils.PositionTextureVertex(lvt_15_1_, p_i225950_4_, lvt_17_1_, 0.0F, 8.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_24_1_ = new TabulaModelRenderUtils.PositionTextureVertex(lvt_15_1_, lvt_16_1_, lvt_17_1_, 8.0F, 8.0F);
            TabulaModelRenderUtils.PositionTextureVertex lvt_25_1_ = new TabulaModelRenderUtils.PositionTextureVertex(p_i225950_3_, lvt_16_1_, lvt_17_1_, 8.0F, 0.0F);
            float lvt_26_1_ = (float)p_i225950_1_;
            float lvt_27_1_ = (float)p_i225950_1_ + p_i225950_8_;
            float lvt_28_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_;
            float lvt_29_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_6_;
            float lvt_30_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_;
            float lvt_31_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_ + p_i225950_6_;
            float lvt_32_1_ = (float)p_i225950_2_;
            float lvt_33_1_ = (float)p_i225950_2_ + p_i225950_8_;
            float lvt_34_1_ = (float)p_i225950_2_ + p_i225950_8_ + p_i225950_7_;
            this.quads[2] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_23_1_, lvt_22_1_, lvt_18_2_, lvt_19_1_}, lvt_27_1_, lvt_32_1_, lvt_28_1_, lvt_33_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.DOWN);
            this.quads[3] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_20_1_, lvt_21_1_, lvt_25_1_, lvt_24_1_}, lvt_28_1_, lvt_33_1_, lvt_29_1_, lvt_32_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.UP);
            this.quads[1] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_18_2_, lvt_22_1_, lvt_25_1_, lvt_21_1_}, lvt_26_1_, lvt_33_1_, lvt_27_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.WEST);
            this.quads[4] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_19_1_, lvt_18_2_, lvt_21_1_, lvt_20_1_}, lvt_27_1_, lvt_33_1_, lvt_28_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.NORTH);
            this.quads[0] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_23_1_, lvt_19_1_, lvt_20_1_, lvt_24_1_}, lvt_28_1_, lvt_33_1_, lvt_30_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.EAST);
            this.quads[5] = new TabulaModelRenderUtils.TexturedQuad(new TabulaModelRenderUtils.PositionTextureVertex[]{lvt_22_1_, lvt_23_1_, lvt_24_1_, lvt_25_1_}, lvt_30_1_, lvt_33_1_, lvt_31_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.SOUTH);
        }
    }
}
