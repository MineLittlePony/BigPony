package playersync.client.api;

import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public interface PacketSerializer<T> {

    void serialize(@Nonnull T object, @Nonnull PacketBuffer packetBuffer);

    @Nonnull
    T deserialize(PacketBuffer packetBuffer);

    PacketSerializer<PacketBuffer> RAW = new PacketSerializer<PacketBuffer>() {
        @Override
        public void serialize(PacketBuffer object, PacketBuffer packetBuffer) {
            packetBuffer.writeBytes(object);
        }

        @Override
        public PacketBuffer deserialize(PacketBuffer packetBuffer) {
            return new PacketBuffer(packetBuffer.copy());
        }
    };
}
