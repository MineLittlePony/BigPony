package playersync.client.api;

import java.util.UUID;

/**
 * Created by Matthew on 12/6/2016.
 */
public interface ChannelHandler<T> {

    void handle(String chan, UUID uuid, T obj);
}
