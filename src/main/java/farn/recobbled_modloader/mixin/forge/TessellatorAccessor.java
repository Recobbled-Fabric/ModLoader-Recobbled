

package farn.recobbled_modloader.mixin.forge;

import net.minecraft.src.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("MixinAnnotationTarget")
@Mixin(value = Tessellator.class, priority = 1001)
public interface TessellatorAccessor {
	@Accessor(value = "defaultTexture", remap = false)
	boolean getDefaultTexture();

	@Accessor(value = "defaultTexture", remap = false)
	void setDefaultTexture(boolean value);

	@Accessor(value = "renderingWorldRenderer", remap = false)
	static boolean getRenderingWorldRenderer() {
		return true;
	}

	@Accessor(value = "renderingWorldRenderer", remap = false)
	static void setRenderingWorldRenderer(boolean value) {
	}

	@Invoker("<init>")
	static Tessellator createTessellator(int id) {
		throw new AssertionError();
	}
}
