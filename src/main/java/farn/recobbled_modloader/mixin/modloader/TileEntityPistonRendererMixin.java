package farn.recobbled_modloader.mixin.modloader;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import forge.ITextureProvider;
import net.minecraft.src.Block;
import net.minecraft.src.TileEntityRendererPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityRendererPiston.class)
public class TileEntityPistonRendererMixin {

    @WrapOperation(method="func_31070_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TileEntityRendererPiston;bindTextureByName(Ljava/lang/String;)V"))
    public void modloadermp_pistonTex(TileEntityRendererPiston instance, String s, Operation<Void> original, @Local Block block) {
        if(s.equals("/terrain.png") && block instanceof ITextureProvider) {
            s = ((ITextureProvider)block).getTextureFile();
        }
        original.call(instance, s);
    }


}
