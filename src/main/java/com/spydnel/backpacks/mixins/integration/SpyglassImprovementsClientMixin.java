package com.spydnel.backpacks.mixins.integration;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.utils.BackpackUtils;
import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import me.juancarloscp52.spyglass_improvements.mixin.MinecraftClientInvoker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("spyglass_improvements")
@Mixin(SpyglassImprovementsClient.class)
public abstract class SpyglassImprovementsClientMixin {

    @Shadow
    public static KeyMapping useSpyglass;

    @Shadow
    private void forceUseSpyglass(LocalPlayer player) { }

    @Inject(method = "onClientTick", at = @At("HEAD"))
    public void backpacks$onClientTick(Minecraft client, CallbackInfo ci) {
        if (client.player != null && client.gameMode != null && useSpyglass.isDown()) {
            ItemStack backpack = BackpackUtils.getEquippedBackpack(client.player);
            if (((MinecraftClientInvoker) client).getItemUseCooldown() == 0 &&
                    !client.player.isUsingItem() &&
                    backpack.is(BPItems.BACKPACK) &&
                    backpack.has(DataComponents.CONTAINER) &&
                    backpack.get(DataComponents.CONTAINER).stream().anyMatch(o -> o.is(Items.SPYGLASS))
            ) {
                forceUseSpyglass(client.player);
            }
        }
    }
}
