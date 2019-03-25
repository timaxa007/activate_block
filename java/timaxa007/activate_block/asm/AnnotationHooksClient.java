package timaxa007.activate_block.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

@SideOnly(Side.CLIENT)
public class AnnotationHooksClient {

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

}
