package com.spydnel.backpacks.common.events;

import com.spydnel.backpacks.common.container.BackpackItemMenu;
import com.spydnel.backpacks.common.container.BackpackMenu;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class EntityInteractionEvents{

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        LivingEntity target = event.getTarget() instanceof LivingEntity ? (LivingEntity) event.getTarget() : null;
        ItemStack item = target != null ? BackpackUtils.getEquippedBackpack(target) : null;

        if (target != null && item.is(BPItems.BACKPACK) && isBehind(player, target)) {
            BackpackItemMenu container = new BackpackItemMenu(target, player);
            if (!item.has(DataComponents.CONTAINER)) {
                item.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            }

            item.get(DataComponents.CONTAINER).copyInto(container.getItems());

            player.openMenu(new SimpleMenuProvider((a, b, c) -> new BackpackMenu(a, player.getInventory(), container), Component.translatable("container.backpack")));

            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }

    public static boolean isBehind(Player player, LivingEntity target) {
        float t = 1.0F;
        Vec3 vector = player.getPosition(t).subtract(target.getPosition(t)).normalize();
        vector = new Vec3(vector.x, 0, vector.z);
        return target.getViewVector(t).dot(vector) < 0;
    }
}
