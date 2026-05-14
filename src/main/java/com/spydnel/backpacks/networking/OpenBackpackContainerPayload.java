package com.spydnel.backpacks.networking;

import com.spydnel.backpacks.BackpackWearer;
import com.spydnel.backpacks.common.container.BackpackItemMenu;
import com.spydnel.backpacks.common.container.BackpackMenu;
import com.spydnel.backpacks.common.items.BackpackItem;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import com.spydnel.backpacks.utils.BPUtils;
import com.spydnel.backpacks.utils.BackpackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenBackpackContainerPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenBackpackContainerPayload> TYPE = new CustomPacketPayload.Type<>(BPUtils.loc("open_backpack_container"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenBackpackContainerPayload> STREAM_CODEC = StreamCodec.unit(new OpenBackpackContainerPayload());

    public static void handle(final OpenBackpackContainerPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack backpack = BackpackUtils.getEquippedBackpack(player);

            if (!backpack.is(BPItems.BACKPACK.get())) return;

            BackpackItemMenu container = new BackpackItemMenu(player, player);
            if (!backpack.has(DataComponents.CONTAINER)) {
                backpack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            }
            backpack.get(DataComponents.CONTAINER).copyInto(container.getItems());

            player.openMenu(new SimpleMenuProvider((a, b, c) -> new BackpackMenu(a, player.getInventory(), container), Component.translatable("container.backpack")));

            player.level().playSound(null, player.blockPosition(), BPSounds.BACKPACK_OPEN.value(), SoundSource.PLAYERS);
        });
    }
}
