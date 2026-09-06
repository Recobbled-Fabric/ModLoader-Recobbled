package farn.recobbled_modloader.mixin.modloader;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.recobbled_modloader.mixin.forge.TessellatorAccessor;
import farn.recobbled_modloader.mixin_config.MakePublic;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlocks.class)
public class RenderBlocksMixin {
    @Shadow
    public IBlockAccess blockAccess;
    @MakePublic
    private static boolean cfgGrassFix = true;
    @MakePublic
    private static float[][] redstoneColors = new float[16][];

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void modloader$classInit(CallbackInfo ci) {
        for(int i = 0; i < redstoneColors.length; ++i) {
            float j = (float)i / 15.0F;
            float red = j * 0.6F + 0.4F;
            if (i == 0) {
                j = 0.0F;
            }

            float green = j * j * 0.7F - 0.5F;
            float blue = j * j * 0.6F - 0.7F;
            if (green < 0.0F) {
                green = 0.0F;
            }

            if (blue < 0.0F) {
                blue = 0.0F;
            }

            redstoneColors[i] = new float[]{red, green, blue};
        }
    }

    @MakePublic
    private static void setRedstoneColors(float[][] colors) {
        if (colors.length != 16) {
            throw new IllegalArgumentException("Must be 16 colors.");
        } else {
            for (float[] color : colors) {
                if (color.length != 3) {
                    throw new IllegalArgumentException("Must be 3 channels in a color.");
                }
            }

            redstoneColors = colors;
        }
    }

    @Inject(method = "renderBlockOnInventory", at = @At("RETURN"))
    private void modloader$RenderInvBlock(Block uu1, int i1, float f1, CallbackInfo ci) {
        int k1 = uu1.getRenderType();

        if (k1 != 0 && k1 != 16) {
            switch (k1) {
                case 1:
                case 2:
                case 6:
                case 10:
                case 11:
                case 13:
                    break;
                default:
                    ModLoader.RenderInvBlock((RenderBlocks) (Object) this, uu1, i1, k1);
            }
        }
    }

    @ModifyReturnValue(method = "renderItemIn3d", at = @At(value = "RETURN"))
    private static boolean modloader$RenderBlockIsItemFull3D(boolean original,
                                                             @Local(argsOnly = true) int i1) {
        if (!original || i1 == 16) {
            return ModLoader.RenderBlockIsItemFull3D(i1);
        }

        return true;
    }

    @ModifyReturnValue(method = "renderBlockByRenderType", at = @At(value = "RETURN", ordinal = 18))
    private boolean modloader$RenderWorldBlock(boolean original,
                                               @Local(argsOnly = true) Block uu1,
                                               @Local(ordinal = 0, argsOnly = true) int i1,
                                               @Local(ordinal = 1, argsOnly = true) int j1,
                                               @Local(ordinal = 2, argsOnly = true) int k1,
                                               @Local(ordinal = 3) int l1) {
        if (!original) {
            return ModLoader.RenderWorldBlock((RenderBlocks) (Object) this, this.blockAccess, i1, j1, k1, uu1, l1);
        }

        return true;
    }

    @WrapOperation(method = {"renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier"}, at = @At(value = "FIELD", target = "Lnet/minecraft/src/RenderBlocks;fancyGrass:Z"))
    private boolean modloader$ReplaceFancyGraphicsWithGrassFix(Operation<Boolean> original) {
        return ((TessellatorAccessor) Tessellator.instance).getDefaultTexture() && cfgGrassFix;
    }

    @WrapOperation(method = "renderBlockRedstoneWire", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 0),
            @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 3),
            @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 6),
            @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 8),
            @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_F(FFF)V", ordinal = 10),
    })
    private void modloader$RenderRedstoneColor(Tessellator instance, float g, float b, float v, Operation<Void> original,
                                               @Local(ordinal = 3) int l1,
                                               @Local(ordinal = 0) float f1) {
        float[] color = redstoneColors[l1];
        float f3 = color[0];
        float f4 = color[1];
        float f5 = color[2];
        original.call(instance, f1 * f3, f1 * f4, f1 * f5);
    }
}
