package farn.recobbled_modloader.mixin;

import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderManager.class)
public class RenderManagerMixin {

    @Shadow
    private Map entityRenderMap;

    @Inject(method="<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", shift = At.Shift.BEFORE))
    public void recobbled_modloader_registerRenderer(CallbackInfo ci) {
        ModLoader.AddAllRenderers(entityRenderMap);
    }
}
