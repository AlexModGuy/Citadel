package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.GlobalTickRateModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemCitadelDebug extends Item {

    public ItemCitadelDebug(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        if(!playerIn.getCooldowns().isOnCooldown(this)){
            modifyTickRate(playerIn, playerIn.isShiftKeyDown() ? 0.5F : 2);
        }
        playerIn.getCooldowns().addCooldown(this, 15);
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemStackIn);
    }

    private void modifyTickRate(Player player, float multiplier){
        if(!player.level.isClientSide){
            player.sendSystemMessage(Component.literal("[DEBUG] multiplied ms tick length by " + multiplier));
        }
        ServerTickRateTracker.modifyTickRate(player.level, new GlobalTickRateModifier(100, multiplier));
    }
}
