package farn.recobbled_modloader.mixin.modloadermp;

import net.minecraft.src.GuiMultiplayer;
import net.minecraft.src.ModLoaderMp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public class GuiMPMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modloadermp$ForceInit(CallbackInfo ci) {
        ModLoaderMp.Init();
    }
}
