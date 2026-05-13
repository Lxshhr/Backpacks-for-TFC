package com.spydnel.backpacks.common.events;

import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.registry.BPBlocks;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

import static com.spydnel.backpacks.common.blocks.BackpackBlock.FACING;
import static com.spydnel.backpacks.common.blocks.BackpackBlock.WATERLOGGED;


public class BackpackPickupEvents {

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        ItemStack heldItem = player.getItemInHand(hand);

        boolean hasBackpack = BackpackUtils.hasBackpack(player);
        boolean canEquipBackpack = BackpackUtils.canEquipBackpack(player);
        boolean isBackpackBlock = block == BPBlocks.BACKPACK.get();

        boolean isAbove = (pos.above().getY() > player.getEyeY());
        boolean isUnobstructed = level.getBlockState(pos.above()).canBeReplaced() &&
                level.isUnobstructed(BPBlocks.BACKPACK.get().defaultBlockState(), pos.above(), CollisionContext.of(player)) &&
                !level.isOutsideBuildHeight(pos.above());

        // Pickup
        if (player.isShiftKeyDown() && canEquipBackpack && isBackpackBlock && blockEntity != null) {
            player.swing(InteractionHand.MAIN_HAND);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            itemstack.applyComponents(blockEntity.collectComponents());

            BackpackUtils.equipBackpack(player, itemstack);

            addParticles(level, pos);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }

            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }

        // Placement
        if (player.isShiftKeyDown() && heldItem.isEmpty() && hasBackpack && event.getFace() == Direction.UP && !isAbove && isUnobstructed) {
            ItemStack backpack = BackpackUtils.getEquippedBackpack(player);

            player.swing(InteractionHand.MAIN_HAND);

            BlockState state = BPBlocks.BACKPACK.get().defaultBlockState()
                    .setValue(FACING, player.getDirection())
                    .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

            blockEntity = new BackpackBlockEntity(pos.above(), state);
            blockEntity.applyComponentsFromItemStack(backpack);

            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos.above(), state);
                level.setBlockEntity(blockEntity);

                backpack.shrink(1);
                level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
            }

            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    public static void  onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            Player player = event.getPlayer();
            if (BackpackUtils.canEquipBackpack(player) && !itemEntity.hasPickUpDelay()) {
                ItemStack backpack = itemStack.copy();
                itemStack.setCount(0);
                BackpackUtils.equipBackpack(player, backpack);

                player.take(itemEntity, 1);
                itemEntity.discard();
                player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                player.onItemPickup(itemEntity);
            }
            event.setCanPickup(TriState.FALSE);
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0,0);
        }
    }
}
