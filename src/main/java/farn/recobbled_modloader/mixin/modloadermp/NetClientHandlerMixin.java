package farn.recobbled_modloader.mixin.modloadermp;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixin {

    @Shadow
    protected abstract Entity getEntityByID(int i);

    @Shadow
    private WorldClient worldClient;

    @Definition(id = "obj", local = @Local(type = Entity.class, ordinal = 0))
    @Expression("obj != null")
    @Inject(method = "handleVehicleSpawn", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void modloadermp$HandleNetClientHandlerEntities(Packet23VehicleSpawn packet23vehiclespawn, CallbackInfo ci,
                                                            @Local LocalRef<Entity> objRef,
                                                            @Local(ordinal = 0) double d,
                                                            @Local(ordinal = 1) double d1,
                                                            @Local(ordinal = 2) double d2) {
        NetClientHandlerEntity netclienthandlerentity = ModLoaderMp.HandleNetClientHandlerEntities(packet23vehiclespawn.entityId);
        if (netclienthandlerentity != null) {
            try {
                objRef.set(netclienthandlerentity.entityClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(this.worldClient, d, d1, d2));
                if (netclienthandlerentity.entityHasOwner) {
                    Field field = netclienthandlerentity.entityClass.getField("owner");
                    if (!Entity.class.isAssignableFrom(field.getType())) {
                        throw new Exception(String.format("Entity's owner field must be of type Entity, but it is of type %s.", field.getType()));
                    }

                    Entity entity1 = this.getEntityByID(packet23vehiclespawn.entityId);
                    if (entity1 == null) {
                        ModLoaderMp.Log("Received spawn packet for entity with owner, but owner was not found.");
                    } else {
                        if (!field.getType().isAssignableFrom(entity1.getClass())) {
                            throw new Exception(String.format("Tried to assign an entity of type %s to entity owner, which is of type %s.", entity1.getClass(), field.getType()));
                        }

                        field.set(objRef.get(), entity1);
                    }
                }
            } catch (Exception exception) {
                ModLoader.getLogger().throwing("NetClientHandler", "handleVehicleSpawn", exception);
                ModLoader.ThrowException(String.format("Error initializing entity of type %s.", packet23vehiclespawn.entityId), exception);
                ci.cancel();
            }
        }
    }

    @Inject(method="func_20087_a", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Container;windowId:I", opcode = Opcodes.PUTFIELD))
    public void modloadermp$onvanillasetwindow(Packet100OpenWindow par1, CallbackInfo ci, @Share(value="modloadermp_vanillaWindow")LocalBooleanRef ref) {
        ref.set(true);
    }

    @Inject(method="func_20087_a", at = @At("TAIL"))
    public void modloadermp$customWindow(Packet100OpenWindow par1, CallbackInfo ci, @Share(value="modloadermp_vanillaWindow")LocalBooleanRef ref) {
        if(!ref.get()) {
            ModLoaderMp.HandleGUI(par1);
        }
    }
}
