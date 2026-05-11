package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.common.blocks.BackpackBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

@SuppressWarnings("deprecation")
public class BPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Backpacks.MOD_ID);

    public static final DeferredBlock<BackpackBlock> BACKPACK = BLOCKS.register(
            "backpack", () -> new BackpackBlock(ofFullCopy(Blocks.BROWN_WOOL)
                    .sound(new SoundType(1.0F, 1.0F,
                            SoundEvents.WOOL_BREAK,
                            SoundEvents.WOOL_STEP,
                            BPSounds.BACKPACK_PLACE.value(),
                            SoundEvents.WOOL_HIT,
                            SoundEvents.WOOL_FALL))
                    .forceSolidOn()));
}
