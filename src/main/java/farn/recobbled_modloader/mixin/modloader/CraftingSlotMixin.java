package farn.recobbled_modloader.mixin.modloader;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class CraftingSlotMixin {

    @Shadow
    private EntityPlayer thePlayer;

    @Inject(method="onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;onCrafting(Lnet/minecraft/src/World;Lnet/minecraft/src/EntityPlayer;)V", shift = At.Shift.BEFORE))
    public void modloader_afterCraft(ItemStack par1, CallbackInfo ci) {
        ModLoader.TakenFromCrafting(this.thePlayer, par1);
    }
}
