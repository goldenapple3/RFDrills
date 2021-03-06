package goldenapple.rfdrills;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import goldenapple.rfdrills.compat.versionchecker.VersionCheckerCompat;
import goldenapple.rfdrills.compat.waila.WailaCompat;
import goldenapple.rfdrills.config.ConfigHandler;
import goldenapple.rfdrills.init.ModItems;
import goldenapple.rfdrills.init.ModRecipes;
import goldenapple.rfdrills.item.soulupgrade.SoulUpgradeRecipeHandler;
import goldenapple.rfdrills.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME, guiFactory = Reference.GUI_FACTORY, dependencies = Reference.DEPENDENCIES)
public class RFDrills {
    @Mod.Instance
    public static RFDrills instance;
    @SidedProxy(serverSide = Reference.COMMON_PROXY, clientSide = Reference.CLIENT_PROXY)
    public static CommonProxy proxy;
    public static CreativeTabs RFDrillsTab;

    public static boolean isTELoaded;
    public static boolean isEIOLoaded;
    public static boolean isRArsLoaded;
    public static boolean isRArmLoaded;
    public static boolean isSJLoaded;
    public static boolean isXULoaded;
    public static boolean isWailaLoaded;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        isTELoaded = Loader.isModLoaded("ThermalExpansion");
        isEIOLoaded = Loader.isModLoaded("EnderIO");
        isRArsLoaded = Loader.isModLoaded("RedstoneArsenal");
        isRArmLoaded = Loader.isModLoaded("RArm");
        isSJLoaded = Loader.isModLoaded("simplyjetpacks");
        isXULoaded = Loader.isModLoaded("ExtraUtilities");
        isWailaLoaded = Loader.isModLoaded("Waila");

        FMLCommonHandler.instance().bus().register(new ConfigHandler(event.getSuggestedConfigurationFile()));

        if((ConfigHandler.integrateTE && isTELoaded) || (ConfigHandler.integrateEIO && isEIOLoaded)) {
            RFDrillsTab = new CreativeTabs(Reference.MOD_ID) {
                @Override
                public ItemStack getIconItemStack() {
                    ItemStack itemStack = new ItemStack(getTabIconItem());
                    itemStack.setTagInfo("isCreativeTabIcon", new NBTTagByte((byte) 1));
                    return itemStack;
                }

                @Override
                public Item getTabIconItem() {
                    if (ConfigHandler.integrateTE)
                        return ModItems.redstoneDrill;
                    else
                        return ModItems.basicDrill;
                }
            };
        }

        ModItems.init();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if(isWailaLoaded)
            FMLInterModComms.sendMessage("Waila", "register", WailaCompat.class.getName() + ".init");
        VersionCheckerCompat.init();
        ModRecipes.init();

        if(ConfigHandler.integrateEIO)
            MinecraftForge.EVENT_BUS.register(new SoulUpgradeRecipeHandler());
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        //OreHelper.dumpAllOres();
    }
}
