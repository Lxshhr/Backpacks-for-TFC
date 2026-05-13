package com.spydnel.backpacks.utils;

import com.spydnel.backpacks.compat.CuriosUtils;
import com.spydnel.backpacks.config.ServerConfig;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackUtils {

    private static boolean curiosEnabled() {
        return BPUtils.isModLoaded("curios") && ServerConfig.enableCuriosCompat.get();
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
}
