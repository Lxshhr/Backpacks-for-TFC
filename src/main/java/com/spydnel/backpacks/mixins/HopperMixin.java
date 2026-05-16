package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public class HopperMixin {

    @Inject(
            method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void backpack$addItem(Container container, ItemEntity item, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = item.getItem();
        if (!stack.is(BPItems.BACKPACK.get())) return;

        boolean isEmpty = Objects.equals(stack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);
        if (!isEmpty) {
            cir.setReturnValue(false);
        }
    }
}
