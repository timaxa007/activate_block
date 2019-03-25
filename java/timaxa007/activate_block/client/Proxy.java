package timaxa007.activate_block.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy extends timaxa007.activate_block.Proxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RegKey.init();
		FMLCommonHandler.instance().bus().register(new Events());
	}

}
