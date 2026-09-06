package farn.recobbled_modloader.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import forge.ForgeHooksClient;
import net.minecraft.src.Block;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Definition(id = "tessellator", field = "Lnet/minecraft/src/WorldRenderer;tessellator:Lnet/minecraft/src/Tessellator;")
    @Expression("tessellator.?()")
    @ModifyReceiver(method = {"updateRenderer"}, at = @At("MIXINEXTRAS:EXPRESSION"))
    private Tessellator forge$useDynamicInstance(Tessellator instance) {
        return Tessellator.instance;
    }

    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;startDrawingQuads()V"))
    private void forge$beforeRenderPass(CallbackInfo ci,
                                        @Local(ordinal = 7) int i2) {
        ForgeHooksClient.beforeRenderPass(i2);
    }

    @Definition(id = "var20", local = @Local(type = int.class, ordinal = 15))
    @Definition(id = "var11", local = @Local(type = int.class, ordinal = 7))
    @Expression("var20 != var11")
    @WrapOperation(method = "updateRenderer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean forge$skipCheck(int left, int right, Operation<Boolean> original) {
        return false;
    }

    @Expression("? == ?")
    @WrapOperation(method = "updateRenderer", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 5))
    private boolean forge$changeCheck(int left, int right, Operation<Boolean> original,
                                      @Local(ordinal = 8) LocalIntRef flag,
                                      @Local Block block) {
        if (left > right) {
            flag.set(1);
        }

        return ForgeHooksClient.canRenderInPass(block, right);
    }

    @WrapOperation(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderBlocks;renderBlockByRenderType(Lnet/minecraft/src/Block;III)Z"))
    private boolean forge$renderEvents(RenderBlocks instance, Block uu1, int i1, int j1, int k1, Operation<Boolean> original) {
        ForgeHooksClient.beforeBlockRender(uu1, instance);
        boolean result = original.call(instance, uu1, i1, j1, k1);
        ForgeHooksClient.afterBlockRender(uu1, instance);
        return result;
    }

    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;draw()V"))
    private void forge$afterRenderPass(CallbackInfo ci,
                                       @Local(ordinal = 7) int i2) {
        ForgeHooksClient.afterRenderPass(i2);
    }
}
