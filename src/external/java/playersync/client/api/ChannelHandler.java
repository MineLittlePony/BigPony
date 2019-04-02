package playersync.client.api;

import java.util.UUID;

public interface ChannelHandler<T> {

    void handle(String chan, UUID uuid, T obj);
}
