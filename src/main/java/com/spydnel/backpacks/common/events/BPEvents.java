package com.spydnel.backpacks.common.events;

import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.common.container.BackpackItemMenu;
import com.spydnel.backpacks.common.container.BackpackMenu;
import com.spydnel.backpacks.compat.CuriosUtils;
import com.spydnel.backpacks.registry.BPBlocks;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import com.spydnel.backpacks.utils.BPUtils;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

import static com.spydnel.backpacks.common.blocks.BackpackBlock.FACING;
import static com.spydnel.backpacks.common.blocks.BackpackBlock.WATERLOGGED;

public class BPEvents {

    public static void init() {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(BPEvents::onRightClickBlock);
        bus.addListener(BPEvents::onItemEntityPickup);
        bus.addListener(BPEvents::onEntityInteract);
        bus.addListener(BPEvents::onEquipmentChange);

        bus.addListener(EventPriority.LOWEST, BPEvents::onPlayerTick);

        if (BPUtils.isModLoaded("curios")) {
            bus.addListener(BPEvents::onCurioEquip);
        }
    }

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

    public static void onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();

        if (itemStack.is(BPItems.BACKPACK) && BackpackUtils.isNonEmptyBackpack(itemStack)) {
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

    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.getAbilities().invulnerable) return;
        if (player.level().getGameTime() % 20 != 0) return;

        if (BackpackUtils.getCarryCount(player) >= 2) {
            player.addEffect(Helpers.getOverburdened(false));
        }
    }

    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getSlot() != EquipmentSlot.CHEST) return;

        ItemStack stack = event.getTo();
        if (!stack.is(BPItems.BACKPACK.get())) return;

        if (BackpackUtils.curiosEnabled() && CuriosUtils.hasBackpack(player)) {
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            player.drop(stack, true, false);
        }
    }

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

    public static void onCurioEquip(CurioCanEquipEvent event) {
        if (!event.getStack().is(BPItems.BACKPACK.get())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK.get()) || CuriosUtils.hasBackpack(player)) {
            event.setEquipResult(TriState.FALSE);
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0,0);
        }
    }
}
