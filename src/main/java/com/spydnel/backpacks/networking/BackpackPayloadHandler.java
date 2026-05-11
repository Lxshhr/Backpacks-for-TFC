package com.spydnel.backpacks.networking;

import com.spydnel.backpacks.BackpackWearer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackPayloadHandler {

    public static void handleClientData(final BackpackOpenPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(payload.id());
            if (entity instanceof BackpackWearer backpackWearer) {
                if (payload.isOpen()) {
                    backpackWearer.onBackpackOpen();
                } else {
                    backpackWearer.onBackpackClose();
                }
            }
        });
    }
}
