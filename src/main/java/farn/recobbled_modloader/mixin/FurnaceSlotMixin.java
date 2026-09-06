package farn.recobbled_modloader.mixin;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.SlotFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotFurnace.class)
public class FurnaceSlotMixin {

    @Shadow
    private EntityPlayer thePlayer;

    @Inject(method="onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;onCrafting(Lnet/minecraft/src/World;Lnet/minecraft/src/EntityPlayer;)V", shift = At.Shift.BEFORE))
    public void modloader_afterCraft(ItemStack par1, CallbackInfo ci) {
        ModLoader.TakenFromCrafting(this.thePlayer, par1);
    }
}
