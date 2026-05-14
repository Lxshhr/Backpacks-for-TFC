package com.spydnel.backpacks.common.container;

import com.spydnel.backpacks.networking.BackpackOpenPayload;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class BackpackItemMenu extends SimpleContainer {
    LivingEntity target;
    Player player;
    ItemStack itemStack;
    Level level;

    public BackpackItemMenu(LivingEntity target, Player player) {
        super(18);
        this.target = target;
        this.player = player;
        itemStack = BackpackUtils.getEquippedBackpack(target);
        level = target.level();
    }

    public boolean stillValid(Player player) {
        return target != null && itemStack.is(BPItems.BACKPACK) &&
                itemStack.has(DataComponents.CONTAINER) && player.distanceTo(target) < 5;
    }

    public void setChanged() {
        ItemStack current = BackpackUtils.getEquippedBackpack(this.target);
        current.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
        super.setChanged();
    }

    @Override
    public void startOpen(Player player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new BackpackOpenPayload(true, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_OPEN.value(), SoundSource.PLAYERS);
        super.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new BackpackOpenPayload(false, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_CLOSE.value(), SoundSource.PLAYERS);
        super.stopOpen(player);
    }
}
