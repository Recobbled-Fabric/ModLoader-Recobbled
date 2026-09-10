package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

import farn.recobbled_modloader.mixin.forge.RenderBlockAccessor;
import io.github.fkononowicz.modloaderfix.ModLoaderFix;
import io.github.fkononowicz.modloaderfix.ModSorter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

@SuppressWarnings({"unused", "CollectionAddAllCanBeReplacedWithConstructor", "unchecked", "FieldCanBeLocal", "JavaReflectionMemberAccess"})
public final class ModLoader {
	private static final List<TextureFX> animList = new LinkedList<>();
	private static final Map<Integer, BaseMod> blockModels = new HashMap<>();
	private static final Map<Integer, Boolean> blockSpecialInv = new HashMap<>();
	private static final File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
	private static final File cfgfile = new File(cfgdir, "ModLoader.cfg");
	public static Level cfgLoggingLevel = Level.FINER;
	private static Map<String, Class<? extends Entity>> classMap = null;
	private static long clock = 0L;
	public static final boolean DEBUG = false;
	private static Field field_animList = null;
	private static Field field_armorList = null;
	private static Field field_blockList = null;
	private static Field field_modifiers = null;
	private static Field field_TileEntityRenderers = null;
	private static boolean hasInit = false;
	private static int highestEntityId = 3000;
	private static final Map<BaseMod, Boolean> inGameHooks = new HashMap<>();
	private static final Map<BaseMod, Boolean> inGUIHooks = new HashMap<>();
	private static Minecraft instance = null;
	private static int itemSpriteIndex = 0;
	private static int itemSpritesLeft = 0;
	private static final Map<BaseMod, Map<KeyBinding, boolean[]>> keyList = new HashMap<>();
	private static final File logfile = new File(Minecraft.getMinecraftDir(), "ModLoader.txt");
	private static final Logger logger = Logger.getLogger("ModLoader");
	private static FileHandler logHandler = null;
	private static Method method_RegisterEntityID = null;
	private static Method method_RegisterTileEntity = null;
	private static final File modDir = new File(Minecraft.getMinecraftDir(), "/mods/");
	private static final LinkedList<BaseMod> modList = new LinkedList<>();
	private static int nextBlockModelID = 1000;
	private static final Map<Integer, Map<String, Integer>> overrides = new HashMap<>();
	public static final Properties props = new Properties();
	private static BiomeGenBase[] standardBiomes;
	private static int terrainSpriteIndex = 0;
	private static int terrainSpritesLeft = 0;
	private static String texPack = null;
	private static boolean texturesAdded = false;
	private static final boolean[] usedItemSprites = new boolean[256];
	private static final boolean[] usedTerrainSprites = new boolean[256];
	public static final String VERSION = "ModLoader Beta 1.7.3";

	public static void AddAchievementDesc(Achievement achievement, String name, String description) {
		try {
			if(achievement.statName.contains(".")) {
				String[] e = achievement.statName.split("\\.");
				if(e.length == 2) {
					String key = e[1];
					AddLocalization("achievement." + key, name);
					AddLocalization("achievement." + key + ".desc", description);
					setPrivateValue(StatBase.class, achievement, 1, StringTranslate.getInstance().translateKey("achievement." + key));
					setPrivateValue(Achievement.class, achievement, 3, StringTranslate.getInstance().translateKey("achievement." + key + ".desc"));
				} else {
					setPrivateValue(StatBase.class, achievement, 1, name);
					setPrivateValue(Achievement.class, achievement, 3, description);
				}
			} else {
				setPrivateValue(StatBase.class, achievement, 1, name);
				setPrivateValue(Achievement.class, achievement, 3, description);
			}
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException var5) {
			logger.throwing("ModLoader", "AddAchievementDesc", var5);
			ThrowException(var5);
		}

    }

	public static int AddAllFuel(int id) {
		logger.finest("Finding fuel for " + id);
		int result = 0;

		for(BaseMod mod : modList) {
			result = mod.AddFuel(id);
			if(result != 0) {
				logger.finest("Returned " + result);
				break;
			}
		}

		return result;
	}

	public static void AddAllRenderers(Map<Class<? extends Entity>, Render> o) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

        for (BaseMod mod : modList) {
            mod.AddRenderer(o);
        }

	}

	public static void addAnimation(TextureFX anim) {
		logger.finest("Adding animation " + anim.toString());

        for (TextureFX oldAnim : animList) {
            if (oldAnim.tileImage == anim.tileImage && oldAnim.iconIndex == anim.iconIndex) {
                animList.remove(anim);
                break;
            }
        }

		animList.add(anim);
	}

	public static int AddArmor(String armor) {
		try {
			String[] e = (String[])field_armorList.get((Object)null);
			List<String> existingArmorList = Arrays.asList(e);
			ArrayList<String> combinedList = new ArrayList<>();
			combinedList.addAll(existingArmorList);
			if(!combinedList.contains(armor)) {
				combinedList.add(armor);
			}

			int index = combinedList.indexOf(armor);
			field_armorList.set((Object)null, combinedList.toArray(new String[0]));
			return index;
		} catch (IllegalArgumentException | IllegalAccessException var5) {
			logger.throwing("ModLoader", "AddArmor", var5);
			ThrowException("An impossible error has occured!", var5);
		}

        return -1;
	}

	public static void AddLocalization(String key, String value) {
		Properties props = null;

		try {
			props = getPrivateValue(StringTranslate.class, StringTranslate.getInstance(), 1);
		} catch (SecurityException | NoSuchFieldException var4) {
			logger.throwing("ModLoader", "AddLocalization", var4);
			ThrowException(var4);
		}

        if(props != null) {
			props.put(key, value);
		}

	}

	private static void addMod(ClassLoader loader, String filename) {
		try {
			String e = filename.split("\\.")[0];
			if(e.contains("$")) {
				return;
			}

			if(props.containsKey(e) && (props.getProperty(e).equalsIgnoreCase("no") || props.getProperty(e).equalsIgnoreCase("off"))) {
				return;
			}

			//if(e.startsWith("/")) e = e.substring(1);

			Class<?> instclass = FabricLauncherBase.getClass(e);
			if(BaseMod.class.isAssignableFrom(instclass)) {
				setupProperties((Class<? extends BaseMod>)instclass);
				BaseMod mod = (BaseMod)instclass.newInstance();
				modList.add(mod);
				logger.fine("Mod Loaded: \"" + mod + "\" from " + filename);
				System.out.println("Mod Loaded: " + mod);
			}
        } catch (Throwable var6) {
			logger.fine("Failed to load mod from \"" + filename + "\"");
			System.out.println("Failed to load mod from \"" + filename + "\"");
			logger.throwing("ModLoader", "addMod", var6);
			ThrowException(var6);
		}

	}

	public static void AddName(Object instance, String name) {
		String tag = null;
		Exception e3;
		if(instance instanceof Item) {
			Item e = (Item)instance;
			if(e.getItemName() != null) {
				tag = e.getItemName() + ".name";
			}
		} else if(instance instanceof Block) {
			Block e1 = (Block)instance;
			if(e1.getBlockName() != null) {
				tag = e1.getBlockName() + ".name";
			}
		} else if(instance instanceof ItemStack) {
			ItemStack e2 = (ItemStack)instance;
			if(e2.getItemName() != null) {
				tag = e2.getItemName() + ".name";
			}
		} else {
			e3 = new Exception(instance.getClass().getName() + " cannot have name attached to it!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

		if(tag != null) {
			AddLocalization(tag, name);
		} else {
			e3 = new Exception(instance + " is missing name tag!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

	}

	public static int addOverride(String fileToOverride, String fileToAdd) {
		try {
			int e = getUniqueSpriteIndex(fileToOverride);
			addOverride(fileToOverride, fileToAdd, e);
			return e;
		} catch (Throwable var3) {
			logger.throwing("ModLoader", "addOverride", var3);
			ThrowException(var3);
			throw new RuntimeException(var3);
		}
	}

	public static void addOverride(String path, String overlayPath, int index) {
		boolean dst = true;
		boolean left = false;
		int dst1;
		int left1;
		if(path.equals("/terrain.png")) {
			dst1 = 0;
			left1 = terrainSpritesLeft;
		} else {
			if(!path.equals("/gui/items.png")) {
				return;
			}

			dst1 = 1;
			left1 = itemSpritesLeft;
		}

		System.out.println("Overriding " + path + " with " + overlayPath + " @ " + index + ". " + left1 + " left.");
		logger.finer("addOverride(" + path + "," + overlayPath + "," + index + "). " + left1 + " left.");
        Map<String, Integer> overlays = overrides.computeIfAbsent(dst1, k -> new HashMap<>());
        overlays.put(overlayPath, index);
	}

	public static void AddRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().addRecipe(output, params);
	}

	public static void AddShapelessRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().addShapelessRecipe(output, params);
	}

	public static void AddSmelting(int input, ItemStack output) {
		FurnaceRecipes.smelting().addSmelting(input, output);
	}

	public static void AddSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, EnumCreatureType spawnList) {
		AddSpawn(entityClass, weightedProb, spawnList, (BiomeGenBase[])null);
	}

	public static void AddSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, EnumCreatureType spawnList, BiomeGenBase... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

            for (BiomeGenBase biome : biomes) {
                List<SpawnListEntry> list = biome.getSpawnableList(spawnList);
                if (list != null) {
                    boolean exists = false;

                    for (SpawnListEntry entry : list) {
                        if (entry.entityClass == entityClass) {
                            entry.spawnRarityRate = weightedProb;
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        list.add(new SpawnListEntry(entityClass, weightedProb));
                    }
                }
            }

		}
	}

	public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList) {
		AddSpawn(entityName, weightedProb, spawnList, (BiomeGenBase[])null);
	}

	public static void AddSpawn(String entityName, int weightedProb, EnumCreatureType spawnList, BiomeGenBase... biomes) {
		Class<? extends EntityLiving> entityClass = (Class<? extends EntityLiving>)classMap.get(entityName);
		if(entityClass != null && EntityLiving.class.isAssignableFrom(entityClass)) {
			AddSpawn(entityClass, weightedProb, spawnList, biomes);
		}

	}

	public static boolean DispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
		boolean result = false;

        Iterator<BaseMod> iter = modList.iterator();
        while (iter.hasNext() && !result) {
            result = iter.next().DispenseEntity(world, x, y, z, xVel, zVel, item);
        }

        return result;
	}

	public static List<BaseMod> getLoadedMods() {
		return Collections.unmodifiableList(modList);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Minecraft getMinecraftInstance() {
		if(instance == null) {
			try {
				ThreadGroup e = Thread.currentThread().getThreadGroup();
				int count = e.activeCount();
				Thread[] threads = new Thread[count];
				e.enumerate(threads);

				for(int i = 0; i < threads.length; ++i) {
					if(threads[i].getName().equals("Minecraft main thread")) {
						instance = getPrivateValue(Thread.class, threads[i], "target");
						break;
					}
				}
			} catch (SecurityException | NoSuchFieldException var4) {
				logger.throwing("ModLoader", "getMinecraftInstance", var4);
				throw new RuntimeException(var4);
			}
        }

		return instance;
	}

	public static <T, E> T getPrivateValue(Class<?> instanceclass, E instance, int fieldindex) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredFields()[fieldindex];
			e.setAccessible(true);
			return (T)e.get(instance);
		} catch (IllegalAccessException var4) {
			logger.throwing("ModLoader", "getPrivateValue", var4);
			ThrowException("An impossible error has occured!", var4);
			return null;
		}
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredField(field);
			e.setAccessible(true);
			return (T)e.get(instance);
		} catch (IllegalAccessException var4) {
			logger.throwing("ModLoader", "getPrivateValue", var4);
			ThrowException("An impossible error has occured!", var4);
			return null;
		}
	}

	public static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem) {
		int id = nextBlockModelID++;
		blockModels.put(id, mod);
		blockSpecialInv.put(id, full3DItem);
		return id;
	}

	public static int getUniqueEntityId() {
		return highestEntityId++;
	}

	private static int getUniqueItemSpriteIndex() {
		while(itemSpriteIndex < usedItemSprites.length) {
			if(!usedItemSprites[itemSpriteIndex]) {
				usedItemSprites[itemSpriteIndex] = true;
				--itemSpritesLeft;
				return itemSpriteIndex++;
			}

			++itemSpriteIndex;
		}

		Exception e = new Exception("No more empty item sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	public static int getUniqueSpriteIndex(String path) {
		if(path.equals("/gui/items.png")) {
			return getUniqueItemSpriteIndex();
		} else if(path.equals("/terrain.png")) {
			return getUniqueTerrainSpriteIndex();
		} else {
			Exception e = new Exception("No registry for this texture: " + path);
			logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
			ThrowException(e);
			return 0;
		}
	}

	private static int getUniqueTerrainSpriteIndex() {
		while(terrainSpriteIndex < usedTerrainSprites.length) {
			if(!usedTerrainSprites[terrainSpriteIndex]) {
				usedTerrainSprites[terrainSpriteIndex] = true;
				--terrainSpritesLeft;
				return terrainSpriteIndex++;
			}

			++terrainSpriteIndex;
		}

		Exception e = new Exception("No more empty terrain sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	private static void init() {
		hasInit = true;
		String usedItemSpritesString = "1111111111111111111111111111111111111111111111111111111111111011111111111111111111111111111011111111111111111111111111111111111111111111111101110100011100000111010000010000011101000000000001110100000000000111010000000000000000000000000000001111000000000000";
		String usedTerrainSpritesString = "1111111111111111111111111111110111111111111111111111110111111111111111111111000111111011111111111111001111111110111111111111100011111111000010001111011111111111111111111111111111111111111111111111111111111111111011111100001101000000000001111111111111000011";

		for(int e = 0; e < 256; ++e) {
			usedItemSprites[e] = usedItemSpritesString.charAt(e) == 49;
			if(!usedItemSprites[e]) {
				++itemSpritesLeft;
			}

			usedTerrainSprites[e] = usedTerrainSpritesString.charAt(e) == 49;
			if(!usedTerrainSprites[e]) {
				++terrainSpritesLeft;
			}
		}

		try {
			instance = getPrivateValue(Minecraft.class, null, 1);
			instance.entityRenderer = new EntityRendererProxy(instance);
			classMap = getPrivateValue(EntityList.class, null, 0);
			try {
				field_modifiers = Field.class.getDeclaredField("modifiers");
				field_modifiers.setAccessible(true);
			} catch (NoSuchFieldException ignored) {
			}
			field_blockList = Session.class.getDeclaredFields()[0];
			field_blockList.setAccessible(true);
			field_TileEntityRenderers = TileEntityRenderer.class.getDeclaredFields()[0];
			field_TileEntityRenderers.setAccessible(true);
			field_armorList = RenderPlayer.class.getDeclaredFields()[3];
			if(field_modifiers != null)
				field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & -17);
			field_armorList.setAccessible(true);
			field_animList = RenderEngine.class.getDeclaredFields()[6];
			field_animList.setAccessible(true);
			Field[] var15 = BiomeGenBase.class.getDeclaredFields();
			LinkedList<BiomeGenBase> mod = new LinkedList<>();

            for (Field field : var15) {
                Class<?> fieldType = field.getType();
                if ((field.getModifiers() & 8) != 0 && fieldType.isAssignableFrom(BiomeGenBase.class)) {
                    BiomeGenBase biome = (BiomeGenBase) field.get((Object) null);
                    if (!(biome instanceof BiomeGenHell) && !(biome instanceof BiomeGenSky)) {
                        mod.add(biome);
                    }
                }
            }

			standardBiomes = mod.toArray(new BiomeGenBase[0]);

			try {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("a", Class.class, String.class);
			} catch (NoSuchMethodException var8) {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("addMapping", Class.class, String.class);
			}

			method_RegisterTileEntity.setAccessible(true);

			try {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("a", Class.class, String.class, Integer.TYPE);
			} catch (NoSuchMethodException var7) {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("addMapping", Class.class, String.class, Integer.TYPE);
			}

			method_RegisterEntityID.setAccessible(true);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException |
                 NoSuchFieldException var10) {
			logger.throwing("ModLoader", "init", var10);
			ThrowException(var10);
			throw new RuntimeException(var10);
		}

        try {
			loadConfig();
			if(props.containsKey("loggingLevel")) {
				cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
			}

			if(props.containsKey("grassFix")) {
				RenderBlockAccessor.setCfgGrassFix(Boolean.parseBoolean(props.getProperty("grassFix")));
			}

			logger.setLevel(cfgLoggingLevel);
			if((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null) {
				logHandler = new FileHandler(logfile.getPath());
				logHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logHandler);
			}

			logger.fine("ModLoader Beta 1.7.3 Initializing...");
			System.out.println("ModLoader Beta 1.7.3 Initializing...");
			File var16 = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			modDir.mkdirs();
			readModsFromEntrypoint();
			readFromModFolder(modDir);
			readFromClassPath(var16);
			System.out.println("Done.");
			props.setProperty("loggingLevel", cfgLoggingLevel.getName());
			props.setProperty("grassFix", "" + RenderBlockAccessor.getCfgGrassFix());

            for (BaseMod var17 : modList) {
                var17.ModsLoaded();
                if (!props.containsKey(var17.getClass().getName())) {
                    props.setProperty(var17.getClass().getName(), "on");
                }
            }

			instance.gameSettings.keyBindings = RegisterAllKeys(instance.gameSettings.keyBindings);
			instance.gameSettings.loadOptions();
			initStats();
			saveConfig();
		} catch (Throwable var9) {
			logger.throwing("ModLoader", "init", var9);
			ThrowException("ModLoader has failed to initialize.", var9);
			if(logHandler != null) {
				logHandler.close();
			}

			throw new RuntimeException(var9);
		}
	}

	private static void initStats() {
		int idHashSet;
		String id;
		for(idHashSet = 0; idHashSet < Block.blocksList.length; ++idHashSet) {
			if(!StatList.field_25169_C.containsKey(16777216 + idHashSet) && Block.blocksList[idHashSet] != null && Block.blocksList[idHashSet].getEnableStats()) {
				id = StringTranslate.getInstance().translateKeyFormat("stat.mineBlock", Block.blocksList[idHashSet].translateBlockName());
				StatList.mineBlockStatArray[idHashSet] = (new StatCrafting(16777216 + idHashSet, id, idHashSet)).registerStat();
				StatList.field_25185_d.add(StatList.mineBlockStatArray[idHashSet]);
			}
		}

		for(idHashSet = 0; idHashSet < Item.itemsList.length; ++idHashSet) {
			if(!StatList.field_25169_C.containsKey(16908288 + idHashSet) && Item.itemsList[idHashSet] != null) {
				id = StringTranslate.getInstance().translateKeyFormat("stat.useItem", Item.itemsList[idHashSet].getStatName());
				StatList.field_25172_A[idHashSet] = (new StatCrafting(16908288 + idHashSet, id, idHashSet)).registerStat();
				if(idHashSet >= Block.blocksList.length) {
					StatList.field_25186_c.add(StatList.field_25172_A[idHashSet]);
				}
			}

			if(!StatList.field_25169_C.containsKey(16973824 + idHashSet) && Item.itemsList[idHashSet] != null && Item.itemsList[idHashSet].isDamagable()) {
				id = StringTranslate.getInstance().translateKeyFormat("stat.breakItem", Item.itemsList[idHashSet].getStatName());
				StatList.field_25170_B[idHashSet] = (new StatCrafting(16973824 + idHashSet, id, idHashSet)).registerStat();
			}
		}

		HashSet<Integer> var4 = new HashSet<>();
		List<IRecipe> var2 = CraftingManager.getInstance().getRecipeList();

		for(IRecipe var5 : var2) {
			var4.add(var5.getRecipeOutput().itemID);
		}

		Collection<ItemStack> smelt = FurnaceRecipes.smelting().getSmeltingList().values();

		for(ItemStack var5 : smelt) {
			var4.add(var5.itemID);
		}

		for(Integer var6 : var4) {
			if(!StatList.field_25169_C.containsKey(16842752 + var6) && Item.itemsList[var6] != null) {
				String str = StringTranslate.getInstance().translateKeyFormat("stat.craftItem", Item.itemsList[var6].getStatName());
				StatList.field_25158_z[var6] = (new StatCrafting(16842752 + var6, str, var6)).registerStat();
			}
		}

	}

	public static boolean isGUIOpen(Class<? extends GuiScreen> gui) {
		Minecraft game = getMinecraftInstance();
		return gui == null ? game.currentScreen == null : gui.isInstance(game.currentScreen);
	}

	public static boolean isModLoaded(String modname) {
		try {
			Class<?> chk = Class.forName(modname);
			for (BaseMod mod : modList) {
				if (chk.isInstance(mod)) {
					return true;
				}
			}
		} catch (ClassNotFoundException ignored) {
		}
		return false;
	}

	public static void loadConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canRead()) {
				FileInputStream in = new FileInputStream(cfgfile);
				props.load(in);
				in.close();
			}

		}
	}

	public static BufferedImage loadImage(RenderEngine texCache, String path) throws Exception {
		TexturePackList pack = getPrivateValue(RenderEngine.class, texCache, 11);
		InputStream input = pack.selectedTexturePack.getResourceAsStream(path);
		if(input == null) {
			throw new Exception("Image not found: " + path);
		} else {
			BufferedImage image = ImageIO.read(input);
			if(image == null) {
				throw new Exception("Image corrupted: " + path);
			} else {
				return image;
			}
		}
	}

	public static void OnItemPickup(EntityPlayer player, ItemStack item) {

        for (BaseMod mod : modList) {
            mod.OnItemPickup(player, item);
        }

	}

	public static void OnTick(Minecraft game) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		if(texPack == null || !Objects.equals(game.gameSettings.skin, texPack)) {
			texturesAdded = false;
			texPack = game.gameSettings.skin;
		}

		if(!texturesAdded && game.renderEngine != null) {
			RegisterAllTextureOverrides(game.renderEngine);
			texturesAdded = true;
		}

		long newclock = 0L;
		if(game.theWorld != null) {
			newclock = game.theWorld.getWorldTime();
			Iterator<Entry<BaseMod, Boolean>> modSet = inGameHooks.entrySet().iterator();
			Entry<BaseMod, Boolean> modSet1;

			label93:
			while(true) {
				do {
					if(!modSet.hasNext()) {
						break label93;
					}

					modSet1 = modSet.next();
				} while(clock == newclock && modSet1.getValue());

				if(!modSet1.getKey().OnTickInGame(game)) {
					modSet.remove();
				}
			}
		}

		if(game.currentScreen != null) {
			Iterator<Entry<BaseMod, Boolean>> modSet = inGUIHooks.entrySet().iterator();
			Entry<BaseMod, Boolean> modSet1;

			label80:
			while(true) {
				do {
					if(!modSet.hasNext()) {
						break label80;
					}

					modSet1 = modSet.next();
				} while(clock == newclock && modSet1.getValue() & game.theWorld != null);

				if(!modSet1.getKey().OnTickInGUI(game, game.currentScreen)) {
					modSet.remove();
				}
			}
		}

		if(clock != newclock) {
			Iterator<Entry<BaseMod, Map<KeyBinding, boolean[]>>> modSet3 = keyList.entrySet().iterator();

			label66:
			while(modSet3.hasNext()) {
				Entry<BaseMod, Map<KeyBinding, boolean[]>> modSet2 = modSet3.next();
				Iterator<Entry<KeyBinding, boolean[]>> var6 = modSet2.getValue().entrySet().iterator();

				while(true) {
					Entry<KeyBinding, boolean[]> keySet;
					boolean state;
					boolean[] keyInfo;
					boolean oldState;
					do {
						do {
							if(!var6.hasNext()) {
								continue label66;
							}

							keySet = var6.next();
							state = Keyboard.isKeyDown(keySet.getKey().keyCode);
							keyInfo = keySet.getValue();
							oldState = keyInfo[1];
							keyInfo[1] = state;
						} while(!state);
					} while(oldState && !keyInfo[0]);

					modSet2.getKey().KeyboardEvent(keySet.getKey());
				}
			}
		}

		clock = newclock;
	}

	public static void OpenGUI(EntityPlayer player, GuiScreen gui) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Minecraft game = getMinecraftInstance();
		if(game.thePlayer == player) {
			if(gui != null) {
				game.displayGuiScreen(gui);
			}

		}
	}

	public static void PopulateChunk(IChunkProvider generator, int chunkX, int chunkZ, World world) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Random rnd = new Random(world.getRandomSeed());
		long xSeed = rnd.nextLong() / 2L * 2L + 1L;
		long zSeed = rnd.nextLong() / 2L * 2L + 1L;
		rnd.setSeed((long)chunkX * xSeed + (long)chunkZ * zSeed ^ world.getRandomSeed());

        for (BaseMod mod : modList) {
            if (generator.makeString().equals("RandomLevelSource")) {
                mod.GenerateSurface(world, rnd, chunkX << 4, chunkZ << 4);
            } else if (generator.makeString().equals("HellRandomLevelSource")) {
                mod.GenerateNether(world, rnd, chunkX << 4, chunkZ << 4);
            }
        }

	}

	private static void readFromClassPath(File classPath) throws IOException {
		ClassLoader classLoader = ModLoader.class.getClassLoader();
		String mlPackage = ModLoaderFix.getMLPackage();
		boolean isPathAModFile = classPath.isFile() && (classPath.getName().endsWith(".jar") || classPath.getName().endsWith(".zip"));
		if (isPathAModFile) {
			logger.info("Loading zip file: " + classPath);
			InputStream is = Files.newInputStream(classPath.toPath());
			ZipInputStream zis = new ZipInputStream(is);
			String modPrefix = "mod_";
			if (mlPackage != null) {
				modPrefix = mlPackage.replace('.', '/') + "/" + modPrefix; // Add ML package at start of prefix
			}

			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				if (!entry.isDirectory() && name.startsWith(modPrefix) && name.endsWith(".class")) {
					logger.info("Loading mod from zip file: " + name);
					addMod(classLoader, name);
				}
			}

			is.close();
		} else if (classPath.isDirectory()) {
			File modDirectory;
			if (mlPackage != null) {
				modDirectory = new File(classPath, mlPackage.replace('.', File.separatorChar)); // Update directory with ML package
			} else {
				modDirectory = classPath;
			}

			if (!modDirectory.exists()) {
				logger.warning("Tried loading directory with no ML package: " + modDirectory);
				return;
			}

			File[] files = modDirectory.listFiles();
			if (files == null) {
				logger.warning("Tried loading an empty directory: " + modDirectory);
				return;
			}

			logger.info("Searching directory: " + modDirectory);
			for (File file : files) {
				String name = file.getName();
				if (file.isFile() && name.startsWith("mod_") && name.endsWith(".class")) {
					String mod = classPath.toPath().relativize(file.toPath()).toString();
					logger.info("Loading mod: " + mod + " from directory: " + modDirectory);
					addMod(classLoader, mod);
				}
			}

		} else {
			logger.warning("Tried to load from classpath: " + classPath + " but the classpath provided is not a directory or a zip file");
		}

	}

	private static void readFromModFolder(File folder) throws IOException, IllegalArgumentException, SecurityException {
		ClassLoader loader = FabricLauncherBase.getLauncher().getTargetClassLoader();
		if(!folder.isDirectory()) {
			throw new IllegalArgumentException("folder must be a Directory.");
		} else {
			File[] sourcefiles = sortMods(folder);
			List<File> validList = new ArrayList<>();
            for (File source : sourcefiles) {
                if (source.isDirectory()) {
                    FabricLauncherBase.getLauncher().addToClassPath(source.toPath());
                    validList.add(source);
                } else if (source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
                    try (ZipFile zipFile = new ZipFile(source)) {
                        if (zipFile.getEntry("fabric.mod.json") == null) {
                            logger.finer("Adding mods from " + zipFile.getName());
                            FabricLauncherBase.getLauncher().addToClassPath(source.toPath());
                            validList.add(source);
                        }
                    }
                }
            }

            for (File source : validList) {
                if (source.isDirectory() || source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
                    logger.finer("Adding mods from " + source.getCanonicalPath());
                    String name;
                    if (!source.isFile()) {
                        if (source.isDirectory()) {
                            logger.finer("Directory found.");
                            File[] var12 = source.listFiles();
                            if (var12 != null) {
                                for (File file : var12) {
                                    name = file.getName();
                                    if (file.isFile() && name.startsWith("mod_") && name.endsWith(".class")) {
                                        addMod(loader, name);
                                    }
                                }
                            }
                        }
                    } else {
                        logger.finer("Zip found.");
                        FileInputStream pkg = new FileInputStream(source);
                        ZipInputStream dirfiles = new ZipInputStream(pkg);
                        ZipEntry j;

                        while (true) {
                            j = dirfiles.getNextEntry();
                            if (j == null) {
                                dirfiles.close();
                                pkg.close();
                                break;
                            }

                            name = j.getName();
                            if (!j.isDirectory() && name.startsWith("mod_") && name.endsWith(".class")) {
                                addMod(loader, name);
                            }
                        }
                    }
                }
            }

		}
	}

	private static File[] sortMods(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			Arrays.sort(files, new ModSorter());
		}

		logger.info("Sorted mods for: " + directory);
		return files;
	}


	public static KeyBinding[] RegisterAllKeys(KeyBinding[] w) {
		LinkedList<KeyBinding> combinedList = new LinkedList<>();
		combinedList.addAll(Arrays.asList(w));

        for (Map<KeyBinding, boolean[]> keyBindingMap : keyList.values()) {
            combinedList.addAll(keyBindingMap.keySet());
        }

		return combinedList.toArray(new KeyBinding[0]);
	}

	public static void RegisterAllTextureOverrides(RenderEngine texCache) {
		animList.clear();
		Minecraft game = getMinecraftInstance();

        for (BaseMod overlay : modList) {
            overlay.RegisterAnimation(game);
        }

        for (TextureFX overlay1 : animList) {
            texCache.registerTextureFX(overlay1);
        }

        for (Entry<Integer, Map<String, Integer>> integerMapEntry : overrides.entrySet()) {
            for (Entry<String, Integer> overlayEntry : integerMapEntry.getValue().entrySet()) {
                String overlayPath = overlayEntry.getKey();
                int index = overlayEntry.getValue();
                int dst = integerMapEntry.getKey();

                try {
                    BufferedImage e = loadImage(texCache, overlayPath);
                    ModTextureStatic anim = new ModTextureStatic(index, dst, e);
                    texCache.registerTextureFX(anim);
                } catch (Exception var11) {
                    logger.throwing("ModLoader", "RegisterAllTextureOverrides", var11);
                    ThrowException(var11);
                    throw new RuntimeException(var11);
                }
            }
        }

	}

	public static void RegisterBlock(Block block) {
		RegisterBlock(block, null);
	}

	public static void RegisterBlock(Block block, Class<? extends ItemBlock> itemclass) {
		try {
			if(block == null) {
				throw new IllegalArgumentException("block parameter cannot be null.");
			}

			List<Block> e = (List<Block>)field_blockList.get((Object)null);
			e.add(block);
			int id = block.blockID;
			ItemBlock item;
			if(itemclass != null) {
				item = itemclass.getConstructor(new Class[]{Integer.TYPE}).newInstance(id - 256);
			} else {
				item = new ItemBlock(id - 256);
			}

			if(Block.blocksList[id] != null && Item.itemsList[id] == null) {
				Item.itemsList[id] = item;
			}
		} catch (IllegalArgumentException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 SecurityException | IllegalAccessException var5) {
			logger.throwing("ModLoader", "RegisterBlock", var5);
			ThrowException(var5);
		}

    }

	public static void RegisterEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
		try {
			method_RegisterEntityID.invoke((Object)null, entityClass, entityName, id);
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
			logger.throwing("ModLoader", "RegisterEntityID", var4);
			ThrowException(var4);
		}

    }

	public static void RegisterKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat) {
		Map<KeyBinding, boolean[]> keyMap = keyList.get(mod);
		if(keyMap == null) {
			keyMap = new HashMap<>();
		}

		keyMap.put(keyHandler, new boolean[]{allowRepeat, false});
		keyList.put(mod, keyMap);
	}

	public static void RegisterTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		RegisterTileEntity(tileEntityClass, id, null);
	}

	public static void RegisterTileEntity(Class<? extends TileEntity> tileEntityClass, String id, TileEntitySpecialRenderer renderer) {
		try {
			method_RegisterTileEntity.invoke((Object)null, tileEntityClass, id);
			if(renderer != null) {
				TileEntityRenderer e = TileEntityRenderer.instance;
				Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> renderers = (Map)field_TileEntityRenderers.get(e);
				renderers.put(tileEntityClass, renderer);
				renderer.setTileEntityRenderer(e);
			}
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
			logger.throwing("ModLoader", "RegisterTileEntity", var5);
			ThrowException(var5);
		}

    }

	public static void RemoveSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList) {
		RemoveSpawn(entityClass, spawnList, (BiomeGenBase[])null);
	}

	public static void RemoveSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, BiomeGenBase... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

            for (BiomeGenBase biome : biomes) {
                List<SpawnListEntry> list = biome.getSpawnableList(spawnList);
                if (list != null) {
                    list.removeIf(spawnListEntry -> spawnListEntry.entityClass == entityClass);
                }
            }

		}
	}

	public static void RemoveSpawn(String entityName, EnumCreatureType spawnList) {
		RemoveSpawn(entityName, spawnList, (BiomeGenBase[])null);
	}

	public static void RemoveSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes) {
		Class<? extends EntityLiving> entityClass = (Class<? extends EntityLiving>)classMap.get(entityName);
		if(entityClass != null && EntityLiving.class.isAssignableFrom(entityClass)) {
			RemoveSpawn(entityClass, spawnList, biomes);
		}

	}

	public static boolean RenderBlockIsItemFull3D(int modelID) {
		return !blockSpecialInv.containsKey(modelID) ? modelID == 16 : blockSpecialInv.get(modelID);
	}

	public static void RenderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID) {
		BaseMod mod = blockModels.get(modelID);
		if(mod != null) {
			mod.RenderInvBlock(renderer, block, metadata, modelID);
		}
	}

	public static boolean RenderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		BaseMod mod = blockModels.get(modelID);
		return mod != null && mod.RenderWorldBlock(renderer, world, x, y, z, block, modelID);
	}

	public static void saveConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canWrite()) {
				FileOutputStream out = new FileOutputStream(cfgfile);
				props.store(out, "ModLoader Config");
				out.close();
			}

		}
	}

	public static void SetInGameHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGameHooks.put(mod, useClock);
		} else {
			inGameHooks.remove(mod);
		}

	}

	public static void SetInGUIHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGUIHooks.put(mod, useClock);
		} else {
			inGUIHooks.remove(mod);
		}

	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredFields()[fieldindex];
			e.setAccessible(true);
			if(field_modifiers != null) {
				int modifiers = field_modifiers.getInt(e);
				if((modifiers & 16) != 0) {
					field_modifiers.setInt(e, modifiers & -17);
				}
			}

			e.set(instance, value);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "setPrivateValue", var6);
			ThrowException("An impossible error has occured!", var6);
		}

	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field e = instanceclass.getDeclaredField(field);
			e.setAccessible(true);
			if(field_modifiers != null) {
				int modifiers = field_modifiers.getInt(e);
				if((modifiers & 16) != 0) {
					field_modifiers.setInt(e, modifiers & -17);
				}
			}
			e.set(instance, value);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "setPrivateValue", var6);
			ThrowException("An impossible error has occured!", var6);
		}

	}

	private static void setupProperties(Class<? extends BaseMod> mod) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException {
		Properties modprops = new Properties();
		File modcfgfile = new File(cfgdir, mod.getName() + ".cfg");
		if(modcfgfile.exists() && modcfgfile.canRead()) {
			modprops.load(Files.newInputStream(modcfgfile.toPath()));
		}

		StringBuilder helptext = new StringBuilder();
		Field[] var7 = mod.getFields();
		int var6 = var7.length;

        for (Field field : var7) {
            if ((field.getModifiers() & 8) != 0 && field.isAnnotationPresent(MLProp.class)) {
                Class<?> type = field.getType();
                MLProp annotation = field.getAnnotation(MLProp.class);
                String key = annotation.name().isEmpty() ? field.getName() : annotation.name();
                Object currentvalue = field.get(null);
                StringBuilder range = new StringBuilder();
                if (annotation.min() != -Double.POSITIVE_INFINITY) {
                    range.append(String.format(",>=%.1f", annotation.min()));
                }

                if (annotation.max() != Double.POSITIVE_INFINITY) {
                    range.append(String.format(",<=%.1f", annotation.max()));
                }

                StringBuilder info = new StringBuilder();
                if (!annotation.info().isEmpty()) {
                    info.append(" -- ");
                    info.append(annotation.info());
                }

                helptext.append(String.format("%s (%s:%s%s)%s\n", key, type.getName(), currentvalue, range, info));
                if (modprops.containsKey(key)) {
                    String strvalue = modprops.getProperty(key);
                    Object value = null;
                    if (type.isAssignableFrom(String.class)) {
                        value = strvalue;
                    } else if (type.isAssignableFrom(Integer.TYPE)) {
                        value = Integer.parseInt(strvalue);
                    } else if (type.isAssignableFrom(Short.TYPE)) {
                        value = Short.parseShort(strvalue);
                    } else if (type.isAssignableFrom(Byte.TYPE)) {
                        value = Byte.parseByte(strvalue);
                    } else if (type.isAssignableFrom(Boolean.TYPE)) {
                        value = Boolean.parseBoolean(strvalue);
                    } else if (type.isAssignableFrom(Float.TYPE)) {
                        value = Float.parseFloat(strvalue);
                    } else if (type.isAssignableFrom(Double.TYPE)) {
                        value = Double.parseDouble(strvalue);
                    }

                    if (value != null) {
                        if (value instanceof Number) {
                            double num = ((Number) value).doubleValue();
                            if (annotation.min() != -Double.POSITIVE_INFINITY && num < annotation.min() || annotation.max() != Double.POSITIVE_INFINITY && num > annotation.max()) {
                                continue;
                            }
                        }

                        logger.finer(key + " set to " + value);
                        if (!value.equals(currentvalue)) {
                            field.set((Object) null, value);
                        }
                    }
                } else {
                    logger.finer(key + " not in config, using default: " + currentvalue);
                    modprops.setProperty(key, currentvalue.toString());
                }
            }
        }

		if(!modprops.isEmpty() && (modcfgfile.exists() || modcfgfile.createNewFile()) && modcfgfile.canWrite()) {
			modprops.store(Files.newOutputStream(modcfgfile.toPath()), helptext.toString());
		}

	}

	public static void TakenFromCrafting(EntityPlayer player, ItemStack item) {

        for (BaseMod mod : modList) {
            mod.TakenFromCrafting(player, item);
        }

	}

	public static void TakenFromFurnace(EntityPlayer player, ItemStack item) {

        for (BaseMod mod : modList) {
            mod.TakenFromFurnace(player, item);
        }

	}

	public static void ThrowException(String message, Throwable e) {
		Minecraft game = getMinecraftInstance();
		if(game != null) {
			game.displayUnexpectedThrowable(new UnexpectedThrowable(message, e));
		} else {
			throw new RuntimeException(e);
		}
	}

	private static void ThrowException(Throwable e) {
		ThrowException("Exception occured in ModLoader", e);
	}

	public static void readModsFromEntrypoint() {
		FabricLoader.getInstance().getEntrypointContainers( "modloader_recobbled:base_mod", BaseMod.class).forEach(mod -> {
			try {
				String[] parts = mod.getDefinition().split("\\.");
				String name = parts[parts.length - 1];

				if (props.containsKey(name) && (props.getProperty(name).equalsIgnoreCase("no") || props.getProperty(name).equalsIgnoreCase("off"))) {
					return;
				}

				Class<?> instclass = Class.forName(mod.getDefinition());
				if (!BaseMod.class.isAssignableFrom(instclass)) {
					return;
				}

				setupProperties((Class<? extends BaseMod>) instclass);
				BaseMod modInstance = mod.getEntrypoint();
				if (modInstance != null) {
					modList.add(modInstance);
					logger.fine("Mod Loaded: \"" + modInstance + "\" from mod " + mod.getProvider().getMetadata().getId());
					System.out.println("Mod Loaded: " + modInstance);
				}
			} catch (Throwable e) {
				logger.fine("Failed to load mod from mod \"" + mod.getProvider().getMetadata().getId() + "\"");
				System.out.println("Failed to load mod from mod \"" + mod.getProvider().getMetadata().getId() + "\"");
				logger.throwing("ModLoader", "readModsFromEntrypoint", e);
				ThrowException(e);
			}
		});
	}
}
