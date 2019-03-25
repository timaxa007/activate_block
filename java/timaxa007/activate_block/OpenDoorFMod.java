package timaxa007.activate_block;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import timaxa007.activate_block.network.BlockActivatedMessage;

@Mod(modid = OpenDoorFMod.MODID, name = OpenDoorFMod.NAME, version = OpenDoorFMod.VERSION)
public class OpenDoorFMod {

	public static final String
	MODID = "activate_block",
	NAME = "Activate Block Mod",
	VERSION = "0.5.2";

	@Mod.Instance(MODID)
	public static OpenDoorFMod instance;

	@SidedProxy(modId = MODID, serverSide = "timaxa007.activate_block.Proxy", clientSide = "timaxa007.activate_block.client.Proxy")
	public static Proxy proxy;

	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		network.registerMessage(BlockActivatedMessage.Handler.class, BlockActivatedMessage.class, 0, Side.CLIENT);
		network.registerMessage(BlockActivatedMessage.Handler.class, BlockActivatedMessage.class, 0, Side.SERVER);
		proxy.preInit(event);
	}

}
