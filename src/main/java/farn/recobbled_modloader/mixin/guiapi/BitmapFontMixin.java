package farn.recobbled_modloader.mixin.guiapi;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.matthiasmann.twl.renderer.lwjgl.BitmapFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URL;

@Mixin(BitmapFont.class)
public class BitmapFontMixin {
	@Definition(id = "URL", type = URL.class)
	@Expression("new URL(?, ?)")
	@WrapOperation(method = "<init>*", at = @At("MIXINEXTRAS:EXPRESSION"))
	private URL fix$fontURL(URL context, String spec, Operation<URL> original) {
		return this.getClass().getResource("/" + spec);
	}
}
