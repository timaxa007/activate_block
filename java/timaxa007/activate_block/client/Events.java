package timaxa007.activate_block.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import timaxa007.activate_block.OpenDoorFMod;
import timaxa007.activate_block.network.BlockActivatedMessage;

public class Events {

	static Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onConfigChanged(InputEvent.KeyInputEvent event) {
		if (RegKey.open_door.getIsKeyPressed()) {
			ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

			if (mc.objectMouseOver == null) {
				//logger.warn("Null returned as \'hitResult\', mc shouldn\'t happen!");
			}
			else {
				switch (mc.objectMouseOver.typeOfHit) {
				case ENTITY:
					//if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer, mc.objectMouseOver.entityHit)) {

					//}
					break;
				case BLOCK:
					int x = mc.objectMouseOver.blockX;
					int y = mc.objectMouseOver.blockY;
					int z = mc.objectMouseOver.blockZ;

					if (!mc.theWorld.getBlock(x, y, z).isAir(mc.theWorld, x, y, z)) {

						float hitX = (float)mc.objectMouseOver.hitVec.xCoord - (float)x;
						float hitY = (float)mc.objectMouseOver.hitVec.yCoord - (float)y;
						float hitZ = (float)mc.objectMouseOver.hitVec.zCoord - (float)z;

						boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, mc.objectMouseOver.sideHit, mc.theWorld).isCanceled();
						if (result && mc.theWorld.getBlock(x, y, z).onBlockActivated(mc.theWorld, x, y, z, mc.thePlayer, mc.objectMouseOver.sideHit, hitX, hitY, hitZ)) {
							//flag = false;
							mc.thePlayer.swingItem();
						}

						BlockActivatedMessage message = new BlockActivatedMessage();
						message.x = x;
						message.y = y;
						message.z = z;
						message.side = mc.objectMouseOver.sideHit;
						message.hitX = hitX;
						message.hitY = hitY;
						message.hitZ = hitZ;
						OpenDoorFMod.network.sendToServer(message);

					}
				default:
					break;
				}
			}

		}
	}

}
