package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.SpawnPool;
import com.metacontent.cobblenav.event.CobblenavEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CobblemonSpawnPools.class)
public abstract class CobblemonSpawnPoolsMixin {
    @Shadow
    public static SpawnPool WORLD_SPAWN_POOL;

    @Inject(method = "onServerLoad", at = @At("HEAD"), remap = false)
    protected void inject(MinecraftServer server, CallbackInfo ci) {
        CobblenavEvents.INSTANCE.getSPAWN_POOL_LOADED().emit(WORLD_SPAWN_POOL);
    }
}
