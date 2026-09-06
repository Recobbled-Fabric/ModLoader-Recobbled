package net.minecraft.src;

public abstract class PlayerBase {
	public EntityPlayerSP player;

	public PlayerBase(EntityPlayerSP var1) {
		this.player = var1;
	}

	public void playerInit() {
	}

	public boolean onLivingUpdate() {
		return false;
	}

	public boolean updatePlayerActionState() {
		return false;
	}

	public boolean handleKeyPress(int var1, boolean var2) {
		return false;
	}

	public boolean writeEntityToNBT(NBTTagCompound var1) {
		return false;
	}

	public boolean readEntityFromNBT(NBTTagCompound var1) {
		return false;
	}

	public boolean setEntityDead() {
		return false;
	}

	public boolean onDeath(Entity var1) {
		return false;
	}

	public boolean respawn() {
		return false;
	}

	public boolean attackEntityFrom(Entity var1, int var2) {
		return false;
	}

	public double getDistanceSq(double var1, double var3, double var5, double var7) {
		return var7;
	}

	public boolean isInWater(boolean var1) {
		return var1;
	}

	public boolean onExitGUI() {
		return false;
	}

	public boolean heal(int var1) {
		return false;
	}

	public boolean canTriggerWalking(boolean var1) {
		return var1;
	}

	public int getPlayerArmorValue(int var1) {
		return var1;
	}

	public float getCurrentPlayerStrVsBlock(Block var1, float var2) {
		return var2;
	}

	public boolean moveFlying(float var1, float var2, float var3) {
		return false;
	}

	public boolean moveEntity(double var1, double var3, double var5) {
		return false;
	}

	public EnumStatus sleepInBedAt(int var1, int var2, int var3, EnumStatus var4) {
		return var4;
	}

	public float getEntityBrightness(float var1, float var2) {
		return var2;
	}

	public boolean pushOutOfBlocks(double var1, double var3, double var5) {
		return false;
	}

	public boolean onUpdate() {
		return false;
	}

	public void afterUpdate() {
	}

	public boolean moveEntityWithHeading(float var1, float var2) {
		return false;
	}

	public boolean isOnLadder(boolean var1) {
		return var1;
	}

	public boolean isInsideOfMaterial(Material var1, boolean var2) {
		return var2;
	}

	public boolean isSneaking(boolean var1) {
		return var1;
	}

	public boolean dropCurrentItem() {
		return false;
	}

	public boolean dropPlayerItem(ItemStack var1) {
		return false;
	}

	public boolean displayGUIEditSign(TileEntitySign var1) {
		return false;
	}

	public boolean displayGUIChest(IInventory var1) {
		return false;
	}

	public boolean displayWorkbenchGUI(int var1, int var2, int var3) {
		return false;
	}

	public boolean displayGUIFurnace(TileEntityFurnace var1) {
		return false;
	}

	public boolean displayGUIDispenser(TileEntityDispenser var1) {
		return false;
	}

	public boolean sendChatMessage(String var1) {
		return false;
	}

	public String getHurtSound(String var1) {
		return null;
	}

	public Boolean canHarvestBlock(Block var1, Boolean var2) {
		return null;
	}

	public boolean fall(float var1) {
		return false;
	}

	public boolean jump() {
		return false;
	}

	public boolean damageEntity(int var1) {
		return false;
	}

	public Double getDistanceSqToEntity(Entity var1, Double var2) {
		return null;
	}

	public boolean attackTargetEntityWithCurrentItem(Entity var1) {
		return false;
	}

	public Boolean handleWaterMovement(Boolean var1) {
		return null;
	}

	public Boolean handleLavaMovement(Boolean var1) {
		return null;
	}

	public boolean dropPlayerItemWithRandomChoice(ItemStack var1, boolean var2) {
		return false;
	}

	public void beforeUpdate() {
	}

	public void beforeMoveEntity(double var1, double var3, double var5) {
	}

	public void afterMoveEntity(double var1, double var3, double var5) {
	}

	public void beforeSleepInBedAt(int var1, int var2, int var3) {
	}
}
