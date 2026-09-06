package net.minecraft.src;

import farn.recobbled_modloader.impl.PlayerEntityExtension;

import java.util.ArrayList;
import java.util.List;

public class PlayerAPI {
	public static List<Class<? extends PlayerBase>> playerBaseClasses = new ArrayList<>();

	public static void RegisterPlayerBase(Class<? extends PlayerBase> var0) {
		playerBaseClasses.add(var0);
	}

	public static PlayerBase getPlayerBase(EntityPlayerSP var0, Class<? extends PlayerBase>var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(var1.isInstance(((PlayerEntityExtension)var0).getPlayerBases().get(var2))) {
				return ((PlayerEntityExtension)var0).getPlayerBases().get(var2);
			}
		}

		return null;
	}

	public static List<PlayerBase> playerInit(EntityPlayerSP var0) {
		ArrayList<PlayerBase> var1 = new ArrayList<>();

        for (Class<? extends PlayerBase> playerBaseClass : playerBaseClasses) {
            try {
                var1.add(playerBaseClass.getDeclaredConstructor(new Class[]{EntityPlayerSP.class}).newInstance(var0));
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

		return var1;
	}

	public static boolean onLivingUpdate(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).onLivingUpdate()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean respawn(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).respawn()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean moveFlying(EntityPlayerSP var0, float var1, float var2, float var3) {
		boolean var4 = false;

		for(int var5 = 0; var5 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var5) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var5).moveFlying(var1, var2, var3)) {
				var4 = true;
			}
		}

		return var4;
	}

	public static boolean updatePlayerActionState(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).updatePlayerActionState()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean handleKeyPress(EntityPlayerSP var0, int var1, boolean var2) {
		boolean var3 = false;

		for(int var4 = 0; var4 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var4) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var4).handleKeyPress(var1, var2)) {
				var3 = true;
			}
		}

		return var3;
	}

	public static boolean writeEntityToNBT(EntityPlayerSP var0, NBTTagCompound var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).writeEntityToNBT(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean readEntityFromNBT(EntityPlayerSP var0, NBTTagCompound var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).readEntityFromNBT(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean onExitGUI(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).onExitGUI()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean setEntityDead(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).setEntityDead()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean onDeath(EntityPlayerSP var0, Entity var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).onDeath(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean attackEntityFrom(EntityPlayerSP var0, Entity var1, int var2) {
		boolean var3 = false;

		for(int var4 = 0; var4 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var4) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var4).attackEntityFrom(var1, var2)) {
				var3 = true;
			}
		}

		return var3;
	}

	public static double getDistanceSq(EntityPlayerSP var0, double var1, double var3, double var5, double var7) {
		for(int var9 = 0; var9 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var9) {
			var7 = ((PlayerEntityExtension)var0).getPlayerBases().get(var9).getDistanceSq(var1, var3, var5, var7);
		}

		return var7;
	}

	public static boolean isInWater(EntityPlayerSP var0, boolean var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).isInWater(var1);
		}

		return var1;
	}

	public static boolean canTriggerWalking(EntityPlayerSP var0, boolean var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).canTriggerWalking(var1);
		}

		return var1;
	}

	public static boolean heal(EntityPlayerSP var0, int var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).heal(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static int getPlayerArmorValue(EntityPlayerSP var0, int var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).getPlayerArmorValue(var1);
		}

		return var1;
	}

	public static float getCurrentPlayerStrVsBlock(EntityPlayerSP var0, Block var1, float var2) {
		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			var2 = ((PlayerEntityExtension)var0).getPlayerBases().get(var3).getCurrentPlayerStrVsBlock(var1, var2);
		}

		return var2;
	}

	public static boolean moveEntity(EntityPlayerSP var0, double var1, double var3, double var5) {
		boolean var7 = false;

		for(int var8 = 0; var8 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var8) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var8).moveEntity(var1, var3, var5)) {
				var7 = true;
			}
		}

		return var7;
	}

	public static EnumStatus sleepInBedAt(EntityPlayerSP var0, int var1, int var2, int var3) {
		EnumStatus var4 = null;

		for(int var5 = 0; var5 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var5) {
			var4 = ((PlayerEntityExtension)var0).getPlayerBases().get(var5).sleepInBedAt(var1, var2, var3, var4);
		}

		return var4;
	}

	public static float getEntityBrightness(EntityPlayerSP var0, float var1, float var2) {
		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var3).getEntityBrightness(var1, var2);
		}

		return var1;
	}

	public static boolean pushOutOfBlocks(EntityPlayerSP var0, double var1, double var3, double var5) {
		boolean var7 = false;

		for(int var8 = 0; var8 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var8) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var8).pushOutOfBlocks(var1, var3, var5)) {
				var7 = true;
			}
		}

		return var7;
	}

	public static boolean onUpdate(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).onUpdate()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static void afterUpdate(EntityPlayerSP var0) {
		for(int var1 = 0; var1 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var1) {
			((PlayerEntityExtension)var0).getPlayerBases().get(var1).afterUpdate();
		}

	}

	public static boolean moveEntityWithHeading(EntityPlayerSP var0, float var1, float var2) {
		boolean var3 = false;

		for(int var4 = 0; var4 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var4) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var4).moveEntityWithHeading(var1, var2)) {
				var3 = true;
			}
		}

		return var3;
	}

	public static boolean isOnLadder(EntityPlayerSP var0, boolean var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).isOnLadder(var1);
		}

		return var1;
	}

	public static boolean isInsideOfMaterial(EntityPlayerSP var0, Material var1, boolean var2) {
		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			var2 = ((PlayerEntityExtension)var0).getPlayerBases().get(var3).isInsideOfMaterial(var1, var2);
		}

		return var2;
	}

	public static boolean isSneaking(EntityPlayerSP var0, boolean var1) {
		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			var1 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).isSneaking(var1);
		}

		return var1;
	}

	public static boolean dropCurrentItem(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).dropCurrentItem()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean dropPlayerItem(EntityPlayerSP var0, ItemStack var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).dropPlayerItem(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean displayGUIEditSign(EntityPlayerSP var0, TileEntitySign var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).displayGUIEditSign(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean displayGUIChest(EntityPlayerSP var0, IInventory var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).displayGUIChest(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean displayWorkbenchGUI(EntityPlayerSP var0, int var1, int var2, int var3) {
		boolean var4 = false;

		for(int var5 = 0; var5 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var5) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var5).displayWorkbenchGUI(var1, var2, var3)) {
				var4 = true;
			}
		}

		return var4;
	}

	public static boolean displayGUIFurnace(EntityPlayerSP var0, TileEntityFurnace var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).displayGUIFurnace(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean displayGUIDispenser(EntityPlayerSP var0, TileEntityDispenser var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).displayGUIDispenser(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean sendChatMessage(EntityPlayerSP var0, String var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).sendChatMessage(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static String getHurtSound(EntityPlayerSP var0) {
		String var1 = null;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			String var3 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).getHurtSound(var1);
			if(var3 != null) {
				var1 = var3;
			}
		}

		return var1;
	}

	public static Boolean canHarvestBlock(EntityPlayerSP var0, Block var1) {
		Boolean var2 = null;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			Boolean var4 = ((PlayerEntityExtension)var0).getPlayerBases().get(var3).canHarvestBlock(var1, var2);
			if(var4 != null) {
				var2 = var4;
			}
		}

		return var2;
	}

	public static boolean fall(EntityPlayerSP var0, float var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).fall(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static boolean jump(EntityPlayerSP var0) {
		boolean var1 = false;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var2).jump()) {
				var1 = true;
			}
		}

		return var1;
	}

	public static boolean damageEntity(EntityPlayerSP var0, int var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).damageEntity(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static Double getDistanceSqToEntity(EntityPlayerSP var0, Entity var1) {
		Double var2 = null;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			Double var4 = ((PlayerEntityExtension)var0).getPlayerBases().get(var3).getDistanceSqToEntity(var1, var2);
			if(var4 != null) {
				var2 = var4;
			}
		}

		return var2;
	}

	public static boolean attackTargetEntityWithCurrentItem(EntityPlayerSP var0, Entity var1) {
		boolean var2 = false;

		for(int var3 = 0; var3 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var3) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var3).attackTargetEntityWithCurrentItem(var1)) {
				var2 = true;
			}
		}

		return var2;
	}

	public static Boolean handleWaterMovement(EntityPlayerSP var0) {
		Boolean var1 = null;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			Boolean var3 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).handleWaterMovement(var1);
			if(var3 != null) {
				var1 = var3;
			}
		}

		return var1;
	}

	public static Boolean handleLavaMovement(EntityPlayerSP var0) {
		Boolean var1 = null;

		for(int var2 = 0; var2 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var2) {
			Boolean var3 = ((PlayerEntityExtension)var0).getPlayerBases().get(var2).handleLavaMovement(var1);
			if(var3 != null) {
				var1 = var3;
			}
		}

		return var1;
	}

	public static boolean dropPlayerItemWithRandomChoice(EntityPlayerSP var0, ItemStack var1, boolean var2) {
		boolean var3 = false;

		for(int var4 = 0; var4 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var4) {
			if(((PlayerEntityExtension)var0).getPlayerBases().get(var4).dropPlayerItemWithRandomChoice(var1, var2)) {
				var3 = true;
			}
		}

		return var3;
	}

	public static void beforeUpdate(EntityPlayerSP var0) {
		for(int var1 = 0; var1 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var1) {
			((PlayerEntityExtension)var0).getPlayerBases().get(var1).beforeUpdate();
		}

	}

	public static void beforeMoveEntity(EntityPlayerSP var0, double var1, double var3, double var5) {
		for(int var7 = 0; var7 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var7) {
			((PlayerEntityExtension)var0).getPlayerBases().get(var7).beforeMoveEntity(var1, var3, var5);
		}

	}

	public static void afterMoveEntity(EntityPlayerSP var0, double var1, double var3, double var5) {
		for(int var7 = 0; var7 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var7) {
			((PlayerEntityExtension)var0).getPlayerBases().get(var7).afterMoveEntity(var1, var3, var5);
		}

	}

	public static void beforeSleepInBedAt(EntityPlayerSP var0, int var1, int var2, int var3) {
		for(int var4 = 0; var4 < ((PlayerEntityExtension)var0).getPlayerBases().size(); ++var4) {
			((PlayerEntityExtension)var0).getPlayerBases().get(var4).beforeSleepInBedAt(var1, var2, var3);
		}

	}
}
