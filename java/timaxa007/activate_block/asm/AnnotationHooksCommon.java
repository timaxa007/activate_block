package timaxa007.activate_block.asm;

import cpw.mods.fml.common.eventhandler.Event;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class AnnotationHooksCommon {

	@Hook(returnCondition = ReturnCondition.ALWAYS)
	public static boolean activateBlockOrUseItem(ItemInWorldManager clazz, EntityPlayer player, World world, ItemStack itemStack, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
		if (event.isCanceled()) {
			clazz.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, clazz.theWorld));
			return false;
		}

		if (itemStack != null && itemStack.getItem().onItemUseFirst(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ)) {
			if (itemStack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(clazz.thisPlayerMP, itemStack);
			return true;
		}

		Block block = world.getBlock(x, y, z);
		boolean isAir = block.isAir(world, x, y, z);
		boolean useBlock = !player.isSneaking() || player.getHeldItem() == null;
		if (!useBlock) useBlock = player.getHeldItem().getItem().doesSneakBypassUse(world, x, y, z, player);
		boolean result = false;

		if (useBlock) {
			if (event.useBlock != Event.Result.DENY) {
				//result = block.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
				result = false;
			}
			else {
				clazz.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, clazz.theWorld));
				result = event.useItem != Event.Result.ALLOW;
			}
		}

		if (itemStack != null && !result && event.useItem != Event.Result.DENY) {
			int meta = itemStack.getItemDamage();
			int size = itemStack.stackSize;
			result = itemStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ);
			if (clazz.isCreative()) {
				itemStack.setItemDamage(meta);
				itemStack.stackSize = size;
			}
			if (itemStack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(clazz.thisPlayerMP, itemStack);
		}

		/* Re-enable if this causes bukkit incompatibility, or re-write client side to only send a single packet per right click.
		if (par3ItemStack != null && ((!result && event.useItem != Event.Result.DENY) || event.useItem == Event.Result.ALLOW)) {
			this.tryUseItem(thisPlayerMP, par2World, par3ItemStack);
		}*/
		return result;
	}

}
