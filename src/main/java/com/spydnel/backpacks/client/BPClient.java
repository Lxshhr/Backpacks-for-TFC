package com.spydnel.backpacks.client;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.client.screen.BackpackScreen;
import com.spydnel.backpacks.registry.BPMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Backpacks.MOD_ID, value = Dist.CLIENT)
public class BPClient {

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(BPMenuTypes.BACKPACK.get(), BackpackScreen::new);
    }

}
