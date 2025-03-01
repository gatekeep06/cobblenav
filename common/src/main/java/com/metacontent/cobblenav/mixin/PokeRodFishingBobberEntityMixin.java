package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity;
import com.metacontent.cobblenav.event.CobblenavEvents;
import com.metacontent.cobblenav.event.FishTravelingStartedEvent;
import com.metacontent.cobblenav.util.FishTravelChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokeRodFishingBobberEntity.class)
public abstract class PokeRodFishingBobberEntityMixin implements FishTravelChecker {
    @Shadow private int fishTravelCountdown;

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
            CobblenavEvents.INSTANCE.getFISH_TRAVEL_STARTED().emit(new FishTravelingStartedEvent(player));
        }
    }

    @Override
    public boolean cobblenav$isTraveling() {
        return fishTravelCountdown > 0;
    }
}
