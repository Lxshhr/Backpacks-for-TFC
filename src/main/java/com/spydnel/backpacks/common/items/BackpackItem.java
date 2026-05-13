package com.spydnel.backpacks.common.items;

import com.spydnel.backpacks.registry.BPSounds;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BackpackItem extends BlockItem implements Equipable, IItemSize {
    public BackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return BPSounds.BACKPACK_EQUIP;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!BackpackUtils.canEquipBackpack(player)) {
            return InteractionResultHolder.fail(heldItem);
        }

        if (BackpackUtils.equipBackpack(player, heldItem)) {
            if (!level.isClientSide) {
                player.setItemInHand(hand, ItemStack.EMPTY);
                player.level().playSound(null, player.blockPosition(), BPSounds.BACKPACK_EQUIP.value(), SoundSource.PLAYERS);
            }
            return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    @Override
    public Size getSize(ItemStack itemStack) {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack itemStack) {
        return itemStack.has(DataComponents.CONTAINER) ? Weight.VERY_HEAVY : Weight.HEAVY;
    }
}
