package dev.smartshub.shpets.plugin.packet;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.packet.PacketHandler;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class PacketHandlerImpl implements PacketHandler {

    private final PetService petService;

    public PacketHandlerImpl(PetService petService) {
        this.petService = petService;
    }

    @Override
    public void inject (Player player){

        ChannelDuplexHandler channelHandler = new ChannelDuplexHandler(){

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object rawPacket) throws Exception {

                if (rawPacket instanceof ServerboundInteractPacket packet) {

                    PetsAPI.runSync(() -> {
                        int entityId = packet.getEntityId();

                        var playerHandle = ((CraftPlayer) player).getHandle();
                        var world = playerHandle.level();

                        var nmsEntity = world.getEntity(entityId);
                        if (nmsEntity == null) return;

                        var bukkitEntity = nmsEntity.getBukkitEntity();

                        petService.performInteraction(bukkitEntity.getUniqueId(), player.getUniqueId());
                    }, 0L);
                }

                super.channelRead(ctx, rawPacket);
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        String handlerName = "shpets-" + player.getUniqueId();
        if (pipeline.get(handlerName) != null) {
            pipeline.remove(handlerName);
        }
        pipeline.addBefore("packet_handler", handlerName, channelHandler);
    }

    @Override
    public void stop (Player player){
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        String handlerName = "shpets-" + player.getUniqueId();
        channel.eventLoop().submit(() -> {
            if (channel.pipeline().get(handlerName) != null) {
                channel.pipeline().remove(handlerName);
            }
            return null;
        });
    }

}