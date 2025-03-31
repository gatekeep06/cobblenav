package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.metacontent.cobblenav.CobblenavItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokerodItem.class)
public class PokeRodItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    protected void injectUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (user.getOffhandItem().is(CobblenavItems.INSTANCE.getFISHINGNAV()) && user.isShiftKeyDown()) {
            cir.setReturnValue(InteractionResultHolder.pass(user.getItemInHand(hand)));
            cir.cancel();
        }
    }
}
