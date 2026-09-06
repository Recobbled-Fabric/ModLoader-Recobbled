package farn.recobbled_modloader.mixin;

import net.minecraft.src.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value = RenderBlocks.class, priority = 1001)
public interface RenderBlockAccessor {
	@Accessor(value = "cfgGrassFix", remap = false)
	static boolean getCfgGrassFix() {
		return true;
	}

	@Accessor(value = "cfgGrassFix", remap = false)
	static void setCfgGrassFix(boolean value) {
	}
}
