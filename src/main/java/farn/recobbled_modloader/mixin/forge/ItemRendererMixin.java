package farn.recobbled_modloader.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import forge.ForgeHooksClient;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    private Minecraft mc;

    @Inject(method = "renderItem", at = @At(value = "FIELD", target = "Lnet/minecraft/src/ItemStack;itemID:I", ordinal = 0), cancellable = true)
    private void forge$renderCustomItem(EntityLiving entityliving, ItemStack itemstack, CallbackInfo ci,
                                        @Share("terrainId") LocalIntRef terrainId) {
        terrainId.set(this.mc.renderEngine.getTexture("/terrain.png"));
    }

    @WrapOperation(method = "renderItem", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V", remap = false))
    private void forge$overrideTexture(int target, int texture, Operation<Void> original,
                                       @Local(argsOnly = true) ItemStack itemstack,
                                       @Share("terrainId") LocalIntRef terrainId) {
        original.call(target, texture);
        Object o = terrainId.get() == texture ? Block.blocksList[itemstack.itemID] : itemstack.getItem();
        ForgeHooksClient.overrideTexture(o);
    }
}
