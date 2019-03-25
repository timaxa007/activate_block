package timaxa007.activate_block.network;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class BlockActivatedMessage implements IMessage {

	public int x, y, z, side;
	public float hitX, hitY, hitZ;

	public BlockActivatedMessage() {}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(side);
		buf.writeFloat(hitX);
		buf.writeFloat(hitY);
		buf.writeFloat(hitZ);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		side = buf.readInt();
		hitX = buf.readFloat();
		hitY = buf.readFloat();
		hitZ = buf.readFloat();
	}

	public static class Handler implements IMessageHandler<BlockActivatedMessage, IMessage> {

		@Override
		public IMessage onMessage(BlockActivatedMessage packet, MessageContext message) {
			if (message.side.isClient())
				act(packet);
			else
				act(message.getServerHandler().playerEntity, packet);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void act(BlockActivatedMessage packet) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.theWorld.isAirBlock(packet.x, packet.y, packet.z)) return;
			mc.theWorld.getBlock(packet.x, packet.y, packet.z)
			.onBlockActivated(mc.theWorld, packet.x, packet.y, packet.z, mc.thePlayer, packet.side, packet.hitX, packet.hitY, packet.hitZ);
		}

		private void act(EntityPlayerMP player, BlockActivatedMessage packet) {

			PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, packet.x, packet.y, packet.z, packet.side, player.worldObj);
			if (event.isCanceled()) return;
			if (event.useBlock == Event.Result.DENY) return;

			if (player.worldObj.isAirBlock(packet.x, packet.y, packet.z)) return;
			player.worldObj.getBlock(packet.x, packet.y, packet.z)
			.onBlockActivated(player.worldObj, packet.x, packet.y, packet.z, player, packet.side, packet.hitX, packet.hitY, packet.hitZ);
			/*BlockActivatedMessage message = new BlockActivatedMessage();
			message.x = packet.x;
			message.y = packet.y;
			message.z = packet.z;
			message.side = packet.side;
			message.hitX = packet.hitX;
			message.hitY = packet.hitY;
			message.hitZ = packet.hitZ;
			OpenDoorFMod.network.sendTo(message, player);*/
		}

	}

}
