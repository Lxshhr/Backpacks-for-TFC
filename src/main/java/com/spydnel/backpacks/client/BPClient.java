package com.spydnel.backpacks.client;

import com.spydnel.backpacks.client.models.BackpackModel;
import com.spydnel.backpacks.client.models.variants.OtherBackpackModel;
import com.spydnel.backpacks.client.rendering.BackpackBlockRenderer;
import com.spydnel.backpacks.client.rendering.BackpackLayer;
import com.spydnel.backpacks.client.screen.BackpackScreen;
import com.spydnel.backpacks.registry.*;
import com.spydnel.backpacks.utils.BPUtils;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class BPClient {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(BPClient::onClientSetup);
        modEventBus.addListener(BPClient::registerLayerDefinitions);
        modEventBus.addListener(BPClient::registerMenuScreens);
        modEventBus.addListener(BPClient::registerItemColors);
        modEventBus.addListener(BPClient::registerEntityRenderers);
        modEventBus.addListener(BPClient::registerPlayerLayers);
        modEventBus.addListener(BPClient::registerEntityLayers);
        modEventBus.addListener(BPClient::registerKeyBindings);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    BPItems.BACKPACK.asItem(),
                    BPUtils.loc("dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F
            );
        });
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BPLayers.BACKPACK, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.BACKPACK_BLOCK, BackpackModel::createBlockLayer);

        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(BPMenuTypes.BACKPACK.get(), BackpackScreen::new);
    }

    private static void registerItemColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? DyedItemColor.getOrDefault(stack, -1) : -1, BPItems.BACKPACK);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BPBlockEntities.BACKPACK.get(), BackpackBlockRenderer::new);
    }

    private static void registerPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }

        if (event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer armorStandRenderer) {
            armorStandRenderer.addLayer(new BackpackLayer<>(armorStandRenderer, event.getEntityModels()));
        }
    }

    public static void registerEntityLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(entityType);

            if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                humanoidMobRenderer.addLayer(new BackpackLayer<>(humanoidMobRenderer, event.getEntityModels()));

            }
        }
    }

    private static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(BPKeyBindings.OPEN_BACKPACK);
    }

}
