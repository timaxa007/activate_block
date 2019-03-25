package timaxa007.activate_block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.ReflectionHelper;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class AnnotationHooks {

	@Hook(returnCondition = ReturnCondition.ALWAYS)
	public static boolean onPlayerRightClick(PlayerControllerMP clazz, EntityPlayer player, World world, ItemStack itemStack, int x, int y, int z, int side, Vec3 vec) {

		Method method = ReflectionHelper.findMethod(PlayerControllerMP.class, clazz, new String[]{"syncCurrentPlayItem", "func_78750_j", "k"});
		try {method.invoke(clazz);}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {e.printStackTrace();}
		//clazz.syncCurrentPlayItem();

		float hitX = (float)vec.xCoord - (float)x;
		float hitY = (float)vec.yCoord - (float)y;
		float hitZ = (float)vec.zCoord - (float)z;
		boolean flag = false;

		if (itemStack != null &&
				itemStack.getItem() != null &&
				itemStack.getItem().onItemUseFirst(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ)) {
			return true;
		}

		if (!player.isSneaking() || player.getHeldItem() == null || player.getHeldItem().getItem().doesSneakBypassUse(world, x, y, z, player)) {
			//flag = world.getBlock(x, y, z).onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
			flag = false;
		}

		if (!flag && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
			ItemBlock itemblock = (ItemBlock)itemStack.getItem();

			if (!itemblock.func_150936_a(world, x, y, z, side, player, itemStack))
				return false;
		}

		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, side, player.inventory.getCurrentItem(), hitX, hitY, hitZ));

		if (flag) return true;
		else if (itemStack == null) return false;
		else if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
			int j1 = itemStack.getItemDamage();
			int i1 = itemStack.stackSize;
			boolean flag1 = itemStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ);
			itemStack.setItemDamage(j1);
			itemStack.stackSize = i1;
			return flag1;
		}
		else {
			if (!itemStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ))
				return false;
			if (itemStack.stackSize <= 0)
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemStack));
			return true;
		}
	}

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
