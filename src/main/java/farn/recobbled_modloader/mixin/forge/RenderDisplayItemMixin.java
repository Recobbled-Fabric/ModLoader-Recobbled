package farn.recobbled_modloader.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import forge.ForgeHooksClient;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Pseudo
@Mixin(RenderDisplayItem.class)
public class RenderDisplayItemMixin {

    @WrapOperation(method = "doRenderDisplayItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderDisplayItem;loadTexture(Ljava/lang/String;)V"))
    private void forge$overrideTexture(RenderDisplayItem instance, String s, Operation<Void> original,
                                       @Local ItemStack itemstack) {
        original.call(instance, s);
        Object o = Objects.equals(s, "/terrain.png") ? Block.blocksList[itemstack.itemID] : itemstack.getItem();
        ForgeHooksClient.overrideTexture(o);
    }

    @Inject(method = "drawItemIntoGui", at = @At(value = "CONSTANT", args = "intValue=256", ordinal = 0))
    private void forge$renderCustomItem$2(FontRenderer fontrenderer, RenderEngine renderengine, int i, int j, int k, int l, int i1, CallbackInfo ci,
                                          @Share("terrainId") LocalIntRef terrainId) {
        terrainId.set(renderengine.getTexture("/terrain.png"));
    }

    @WrapOperation(method = "drawItemIntoGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderEngine;bindTexture(I)V"))
    private void forge$overrideTexture(RenderEngine instance, int texture, Operation<Void> original,
                                       @Local(argsOnly = true, ordinal = 0) int i,
                                       @Share("terrainId") LocalIntRef terrainId) {
        original.call(instance, texture);
        Object o = terrainId.get() == texture ? Block.blocksList[i] : Item.itemsList[i];
        ForgeHooksClient.overrideTexture(o);
    }
}
