package farn.recobbled_modloader.mixin;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public class EntityItemMixin {

    @Shadow
    public ItemStack item;

    @Inject(method="onCollideWithPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;onItemPickup(Lnet/minecraft/src/Entity;I)V"))
    public void modloader_pickup(EntityPlayer par1, CallbackInfo ci) {
        ModLoader.OnItemPickup(par1, this.item);
    }
}
