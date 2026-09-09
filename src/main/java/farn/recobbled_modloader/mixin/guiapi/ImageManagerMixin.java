package farn.recobbled_modloader.mixin.guiapi;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URL;

@Mixin(targets = "de.matthiasmann.twl.theme.ImageManager")
public class ImageManagerMixin {
	@Definition(id = "URL", type = URL.class)
	@Expression("new URL(?, ?)")
	@WrapOperation(method = "parseImages(Lde/matthiasmann/twl/utils/XMLParser;Ljava/net/URL;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private URL fix$fontURL(URL context, String spec, Operation<URL> original) {
		return this.getClass().getResource("/" + spec);
	}
}
