package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.util.PreferencesSaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract public class EntityMixin implements PreferencesSaver {
    @Unique private CompoundTag cobblenav$savedPreferences;

    @Override
    public CompoundTag cobblenav$getSavedPreferences() {
        if (cobblenav$savedPreferences == null) {
            cobblenav$savedPreferences = new CompoundTag();
        }
        return cobblenav$savedPreferences;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    protected void injectWriteMethod(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        if (cobblenav$savedPreferences != null) {
            compoundTag.put(SAVED_PREFERENCES_KEY, cobblenav$savedPreferences);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    protected void injectReadMethod(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(SAVED_PREFERENCES_KEY)) {
            cobblenav$savedPreferences = compoundTag.getCompound(SAVED_PREFERENCES_KEY);
        }
    }
}
