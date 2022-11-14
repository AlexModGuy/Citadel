package com.github.alexthe666.citadel.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CitadelLecternBlock extends LecternBlock {

    public CitadelLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new CitadelLecternBlockEntity(pos, blockState);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (level.isClientSide && blockEntity instanceof CitadelLecternBlockEntity lecternBlockEntity && lecternBlockEntity.hasBook()) {
            ItemStack book = lecternBlockEntity.getBook();
            if (!book.isEmpty() && !player.getCooldowns().isOnCooldown(book.getItem())) {
                book.use(level, player, hand);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);

    }


    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(HAS_BOOK)) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CitadelLecternBlockEntity) {
                return ((CitadelLecternBlockEntity) blockentity).getRedstoneSignal();
            }
        }

        return 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replaceState, boolean b) {
        if (!state.is(replaceState.getBlock())) {
            if (state.getValue(HAS_BOOK)) {
                this.popCitadelBook(state, level, pos);
            }

            if (state.getValue(POWERED)) {
                level.updateNeighborsAt(pos.below(), this);
            }

            super.onRemove(state, level, pos, replaceState, b);
        }
    }

    private void popCitadelBook(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof CitadelLecternBlockEntity lecternblockentity) {
            Direction direction = state.getValue(FACING);
            ItemStack itemstack = lecternblockentity.getBook().copy();
            float f = 0.25F * (float) direction.getStepX();
            float f1 = 0.25F * (float) direction.getStepZ();
            ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + 0.5D + (double) f, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D + (double) f1, itemstack);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
            lecternblockentity.clearContent();
        }

    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(Items.LECTERN);
    }
}
