package com.spydnel.backpacks.client;

import com.spydnel.backpacks.networking.OpenBackpackContainerPayload;
import com.spydnel.backpacks.registry.BPKeyBindings;
import com.spydnel.backpacks.utils.BackpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public class BPClientEvents {

    public static void init() {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(BPClientEvents::onKeyEvent);
    }

    private static void onKeyEvent(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.screen != null) return;

        if (BPKeyBindings.OPEN_BACKPACK.consumeClick()) {
            if (BackpackUtils.hasBackpack(player)) {
                PacketDistributor.sendToServer(new OpenBackpackContainerPayload());
            }
        }
    }

}
