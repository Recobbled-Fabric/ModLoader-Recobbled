package net.minecraft.src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "JavaReflectionMemberAccess"})
public class ModLoaderMp {
	public static final String NAME = "ModLoaderMP";
	public static final String VERSION = "Beta 1.7.3 unofficial";
	private static boolean hasInit = false;
	private static boolean packet230Received = false;
	private static Map<Integer, NetClientHandlerEntity> netClientHandlerEntityMap = new HashMap<>();
	private static Map<Integer, BaseModMp> guiModMap = new HashMap<>();

	public static void Init() {
		if(!hasInit) {
			init();
		}

	}

	public static void HandleAllPackets(Packet230ModLoader packet230modloader) {
		if(!hasInit) {
			init();
		}

		packet230Received = true;
		if(packet230modloader.modId == NAME.hashCode()) {
			switch(packet230modloader.packetType) {
			case 0:
				handleModCheck(packet230modloader);
				break;
			case 1:
				handleTileEntityPacket(packet230modloader);
			}
		} else if(packet230modloader.modId == "Spawn".hashCode()) {
			NetClientHandlerEntity i = HandleNetClientHandlerEntities(packet230modloader.packetType);
			if(i != null && ISpawnable.class.isAssignableFrom(i.entityClass)) {
				try {
					Entity basemod = i.entityClass.getConstructor(new Class[]{World.class}).newInstance(ModLoader.getMinecraftInstance().theWorld);
					((ISpawnable)basemod).spawn(packet230modloader);
					((WorldClient)ModLoader.getMinecraftInstance().theWorld).func_712_a(basemod.entityId, basemod);
				} catch (Exception exception4) {
					ModLoader.getLogger().throwing("ModLoader", "handleCustomSpawn", exception4);
					ModLoader.ThrowException(String.format("Error initializing entity of type %s.", packet230modloader.packetType), exception4);
                }
			}
		} else {
			for(int i5 = 0; i5 < ModLoader.getLoadedMods().size(); ++i5) {
				BaseMod baseMod6 = ModLoader.getLoadedMods().get(i5);
				if(baseMod6 instanceof BaseModMp) {
					BaseModMp basemodmp = (BaseModMp)baseMod6;
					if(basemodmp.getId() == packet230modloader.modId) {
						basemodmp.HandlePacket(packet230modloader);
						break;
					}
				}
			}
		}

	}

	public static NetClientHandlerEntity HandleNetClientHandlerEntities(int i) {
		if(!hasInit) {
			init();
		}

		return netClientHandlerEntityMap.getOrDefault(i, null);
	}

	public static void SendPacket(BaseModMp basemodmp, Packet230ModLoader packet230modloader) {
		if(!hasInit) {
			init();
		}

		if(basemodmp == null) {
			IllegalArgumentException illegalargumentexception = new IllegalArgumentException("baseModMp cannot be null.");
			ModLoader.getLogger().throwing("ModLoaderMp", "SendPacket", illegalargumentexception);
			ModLoader.ThrowException("baseModMp cannot be null.", illegalargumentexception);
		} else {
			packet230modloader.modId = basemodmp.getId();
			sendPacket(packet230modloader);
		}

	}

	public static void RegisterGUI(BaseModMp basemodmp, int i) {
		if(!hasInit) {
			init();
		}

		if(guiModMap.containsKey(i)) {
			Log("RegisterGUI error: inventoryType already registered.");
		} else {
			guiModMap.put(i, basemodmp);
		}

	}

	public static void HandleGUI(Packet100OpenWindow packet100openwindow) {
		if(!hasInit) {
			init();
		}

		BaseModMp basemodmp = guiModMap.get(packet100openwindow.inventoryType);
		GuiScreen guiscreen = basemodmp.HandleGUI(packet100openwindow.inventoryType);
		if(guiscreen != null) {
			ModLoader.OpenGUI(ModLoader.getMinecraftInstance().thePlayer, guiscreen);
			ModLoader.getMinecraftInstance().thePlayer.craftingInventory.windowId = packet100openwindow.windowId;
		}

	}

	public static void RegisterNetClientHandlerEntity(Class<? extends Entity> class1, int i) {
		RegisterNetClientHandlerEntity(class1, false, i);
	}

	public static void RegisterNetClientHandlerEntity(Class<? extends Entity> class1, boolean flag, int i) {
		if(!hasInit) {
			init();
		}

		if(i > 255) {
			Log("RegisterNetClientHandlerEntity error: entityId cannot be greater than 255.");
		} else if(netClientHandlerEntityMap.containsKey(i)) {
			Log("RegisterNetClientHandlerEntity error: entityId already registered.");
		} else {
			if(i > 127) {
				i -= 256;
			}

			netClientHandlerEntityMap.put(i, new NetClientHandlerEntity(class1, flag));
		}

	}

	public static void SendKey(BaseModMp basemodmp, int i) {
		if(!hasInit) {
			init();
		}

		if(basemodmp == null) {
			IllegalArgumentException packet230modloader = new IllegalArgumentException("baseModMp cannot be null.");
			ModLoader.getLogger().throwing(NAME, "SendKey", packet230modloader);
			ModLoader.ThrowException("baseModMp cannot be null.", packet230modloader);
		} else {
			Packet230ModLoader packet230modloader1 = new Packet230ModLoader();
			packet230modloader1.modId = NAME.hashCode();
			packet230modloader1.packetType = 1;
			packet230modloader1.dataInt = new int[]{basemodmp.getId(), i};
			sendPacket(packet230modloader1);
		}

	}

	public static void Log(String s) {
		System.out.println(s);
		ModLoader.getLogger().fine(s);
	}

	private static void init() {
		hasInit = true;

		try {
			Method securityexception;
			try {
				securityexception = Packet.class.getDeclaredMethod("a", Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Class.class);
			} catch (NoSuchMethodException noSuchMethodException2) {
				securityexception = Packet.class.getDeclaredMethod("addIdClassMapping", Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Class.class);
			}

			securityexception.setAccessible(true);
			securityexception.invoke((Object)null, 230, true, true, Packet230ModLoader.class);
		} catch (IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException |
                 IllegalArgumentException illegalAccessException3) {
			ModLoader.getLogger().throwing("ModLoaderMp", "init", illegalAccessException3);
			ModLoader.ThrowException("An impossible error has occurred!", illegalAccessException3);
		}

        Log("ModLoaderMP Beta 1.7.3 unofficial Initialized");
	}

	private static void handleModCheck(Packet230ModLoader packet230modloader) {
		Packet230ModLoader packet230modloader1 = new Packet230ModLoader();
		packet230modloader1.modId = NAME.hashCode();
		packet230modloader1.packetType = 0;
		packet230modloader1.dataString = new String[ModLoader.getLoadedMods().size()];

		for(int i = 0; i < ModLoader.getLoadedMods().size(); ++i) {
			packet230modloader1.dataString[i] = ModLoader.getLoadedMods().get(i).toString();
		}

		sendPacket(packet230modloader1);
	}

	private static void handleTileEntityPacket(Packet230ModLoader packet230modloader) {
		if(packet230modloader.dataInt != null && packet230modloader.dataInt.length >= 5) {
			int i = packet230modloader.dataInt[0];
			int j = packet230modloader.dataInt[1];
			int k = packet230modloader.dataInt[2];
			int l = packet230modloader.dataInt[3];
			int i1 = packet230modloader.dataInt[4];
			int[] ai = new int[packet230modloader.dataInt.length - 5];
			System.arraycopy(packet230modloader.dataInt, 5, ai, 0, packet230modloader.dataInt.length - 5);
			float[] af = packet230modloader.dataFloat;
			String[] as = packet230modloader.dataString;

			for(int j1 = 0; j1 < ModLoader.getLoadedMods().size(); ++j1) {
				BaseMod basemod = ModLoader.getLoadedMods().get(j1);
				if(basemod instanceof BaseModMp) {
					BaseModMp basemodmp = (BaseModMp)basemod;
					if(basemodmp.getId() == i) {
						basemodmp.HandleTileEntityPacket(j, k, l, i1, ai, af, as);
						break;
					}
				}
			}
		} else {
			Log("Bad TileEntityPacket received.");
		}

	}

	private static void sendPacket(Packet230ModLoader packet230modloader) {
		if(packet230Received && ModLoader.getMinecraftInstance().theWorld != null && ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet230modloader);
		}

	}

	public static BaseModMp GetModInstance(Class<?> class1) {
		for(int i = 0; i < ModLoader.getLoadedMods().size(); ++i) {
			BaseMod basemod = ModLoader.getLoadedMods().get(i);
			if(basemod instanceof BaseModMp) {
				BaseModMp basemodmp = (BaseModMp)basemod;
				if(class1.isInstance(basemodmp)) {
					return (BaseModMp)ModLoader.getLoadedMods().get(i);
				}
			}
		}

		return null;
	}
}
