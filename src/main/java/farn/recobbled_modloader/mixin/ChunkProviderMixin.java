package farn.recobbled_modloader.mixin;

import net.minecraft.src.ChunkProvider;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProvider.class)
public class ChunkProviderMixin {

    @Shadow
    private World field_28066_g;

    @Shadow
    private IChunkProvider chunkProvider;

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Chunk;setChunkModified()V"))
    private void modloader$PopulateChunk(IChunkProvider paramcl, int paramInt1, int paramInt2, CallbackInfo ci) {
        ModLoader.PopulateChunk(this.chunkProvider, paramInt1, paramInt2, this.field_28066_g);
    }
}
