package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void citadel_collide(Vec3 vec, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ICustomCollisions) {
            cir.setReturnValue(ICustomCollisions.getAllowedMovementForEntity(self, vec));
        }
    }
}
