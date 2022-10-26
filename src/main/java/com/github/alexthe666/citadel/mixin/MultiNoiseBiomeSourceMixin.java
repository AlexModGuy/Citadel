package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {

    private int lastSampledX;
    private int lastSampledY;
    private int lastSampledZ;

    @Inject(at = @At("HEAD"),
            remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;"
    )
    private void citadel_getNoiseBiomeCoords(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        lastSampledX = x;
        lastSampledY = y;
        lastSampledZ = z;
    }

        @Inject(at = @At("RETURN"),
            cancellable = true,
            remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(Lnet/minecraft/world/level/biome/Climate$TargetPoint;)Lnet/minecraft/core/Holder;"
    )
    private void citadel_getNoiseBiome(Climate.TargetPoint targetPoint, CallbackInfoReturnable<Holder<Biome>> cir) {
        float f = Climate.unquantizeCoord(targetPoint.continentalness());
        float f1 = Climate.unquantizeCoord(targetPoint.erosion());
        float f2 = Climate.unquantizeCoord(targetPoint.temperature());
        float f3 = Climate.unquantizeCoord(targetPoint.humidity());
        float f4 = Climate.unquantizeCoord(targetPoint.weirdness());
        float f5 = Climate.unquantizeCoord(targetPoint.depth());
        EventReplaceBiome event = new EventReplaceBiome((ExpandedBiomeSource) this, cir.getReturnValue(), lastSampledX, lastSampledY, lastSampledZ, f, f1, f2, f3, f4, f5);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult() == Event.Result.ALLOW){
            cir.setReturnValue(event.getBiomeToGenerate());
        }

    }



}
