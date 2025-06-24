package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.client.gui.interact.battleRequest.BattleConfigureGUI;
import com.metacontent.cobblenav.client.CobblenavClient;
import com.metacontent.cobblenav.client.gui.widget.contact.ContactSharingSwitch;
import com.metacontent.cobblenav.networking.packet.server.ContactSharingChoicePacket;
import kotlin.Pair;
import kotlin.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BattleConfigureGUI.class)
abstract public class BattleConfigureGUIMixin {
    @Shadow
    protected abstract Pair<Integer, Integer> getDimensions();

    @Unique
    private ContactSharingSwitch cobblenav$contactSharingSwitch;

    @Inject(method = "init", at = @At("HEAD"))
    protected void injectInit(CallbackInfo ci) {
        Pair<Integer, Integer> dimensions = getDimensions();
        boolean defaultValue = CobblenavClient.INSTANCE.getPokenavSettings() == null
                || CobblenavClient.INSTANCE.getPokenavSettings().getShareContacts();
        cobblenav$contactSharingSwitch = new ContactSharingSwitch(
                dimensions.getFirst() + 94,
                dimensions.getSecond() + 86,
                defaultValue,
                false,
                button -> {
                    CobblenavClient.INSTANCE.getPokenavSettings().setShareContacts(cobblenav$contactSharingSwitch.getEnabled());
                    return Unit.INSTANCE;
                }
        );
        ((ScreenMixin) this).invokeAddRenderableWidget(cobblenav$contactSharingSwitch);
    }

    @Inject(method = "closeGUI", at = @At("HEAD"), remap = false)
    protected void sendPacketOnClose(CallbackInfo ci) {
        new ContactSharingChoicePacket(cobblenav$contactSharingSwitch.getEnabled()).sendToServer();
    }
}
