package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.GlobalTickRateModifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemCitadelBook extends Item {

    public ItemCitadelBook(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            Citadel.PROXY.openBookGUI(itemStackIn);
        }
        ServerTickRateTracker.modifyTickRate(worldIn, new GlobalTickRateModifier(100, 4F));
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemStackIn);
    }

}
