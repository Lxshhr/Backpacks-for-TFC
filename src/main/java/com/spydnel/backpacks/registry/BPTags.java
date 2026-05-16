package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.utils.BPUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BPTags {
    public static class Items {
        public static final TagKey<Item> BACKPACK_BLACKLIST = ItemTags.create(BPUtils.loc("backpack_blacklist"));
    }
}
