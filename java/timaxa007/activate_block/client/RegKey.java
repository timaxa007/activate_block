package timaxa007.activate_block.client;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class RegKey {

	private static final String CAT_NAME = "key.categories.gameplay"/*"category.control_button.name"*/;

	public static final KeyBinding open_door = new KeyBinding("key.open_door.name", Keyboard.KEY_F, CAT_NAME);

	public static void init() {
		ClientRegistry.registerKeyBinding(open_door);
	}

}
