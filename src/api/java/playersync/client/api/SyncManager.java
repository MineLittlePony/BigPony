package playersync.client.api;

public interface SyncManager {


    <T> void sendPacket(String channel, T data);

    <T> void register(String channel, PacketSerializer<T> serializer, ChannelHandler<T> handler, T instance);
}
