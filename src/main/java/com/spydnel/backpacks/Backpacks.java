package com.spydnel.backpacks;

import com.spydnel.backpacks.client.BPClient;
import com.spydnel.backpacks.client.BPClientEvents;
import com.spydnel.backpacks.config.ServerConfig;
import com.spydnel.backpacks.networking.BackpackOpenPayload;
import com.spydnel.backpacks.networking.OpenBackpackContainerPayload;
import com.spydnel.backpacks.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(Backpacks.MOD_ID)
public class Backpacks {
    public static final String MOD_ID = "backpacks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Backpacks(IEventBus modEventBus, ModContainer modContainer) {
        BPDataAttatchments.ATTACHMENT_TYPES.register(modEventBus);
        BPBlocks.BLOCKS.register(modEventBus);
        BPItems.ITEMS.register(modEventBus);
        BPBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        BPSounds.SOUND_EVENTS.register(modEventBus);
        BPMenuTypes.MENU_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        modEventBus.addListener(this::register);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreative);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BPClient.init(modEventBus);
        }

        BPClientEvents.init();
    }

    private void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                BackpackOpenPayload.TYPE,
                BackpackOpenPayload.STREAM_CODEC,
                BackpackOpenPayload::handleClientData
        );

        registrar.playToServer(
                OpenBackpackContainerPayload.TYPE,
                OpenBackpackContainerPayload.STREAM_CODEC,
                OpenBackpackContainerPayload::handle
        );
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BPBlockEntities.BACKPACK.get(),
                (container, type) -> new InvWrapper(container)
        );
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BPItems.BACKPACK);
        }
    }
}
