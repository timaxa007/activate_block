package timaxa007.activate_block.client;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.settings.KeyBinding;

public class Proxy extends timaxa007.activate_block.Proxy {

	public static KeyBinding open_door;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		open_door = new KeyBinding("key.activate_block.name", Keyboard.KEY_F, "key.categories.gameplay");
		ClientRegistry.registerKeyBinding(open_door);
		FMLCommonHandler.instance().bus().register(new Events());
	}

}
