package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CitadelEvents {

    private int updateTimer;

    @SubscribeEvent
    public void onEntityUpdateDebug(LivingEvent.LivingTickEvent event) {
        if (CitadelConstants.DEBUG) {
            if ((event.getEntity() instanceof Player)) {
                CompoundTag tag = CitadelEntityData.getCitadelTag(event.getEntity());
                tag.putInt("CitadelInt", tag.getInt("CitadelInt") + 1);
                Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + tag.getInt("CitadelInt"));
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getLevel().getBlockState(event.getPos()).is(Blocks.LECTERN) && LecternBooks.isLecternBook(event.getItemStack())){
            event.getEntity().getCooldowns().addCooldown(event.getItemStack().getItem(), 1);
            BlockState oldLectern = event.getLevel().getBlockState(event.getPos());
            if(event.getLevel().getBlockEntity(event.getPos()) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()){
                BlockState newLectern = Citadel.LECTERN.get().defaultBlockState().setValue(CitadelLecternBlock.FACING, oldLectern.getValue(LecternBlock.FACING)).setValue(CitadelLecternBlock.POWERED, oldLectern.getValue(LecternBlock.POWERED)).setValue(CitadelLecternBlock.HAS_BOOK, true);
                event.getLevel().setBlockAndUpdate(event.getPos(), newLectern);
                CitadelLecternBlockEntity newBe = new CitadelLecternBlockEntity(event.getPos(), newLectern);
                ItemStack bookCopy = event.getItemStack().copy();
                bookCopy.setCount(1);
                newBe.setBook(bookCopy);
                if(!event.getEntity().isCreative()){
                    event.getItemStack().shrink(1);
                }
                event.getLevel().setBlockEntity(newBe);
                event.getEntity().swing(event.getHand(), true);
                event.getLevel().playSound((Player)null, event.getPos(), SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() != null && CitadelEntityData.getCitadelTag(event.getOriginal()) != null) {
            CitadelEntityData.setCitadelTag(event.getEntity(), CitadelEntityData.getCitadelTag(event.getOriginal()));
        }
    }
}
