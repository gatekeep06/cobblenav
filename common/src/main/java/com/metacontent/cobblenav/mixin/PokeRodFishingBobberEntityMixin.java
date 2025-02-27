package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity;
import com.metacontent.cobblenav.event.CobblenavEvents;
import com.metacontent.cobblenav.event.FishTravelingStartedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokeRodFishingBobberEntity.class)
public abstract class PokeRodFishingBobberEntityMixin {
    @Inject(
            method = "tickFishingLogic",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/cobblemon/mod/common/entity/fishing/PokeRodFishingBobberEntity;fishTravelCountdown:I",
                    ordinal = 7
            )
    )
    protected void injectTick(BlockPos pos, CallbackInfo ci) {
        if (((PokeRodFishingBobberEntity)(Object)this).getPlayerOwner() instanceof ServerPlayer player) {
            CobblenavEvents.INSTANCE.getFISH_TRAVELING_STARTED().emit(new FishTravelingStartedEvent(player));
        }
    }
}
