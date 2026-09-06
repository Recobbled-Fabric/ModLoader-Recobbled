package forge;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import farn.recobbled_modloader.mixin.forge.TessellatorAccessor;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class ForgeHooksClient {
	static HashMap<List<Integer>, Tessellator> tessellators = new HashMap<>();
	static HashMap<String, Integer> textures = new HashMap<>();
	static boolean inWorld = false;
	static HashSet<List<Integer>> renderTextureTest = new HashSet<>();
	static ArrayList<List<Integer>> renderTextureList = new ArrayList<>();
	static int renderPass = -1;

	public static Tessellator defaultTessellator = null;

	public static boolean canRenderInPass(Block var0, int var1) {
		return var1 == var0.getRenderBlockPass();
	}

	protected static void bindTessellator(int var0, int var1) {
		List<Integer> var2 = Arrays.asList(var0, var1);
		Tessellator var3;
		if(!tessellators.containsKey(var2)) {
			try {
				var3 = Tessellator.class.getDeclaredConstructor(int.class).newInstance(0);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
			         NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
			tessellators.put(var2, var3);
		} else {
			var3 = tessellators.get(var2);
		}

		if(inWorld && !renderTextureTest.contains(var2)) {
			renderTextureTest.add(var2);
			renderTextureList.add(var2);
			var3.startDrawingQuads();
			var3.setTranslationD(defaultTessellator.xOffset, defaultTessellator.yOffset, defaultTessellator.zOffset);
		}

		Tessellator.instance = var3;
	}

	protected static void bindTexture(String var0, int var1) {
		int var2;
		if(!textures.containsKey(var0)) {
			var2 = ModLoader.getMinecraftInstance().renderEngine.getTexture(var0);
			textures.put(var0, var2);
		} else {
			var2 = textures.get(var0);
		}

		if(!inWorld) {
			Tessellator.instance = defaultTessellator;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var2);
		} else {
			bindTessellator(var2, var1);
		}
	}

	protected static void unbindTexture() {
		Tessellator.instance = defaultTessellator;
		if(!inWorld) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
		}
	}

	public static void beforeRenderPass(int var0) {
		renderPass = var0;
		Tessellator.instance = defaultTessellator;
		TessellatorAccessor.setRenderingWorldRenderer(true);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
		renderTextureTest.clear();
		renderTextureList.clear();
		inWorld = true;
	}

	public static void afterRenderPass(int var0) {
		renderPass = -1;
		inWorld = false;

        for (List<Integer> integers : renderTextureList) {
            Integer[] var3 = integers.toArray(integers.toArray(new Integer[0]));
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, var3[0]);
            Tessellator var4 = tessellators.get(integers);
            var4.draw();
        }

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
		Tessellator.instance = defaultTessellator;
		TessellatorAccessor.setRenderingWorldRenderer(false);
	}

	public static void beforeBlockRender(Block block, RenderBlocks var1) {
		if(block instanceof ITextureProvider && var1.overrideBlockTexture == -1) {
			ITextureProvider var2 = (ITextureProvider)block;
			bindTexture(var2.getTextureFile(), 0);
		}

	}

	public static void afterBlockRender(Block block, RenderBlocks var1) {
		if(block instanceof ITextureProvider && var1.overrideBlockTexture == -1) {
			unbindTexture();
		}

	}

	public static void overrideTexture(Object var0) {
		if(var0 instanceof ITextureProvider) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture(((ITextureProvider)((ITextureProvider)var0)).getTextureFile()));
		}

	}
}
