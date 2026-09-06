package farn.recobbled_modloader.mixin.playerapi;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.recobbled_modloader.impl.PlayerEntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EntityPlayerSP.class)
public abstract class ClientPlayerEntityMixin extends EntityPlayer implements PlayerEntityExtension {
	@Shadow
	protected Minecraft mc;

	public ClientPlayerEntityMixin(World arg) {
		super(arg);
	}

	public List<PlayerBase> playerBases = new ArrayList<>();

	@Override
	public List<PlayerBase> getPlayerBases() {
		return playerBases;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void playerapi$ctr(Minecraft minecraft, World world, Session session, int dimensionId, CallbackInfo ci) {
		this.playerBases = PlayerAPI.playerInit((EntityPlayerSP) (Object) this);
	}

	@Override
	public boolean attackEntityFrom(Entity entity, int i, int protection) {
		return !PlayerAPI.attackEntityFrom((EntityPlayerSP) (Object) this, entity, i) && super.attackEntityFrom(entity, i, protection);
	}

	@Override
	public void onDeath(Entity entity) {
		if (!PlayerAPI.onDeath((EntityPlayerSP) (Object) this, entity)) {
			super.onDeath(entity);
		}
	}

	@WrapMethod(method = "updatePlayerActionState")
	private void playerapi$tickLiving(Operation<Void> original) {
		if (!PlayerAPI.updatePlayerActionState((EntityPlayerSP) (Object) this)) {
			original.call();
		}
	}

	@Override
	public void superUpdatePlayerActionState() {
		super.onLivingUpdate();
	}

	@WrapMethod(method = "onLivingUpdate")
	private void playerapi$tickMovement(Operation<Void> original) {
		if (!PlayerAPI.onLivingUpdate((EntityPlayerSP) (Object)this)) {
			original.call();
		}
	}

	@Override
	public void superOnLivingUpdate() {
		super.onLivingUpdate();
	}

	@Override
	public void superOnUpdate() {
		super.onUpdate();
	}

	@Override
	public void moveFlying(float f, float f1, float f2) {
		if (!PlayerAPI.moveFlying((EntityPlayerSP) (Object)this, f, f1, f2)) {
			super.moveFlying(f, f1, f2);
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return PlayerAPI.canTriggerWalking((EntityPlayerSP) (Object)this, true);
	}

	@WrapMethod(method = "handleKeyPress")
	private void playerapi$updateKey(int key, boolean state, Operation<Void> original) {
		if (!PlayerAPI.handleKeyPress((EntityPlayerSP) (Object)this, key, state)) {
			original.call(key, state);
		}
	}

	@WrapMethod(method = "writeEntityToNBT")
	private void playerapi$writeNbt(NBTTagCompound par1, Operation<Void> original) {
		if (!PlayerAPI.writeEntityToNBT((EntityPlayerSP) (Object)this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "readEntityFromNBT")
	private void playerapi$readNbt(NBTTagCompound par1, Operation<Void> original) {
		if (!PlayerAPI.readEntityFromNBT((EntityPlayerSP) (Object)this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "closeScreen")
	private void playerapi$closeHandledScreen(Operation<Void> original) {
		if (!PlayerAPI.onExitGUI((EntityPlayerSP) (Object)this)) {
			original.call();
		}
	}

	@WrapMethod(method = "displayGUIEditSign")
	private void playerapi$openEditSignScreen(TileEntitySign par1, Operation<Void> original) {
		if (!PlayerAPI.displayGUIEditSign((EntityPlayerSP) (Object) this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "displayGUIChest")
	private void playerapi$openChestScreen(IInventory par1, Operation<Void> original) {
		if (!PlayerAPI.displayGUIChest((EntityPlayerSP) (Object)this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "displayWorkbenchGUI")
	private void playerapi$openCraftingScreen(int i, int j, int k, Operation<Void> original) {
		if (!PlayerAPI.displayWorkbenchGUI((EntityPlayerSP) (Object)this, i, j, k)) {
			original.call(i, j, k);
		}
	}

	@WrapMethod(method = "displayGUIFurnace")
	private void playerapi$openFurnaceScreen(TileEntityFurnace par1, Operation<Void> original) {
		if (!PlayerAPI.displayGUIFurnace((EntityPlayerSP) (Object)this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "displayGUIDispenser")
	private void playerapi$openDispenserScreen(TileEntityDispenser par1, Operation<Void> original) {
		if (!PlayerAPI.displayGUIDispenser((EntityPlayerSP) (Object)this, par1)) {
			original.call(par1);
		}
	}

	@WrapMethod(method = "getPlayerArmorValue")
	private int playerapi$getTotalArmorDurability(Operation<Integer> original) {
		return PlayerAPI.getPlayerArmorValue((EntityPlayerSP) (Object)this, original.call());
	}

	@Override
	public void setEntityDead() {
		if (!PlayerAPI.setEntityDead((EntityPlayerSP) (Object)this)) {
			super.setEntityDead();
		}
	}

	@Override
	public double getDistanceSq(double d, double d1, double d2) {
		return PlayerAPI.getDistanceSq((EntityPlayerSP) (Object)this, d, d1, d2, super.getDistanceSq(d, d1, d2));
	}

	@Override
	public boolean isInWater() {
		return PlayerAPI.isInWater((EntityPlayerSP) (Object)this, this.inWater);
	}

	@WrapMethod(method = "isSneaking")
	private boolean playerapi$isSneaking(Operation<Boolean> original) {
		return PlayerAPI.isSneaking((EntityPlayerSP) (Object)this, original.call());
	}

	@Override
	public float getCurrentPlayerStrVsBlock(Block block) {
		float f = super.getCurrentPlayerStrVsBlock(block);

		return PlayerAPI.getCurrentPlayerStrVsBlock((EntityPlayerSP) (Object)this, block, f);
	}

	@Override
	public void heal(int i) {
		if (!PlayerAPI.heal((EntityPlayerSP) (Object)this, i)) {
			super.heal(i);
		}
	}

	@WrapMethod(method = "respawnPlayer")
	private void playerapi$respawn(Operation<Void> original) {
		if (!PlayerAPI.respawn((EntityPlayerSP) (Object)this)) {
			original.call();
		}
	}

	@WrapMethod(method = "pushOutOfBlocks")
	private boolean playerapi$pushOutOfBlock(double d, double d1, double d2, Operation<Boolean> original) {
		if (PlayerAPI.pushOutOfBlocks((EntityPlayerSP) (Object)this, d, d1, d2)) {
			return false;
		} else {
			return original.call(d, d1, d2);
		}
	}

	@Override
	public EnumStatus superSleepInBedAt(int i, int j, int k) {
		return super.sleepInBedAt(i, j, k);
	}

	@Override
	public Minecraft getMc() {
		return this.mc;
	}

	@Override
	public void superMoveEntity(double d, double d1, double d2) {
		super.moveEntity(d, d1, d2);
	}

	@Override
	public void setMoveForward(float f) {
		this.moveForward = f;
	}

	@Override
	public void setMoveStrafing(float f) {
		this.moveStrafing = f;
	}

	@Override
	public void setIsJumping(boolean flag) {
		this.isJumping = flag;
	}

	@Override
	public float getEntityBrightness(float f) {
		return PlayerAPI.getEntityBrightness((EntityPlayerSP) (Object)this, f, super.getEntityBrightness(f));
	}

	@Override
	public void onUpdate() {
		PlayerAPI.beforeUpdate((EntityPlayerSP) (Object) this);
		if (!PlayerAPI.onUpdate((EntityPlayerSP) (Object)this)) {
			super.onUpdate();
		}

		PlayerAPI.afterUpdate((EntityPlayerSP) (Object)this);
	}

	@Override
	public void superMoveFlying(float f, float f1, float f2) {
		super.moveFlying(f, f1, f2);
	}

	@WrapMethod(method = "moveEntity")
	private void playerapi$move(double d, double d1, double d2, Operation<Void> original) {
		PlayerAPI.beforeMoveEntity((EntityPlayerSP) (Object) this, d, d1, d2);
		if (!PlayerAPI.moveEntity((EntityPlayerSP) (Object) this, d, d1, d2)) {
			original.call(d, d1, d2);
		}

		PlayerAPI.afterMoveEntity((EntityPlayerSP) (Object)  this, d, d1, d2);
	}

	@Override
	public EnumStatus sleepInBedAt(int i, int j, int k) {
		PlayerAPI.beforeSleepInBedAt((EntityPlayerSP) (Object)  this, i, j, k);
		EnumStatus enumstatus = PlayerAPI.sleepInBedAt((EntityPlayerSP) (Object)this, i, j, k);
		return enumstatus == null ? super.sleepInBedAt(i, j, k) : enumstatus;
	}

	@Override
	public void doFall(float fallDist) {
		super.fall(fallDist);
	}

	@Override
	public float getFallDistance() {
		return this.fallDistance;
	}

	@Override
	public boolean getSleeping() {
		return this.sleeping;
	}

	@Override
	public boolean getJumping() {
		return this.isJumping;
	}

	@Override
	public void doJump() {
		this.jump();
	}

	@Override
	public Random getRandom() {
		return this.rand;
	}

	@Override
	public void setFallDistance(float f) {
		this.fallDistance = f;
	}

	@Override
	public void setYSize(float f) {
		this.ySize = f;
	}

	@Override
	public void moveEntityWithHeading(float f, float f1) {
		if (!PlayerAPI.moveEntityWithHeading((EntityPlayerSP) (Object)this, f, f1)) {
			super.moveEntityWithHeading(f, f1);
		}
	}

	@Override
	public boolean isOnLadder() {
		return PlayerAPI.isOnLadder((EntityPlayerSP) (Object)this, super.isOnLadder());
	}

	@Override
	public void setActionState(float newMoveStrafing, float newMoveForward, boolean newIsJumping) {
		this.moveStrafing = newMoveStrafing;
		this.moveForward = newMoveForward;
		this.isJumping = newIsJumping;
	}

	@Override
	public boolean isInsideOfMaterial(Material material) {
		return PlayerAPI.isInsideOfMaterial((EntityPlayerSP) (Object)this, material, super.isInsideOfMaterial(material));
	}

	@Override
	public void dropCurrentItem() {
		if (!PlayerAPI.dropCurrentItem((EntityPlayerSP) (Object)this)) {
			super.dropCurrentItem();
		}
	}

	@Override
	public void dropPlayerItem(ItemStack itemstack) {
		if (!PlayerAPI.dropPlayerItem((EntityPlayerSP) (Object)this, itemstack)) {
			super.dropPlayerItem(itemstack);
		}
	}

	@Override
	public boolean superIsInsideOfMaterial(Material material) {
		return super.isInsideOfMaterial(material);
	}

	@Override
	public float superGetEntityBrightness(float f) {
		return super.getEntityBrightness(f);
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"))
	private void playerapi$sendChatMessage(String par1, CallbackInfo ci) {
		PlayerAPI.sendChatMessage((EntityPlayerSP) (Object) this, par1);
	}

	@Override
	protected String getHurtSound() {
		String result = PlayerAPI.getHurtSound((EntityPlayerSP) (Object) this);
		return result != null ? result : super.getHurtSound();
	}

	@Override
	public String superGetHurtSound() {
		return super.getHurtSound();
	}

	@Override
	public float superGetCurrentPlayerStrVsBlock(Block block) {
		return super.getCurrentPlayerStrVsBlock(block);
	}

	@Override
	public boolean canHarvestBlock(Block block) {
		Boolean result = PlayerAPI.canHarvestBlock((EntityPlayerSP) (Object) this, block);
		return result != null ? result : super.canHarvestBlock(block);
	}

	@Override
	public boolean superCanHarvestBlock(Block block) {
		return super.canHarvestBlock(block);
	}

	@Override
	protected void fall(float f) {
		if (!PlayerAPI.fall((EntityPlayerSP) (Object) this, f)) {
			super.fall(f);
		}

	}

	@Override
	public void superFall(float f) {
		super.fall(f);
	}

	@Override
	protected void jump() {
		if (!PlayerAPI.jump((EntityPlayerSP) (Object) this)) {
			super.jump();
		}

	}

	@Override
	public void superJump() {
		super.jump();
	}

	@Override
	protected void damageEntity(int i, int protection) {
		if (!PlayerAPI.damageEntity((EntityPlayerSP) (Object) this, i)) {
			super.damageEntity(i, protection);
		}

	}

	@Override
	public void superDamageEntity(int i) {
		super.damageEntity(i, 0);
	}

	public double getDistanceSqToEntity(Entity entity) {
		Double result = PlayerAPI.getDistanceSqToEntity((EntityPlayerSP) (Object) this, entity);
		return result != null ? result : super.getDistanceSqToEntity(entity);
	}

	@Override
	public double superGetDistanceSqToEntity(Entity entity) {
		return super.getDistanceSqToEntity(entity);
	}

	@Override
	public void attackTargetEntityWithCurrentItem(Entity entity) {
		if (!PlayerAPI.attackTargetEntityWithCurrentItem((EntityPlayerSP) (Object) this, entity)) {
			super.attackTargetEntityWithCurrentItem(entity);
		}

	}

	@Override
	public void superAttackTargetEntityWithCurrentItem(Entity entity) {
		super.attackTargetEntityWithCurrentItem(entity);
	}

	@Override
	public boolean handleWaterMovement() {
		Boolean result = PlayerAPI.handleWaterMovement((EntityPlayerSP) (Object) this);
		return result != null ? result : super.handleWaterMovement();
	}

	@Override
	public boolean superHandleWaterMovement() {
		return super.handleWaterMovement();
	}

	@Override
	public boolean handleLavaMovement() {
		Boolean result = PlayerAPI.handleLavaMovement((EntityPlayerSP) (Object) this);
		return result != null ? result : super.handleLavaMovement();
	}

	@Override
	public boolean superHandleLavaMovement() {
		return super.handleLavaMovement();
	}

	@Override
	public void dropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag) {
		if (!PlayerAPI.dropPlayerItemWithRandomChoice((EntityPlayerSP) (Object) this, itemstack, flag)) {
			super.dropPlayerItemWithRandomChoice(itemstack, flag);
		}

	}

	@Override
	public void superDropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag) {
		super.dropPlayerItemWithRandomChoice(itemstack, flag);
	}
}
