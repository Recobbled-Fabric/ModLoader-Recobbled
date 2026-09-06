package farn.recobbled_modloader.mixin.modloader;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceMixin {

    @ModifyReturnValue(method = "getItemBurnTime", at = @At("TAIL"))
    private int modloader$AddAllFuel(int original,
                                     @Local int j) {
        if (original == 0) {
            return ModLoader.AddAllFuel(j);
        }

        return original;
    }
}
