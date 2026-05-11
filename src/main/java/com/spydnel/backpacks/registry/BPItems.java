package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.common.items.BackpackItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BPItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Backpacks.MOD_ID);

    public static final DeferredItem<BackpackItem> BACKPACK = ITEMS.register("backpack",
            () -> new BackpackItem(BPBlocks.BACKPACK.get(), new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
}
