package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.common.container.BackpackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BPMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Backpacks.MOD_ID);

    public static final Supplier<MenuType<BackpackMenu>> BACKPACK = register("backpack", BackpackMenu::new);

    private static Supplier<MenuType<BackpackMenu>> register(String name, IContainerFactory<BackpackMenu> factory) {
        return MENU_TYPES.register(name, () -> IMenuTypeExtension.create(factory));
    }

}