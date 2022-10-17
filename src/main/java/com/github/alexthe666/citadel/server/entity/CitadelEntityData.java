package com.github.alexthe666.citadel.server.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author Alexthe666
 * @since 1.7.0
 *
 * CitadelTag is a datasynced tag for LivingEntity provided by citadel to be used by various mods.
 */
public class CitadelEntityData {


    public static CompoundTag getOrCreateCitadelTag(LivingEntity entity) {
        CompoundTag tag = getCitadelTag(entity);
        return tag == null ? new CompoundTag() : tag;
    }

    public static CompoundTag getCitadelTag(LivingEntity entity) {
        return entity instanceof ICitadelDataEntity ? ((ICitadelDataEntity) entity).getCitadelEntityData() : new CompoundTag();
    }

    public static void setCitadelTag(LivingEntity entity, CompoundTag tag) {
        if(entity instanceof ICitadelDataEntity){
            ((ICitadelDataEntity) entity).setCitadelEntityData(tag);
        }
    }
}
