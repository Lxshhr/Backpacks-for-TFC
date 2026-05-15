package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.config.BPCommonConfig;
import com.spydnel.backpacks.registry.BPBlocks;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.spydnel.backpacks.common.blocks.BackpackBlock.*;

@Mixin(value = ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements TraceableEntity {

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract int getAge();

    @Shadow
    public abstract void setExtendedLifetime();

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void backpacks$tick(CallbackInfo ci) {
        if (!BPCommonConfig.placeBackpackOnDeath.get()) {
            return;
        }

        ItemStack itemStack = this.getItem();
        if (!itemStack.is(BPItems.BACKPACK.get())) {
            return;
        }

        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        ItemContainerContents container = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        boolean isEmpty = !hasContainer || container.stream().allMatch(ItemStack::isEmpty);

        if (hasContainer && !isEmpty) {
            if (this.getAge() > 0 ) {
                this.setExtendedLifetime();
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1.0,0.9));

            if (this.isInFluidType()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.02, 0.0));
            }

            Level level = this.level();
            BlockPos pos = this.blockPosition();
            BlockPos targetPos = level.getBlockState(pos).canBeReplaced() ? pos : pos.above();

            boolean isUnobstructed = !level.isOutsideBuildHeight(targetPos) && level.getBlockState(targetPos).canBeReplaced() &&
                    (!level.getFluidState(targetPos).isSource() || !level.getBlockState(targetPos.above()).canBeReplaced());

            if ((this.onGround() || level.getFluidState(pos).isSource()) && isUnobstructed) {

                BlockState state = BPBlocks.BACKPACK.get().defaultBlockState()
                        .setValue(FACING, this.getDirection())
                        .setValue(FLOATING, level.getFluidState(targetPos.below()).isSource() && !level.getFluidState(targetPos).isSource())
                        .setValue(WATERLOGGED, level.getFluidState(targetPos).getType() == Fluids.WATER);

                BackpackBlockEntity blockEntity = new BackpackBlockEntity(targetPos, state);
                blockEntity.applyComponentsFromItemStack(itemStack);

                blockEntity.newlyPlaced = true;
                blockEntity.placeTicks = 0;

                if (!level.isClientSide) {
                    level.setBlockAndUpdate(targetPos, state);
                    level.setBlockEntity(blockEntity);
                    level.playSound(null, targetPos, BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
                }

                this.discard();
            }
        }
    }
}
