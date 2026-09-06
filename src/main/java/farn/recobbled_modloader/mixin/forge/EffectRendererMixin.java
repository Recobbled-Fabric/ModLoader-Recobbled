package farn.recobbled_modloader.mixin.forge;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.recobbled_modloader.impl.ForgeParticleManager;
import forge.BlockTextureParticles;
import forge.ITextureProvider;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EffectRenderer.class)
public abstract class EffectRendererMixin implements ForgeParticleManager {

    @Shadow
    private RenderEngine renderer;

    @Shadow
    public abstract void addEffect(EntityFX particle);

    // Forge Fields
    @Unique
    private final List<BlockTextureParticles> effectList = new ArrayList<>();

    /**
     * @author Eloraam
     * @reason implement Forge hooks
     */
    @Inject(method = "updateEffects", at = @At("RETURN"))
    private void forge$method_320(CallbackInfo ci) {
        for (int x = 0; x < this.effectList.size(); ++x) {
            BlockTextureParticles entry = this.effectList.get(x);

            for (int y = 0; y < entry.effects.size(); ++y) {
                EntityFX entityfx = entry.effects.get(y);

                if (entityfx.isDead) {
                    entry.effects.remove(y--);
                }
            }
        }
    }

    /**
     * @author Eloraam
     * @reason implement Forge hooks
     */
    @WrapWithCondition(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityFX;renderParticle(Lnet/minecraft/src/Tessellator;FFFFFF)V", ordinal = 0))
    private boolean forge$method_2002(EntityFX entityfx, Tessellator tessellator, float f, float f1, float f5, float f2, float f3, float f4) {
        return !(entityfx instanceof EntityDiggingFX);
    }

    /**
     * @author Eloraam
     * @reason implement Forge hooks
     */
    @Inject(method = "renderParticles", at = @At("RETURN"))
    private void forge$method_324(Entity entity, float f, CallbackInfo ci) {

        float f1 = MathHelper.cos(entity.rotationYaw * 3.1415927F / 180.0F);
        float f2 = MathHelper.sin(entity.rotationYaw * 3.1415927F / 180.0F);
        float f3 = -f2 * MathHelper.sin(entity.rotationPitch * 3.1415927F / 180.0F);
        float f4 = f1 * MathHelper.sin(entity.rotationPitch * 3.1415927F / 180.0F);
        float f5 = MathHelper.cos(entity.rotationPitch * 3.1415927F / 180.0F);

        Tessellator tessellator = Tessellator.instance;

        for (BlockTextureParticles entry : this.effectList) {
            GL11.glBindTexture(3553, this.renderer.getTexture(entry.texture));
            tessellator.startDrawingQuads();

            for (int y = 0; y < entry.effects.size(); ++y) {
                EntityFX entityfx = entry.effects.get(y);
                entityfx.renderParticle(tessellator, f, f1, f5, f2, f3, f4);
            }

            tessellator.draw();
        }
    }

    /**
     * @author Eloraam
     * @reason implement Forge hooks
     */
    @Inject(method = "clearEffects", at = @At("RETURN"))
    private void forge$method_323(World par1, CallbackInfo ci) {
        for (BlockTextureParticles entry : this.effectList) {
            entry.effects.clear();
        }

        this.effectList.clear();
    }

    @WrapOperation(method = {"addBlockDestroyEffects", "addBlockHitEffects"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EffectRenderer;addEffect(Lnet/minecraft/src/EntityFX;)V"))
    private void forge$addDigParticleEffect(EffectRenderer instance, EntityFX entityFX, Operation<Void> original, @Local Block block) {
        this.addDigParticleEffect((EntityDiggingFX)entityFX, block);
    }

    @Override
    public void addDigParticleEffect(EntityDiggingFX dig_effect, Block block) {
        boolean added = false;
        String comp;

        if (block instanceof ITextureProvider) {
            comp = ((ITextureProvider) block).getTextureFile();
        } else {
            comp = "/terrain.png";
        }

        for (BlockTextureParticles entry : this.effectList) {
            if (entry.texture.equals(comp)) {
                entry.effects.add(dig_effect);
                added = true;
            }
        }

        if (!added) {
            BlockTextureParticles entry = new BlockTextureParticles();
            entry.texture = comp;
            entry.effects.add(dig_effect);
            this.effectList.add(entry);
        }

        this.addEffect(dig_effect);
    }
}
