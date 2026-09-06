package farn.recobbled_modloader;

import net.minecraft.src.Block;
import net.minecraft.src.EntityDiggingFX;

public interface ForgeParticleManager {

    default void addDigParticleEffect(EntityDiggingFX dig_effect, Block block) {

    }

}
