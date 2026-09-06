package farn.recobbled_modloader.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public interface PlayerEntityExtension {
	List<PlayerBase> getPlayerBases();

	void superUpdatePlayerActionState();

	void superOnLivingUpdate();

	void superOnUpdate();

	EnumStatus superSleepInBedAt(int i, int j, int k);

	void superMoveEntity(double d, double d1, double d2);

	void setMoveForward(float f);

	void setMoveStrafing(float f);

	void setIsJumping(boolean flag);

	void doFall(float fallDist);

	float getFallDistance();

	boolean getSleeping();

	boolean getJumping();

	void doJump();

	Random getRandom();

	void setFallDistance(float f);

	void setYSize(float f);

	void setActionState(float newMoveStrafing, float newMoveForward, boolean newIsJumping);

	boolean superIsInsideOfMaterial(Material material);

	float superGetEntityBrightness(float f);

	Minecraft getMc();

	void superMoveFlying(float f, float f1, float f2);

	String superGetHurtSound();

	float superGetCurrentPlayerStrVsBlock(Block block);

	boolean superCanHarvestBlock(Block block);

	void superFall(float f);

	void superJump();

	void superDamageEntity(int i);

	double superGetDistanceSqToEntity(Entity entity);

	void superAttackTargetEntityWithCurrentItem(Entity entity);

	boolean superHandleWaterMovement();

	boolean superHandleLavaMovement();

	void superDropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag);
}
