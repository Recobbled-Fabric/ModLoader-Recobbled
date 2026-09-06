package farn.recobbled_modloader.mixin.modloader;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.BlockDispenser;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockDispenser.class)
public class BlockDispenserMixin {

    @Inject(method = "dispenseItem", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Item;arrow:Lnet/minecraft/src/Item;"), cancellable = true)
    private void modloader$DispenseEntity(World paramfd, int paramInt1, int paramInt2, int paramInt3, Random paramRandom, CallbackInfo ci,
                                          @Local(ordinal = 0) double d1,
                                          @Local(ordinal = 1) double d2,
                                          @Local(ordinal = 2) double d3,
                                          @Local(ordinal = 4) int j,
                                          @Local(ordinal = 5) int k,
                                          @Local() ItemStack localiz) {
        if (ModLoader.DispenseEntity(paramfd, d1, d2, d3, j, k, localiz)) {
            paramfd.func_28106_e(2000, paramInt1, paramInt2, paramInt3, j + 1 + (k + 1) * 3);
            ci.cancel();
        }
    }
}
