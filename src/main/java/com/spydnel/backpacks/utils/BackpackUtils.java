package com.spydnel.backpacks.utils;

import com.spydnel.backpacks.compat.CuriosUtils;
import com.spydnel.backpacks.config.BPCommonConfig;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.Objects;

public class BackpackUtils {

    public static boolean curiosEnabled() {
        return BPUtils.isModLoaded("curios") && BPCommonConfig.enableCuriosCompat.get();
    }

    public static ItemStack getEquippedBackpack(LivingEntity entity) {
        if (curiosEnabled()) {
            ItemStack curio = CuriosUtils.getEquippedBackpack(entity);
            if (!curio.isEmpty()) return curio;
        }
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(BPItems.BACKPACK.get()) ? chest : ItemStack.EMPTY;
    }

    public static boolean hasBackpack(LivingEntity entity) {
        return !getEquippedBackpack(entity).isEmpty();
    }

    public static boolean equipBackpack(Player player, ItemStack itemStack) {
        if (!canEquipBackpack(player)) return false;
        if (curiosEnabled() && CuriosUtils.equipBackpack(player, itemStack)) {
            return true;
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            player.setItemSlot(EquipmentSlot.CHEST, itemStack);
            return true;
        }
        return false;
    }

    public static ItemStack unequipBackpack(Player player) {
        if (curiosEnabled()) {
            ItemStack curio = CuriosUtils.unequipBackpack(player);
            if (!curio.isEmpty()) return curio;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(BPItems.BACKPACK.get())) {
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            return chest;
        }
        return ItemStack.EMPTY;
    }

    public static boolean isBackpackVisible(LivingEntity entity) {
        if (curiosEnabled() && !CuriosUtils.getEquippedBackpack(entity).isEmpty()) {
            return CuriosUtils.isBackpackVisible(entity);
        }
        return true;
    }

    public static boolean canEquipBackpack(Player player) {
        if (curiosEnabled()) {
            return !CuriosUtils.hasBackpack(player) && !player.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK.get());
        }
        return player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    public static boolean isNonEmptyBackpack(ItemStack stack) {
        if (!stack.is(BPItems.BACKPACK)) return true;
        if (!stack.has(DataComponents.CONTAINER)) return true;
        return !Objects.equals(stack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);
    }

    public static int getCarryCount(Player player) {
        Container container = player.getInventory();
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            final ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(BPItems.BACKPACK)) {
                if (BackpackUtils.isNonEmptyBackpack(stack)) {
                    count++;
                    if (count == 2) {
                        break;
                    }
                }
            }
        }

        if (BackpackUtils.curiosEnabled()) {
            ItemStack stack = CuriosUtils.getEquippedBackpack(player);
            if (BackpackUtils.isNonEmptyBackpack(stack)) count++;
        }
        return count;
    }
}
