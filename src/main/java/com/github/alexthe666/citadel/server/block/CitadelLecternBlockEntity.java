package com.github.alexthe666.citadel.server.block;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CitadelLecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    private ItemStack book = ItemStack.EMPTY;

    private final Container bookAccess = new Container() {
        public int getContainerSize() {
            return 1;
        }

        public boolean isEmpty() {
            return CitadelLecternBlockEntity.this.book.isEmpty();
        }

        public ItemStack getItem(int i) {
            return i == 0 ? CitadelLecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        public ItemStack removeItem(int i, int j) {
            if (i == 0) {
                ItemStack itemstack = CitadelLecternBlockEntity.this.book.split(j);
                if (CitadelLecternBlockEntity.this.book.isEmpty()) {
                    CitadelLecternBlockEntity.this.onBookItemRemove();
                }

                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        public ItemStack removeItemNoUpdate(int i) {
            if (i == 0) {
                ItemStack itemstack = CitadelLecternBlockEntity.this.book;
                CitadelLecternBlockEntity.this.book = ItemStack.EMPTY;
                CitadelLecternBlockEntity.this.onBookItemRemove();
                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        public void setItem(int i, ItemStack stack) {
        }

        public int getMaxStackSize() {
            return 1;
        }

        public void setChanged() {
            CitadelLecternBlockEntity.this.setChanged();
        }

        public boolean stillValid(Player p_59588_) {
            if (CitadelLecternBlockEntity.this.level.getBlockEntity(CitadelLecternBlockEntity.this.worldPosition) != CitadelLecternBlockEntity.this) {
                return false;
            } else {
                return p_59588_.distanceToSqr((double) CitadelLecternBlockEntity.this.worldPosition.getX() + 0.5D, (double) CitadelLecternBlockEntity.this.worldPosition.getY() + 0.5D, (double) CitadelLecternBlockEntity.this.worldPosition.getZ() + 0.5D) > 64.0D ? false : CitadelLecternBlockEntity.this.hasBook();
            }
        }

        public boolean canPlaceItem(int i, ItemStack stack) {
            return false;
        }

        public void clearContent() {
        }
    };
    //dummy container for page turning
    private final ContainerData dataAccess = new ContainerData() {
        public int get(int i) {
            return 0;
        }

        public void set(int i, int j) {
        }

        public int getCount() {
            return 1;
        }
    };

    public CitadelLecternBlockEntity(BlockPos pos, BlockState state) {
        super(Citadel.LECTERN_BE.get(), pos, state);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return LecternBooks.isLecternBook(book);
    }

    public void setBook(ItemStack stack) {
        this.setBook(stack, null);
    }

    void onBookItemRemove() {
        LecternBlock.resetBookState(null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack itemStack, @Nullable Player player) {
        this.book = itemStack;
        this.setChanged();
    }

    public int getRedstoneSignal() {
        return this.hasBook() ? 1 : 0;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Book", 10)) {
            this.book = ItemStack.parse(registries, tag.getCompound("Book")).orElse(ItemStack.EMPTY);
        } else {
            this.book = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.getBook().isEmpty()) {
            tag.put("Book", this.getBook().save(registries));
        }
    }

    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new LecternMenu(i, this.bookAccess, this.dataAccess);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }
}