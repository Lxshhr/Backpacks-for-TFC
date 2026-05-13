package com.spydnel.backpacks.compat;

import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class CuriosUtils {

    public static ItemStack getEquippedBackpack(LivingEntity entity) {
        SlotResult curioSlot = getCurio(entity);
        return curioSlot != null ? curioSlot.stack() : ItemStack.EMPTY;
    }

    public static boolean hasBackpack(LivingEntity entity) {
        return getCurio(entity) != null;
    }

    public static boolean equipBackpack(Player player, ItemStack itemStack) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(player);
        if (curios.isEmpty()) return false;

        var backSlot = curios.get().getCurios().get("back");
        if (backSlot == null) return false;

        var inventory = backSlot.getStacks();
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                inventory.setStackInSlot(i, itemStack);
                return true;
            }
        }
        return false;
    }

    public static ItemStack unequipBackpack(Player player) {
        SlotResult curioSlot = getCurio(player);
        if (curioSlot == null) return ItemStack.EMPTY;

        var inventory = CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.getCurios().get("back"))
                .map(ICurioStacksHandler::getStacks)
                .orElse(null);
        if (inventory == null) return ItemStack.EMPTY;

        int slotIndex = curioSlot.slotContext().index();
        ItemStack itemStack = inventory.getStackInSlot(slotIndex);
        inventory.setStackInSlot(slotIndex, ItemStack.EMPTY);
        return itemStack;
    }

    public static boolean isBackpackVisible(LivingEntity entity) {
        SlotResult result = getCurio(entity);
        if (result == null) return false;
        return result.slotContext().visible();
    }

    @Nullable
    public static SlotResult getCurio(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).flatMap(inv -> inv.findFirstCurio(BPItems.BACKPACK.get())).orElse(null);
    }
}
