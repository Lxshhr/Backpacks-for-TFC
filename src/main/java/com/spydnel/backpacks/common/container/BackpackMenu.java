package com.spydnel.backpacks.common.container;

import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.registry.BPMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackMenu extends AbstractContainerMenu {
    private final Container container;
    private final Player player;

    public BackpackMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, new SimpleContainer(18));
    }

    public BackpackMenu(int containerId, Inventory playerInv, Container container) {
        super(BPMenuTypes.BACKPACK.get(), containerId);
        checkContainerSize(container, 18);

        this.container = container;
        this.player = playerInv.player;

        container.startOpen(this.player);
        addInventorySlots();

        addPlayerSlots(playerInv);
    }

    private void addInventorySlots() {
        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new BackpackSlot(this.container, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
    }

    private void addPlayerSlots(Inventory playerInv) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 68 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 126));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return itemStack;

        ItemStack slotStack = slot.getItem();
        itemStack = slotStack.copy();

        int backpackSize = this.container.getContainerSize();
        int totalSlots = this.slots.size();

        if (index < backpackSize) {
            if (!this.moveItemStackTo(slotStack, backpackSize, totalSlots, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 0, backpackSize, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class BackpackSlot extends Slot {
        public BackpackSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return super.mayPlace(stack) && BackpackBlockEntity.isValid(stack) && stack.canFitInsideContainerItems();
        }
    }
}