package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.CelestialTickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.LocalPositionTickRateModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemCitadelDebug extends Item {

    public ItemCitadelDebug(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        playerIn.getCooldowns().addCooldown(this, 15);
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemStackIn);
    }

}
