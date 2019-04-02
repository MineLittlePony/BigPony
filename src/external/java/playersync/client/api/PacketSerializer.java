package playersync.client.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import javax.annotation.Nonnull;

public interface PacketSerializer<T> {

    void serialize(@Nonnull T object, @Nonnull PacketBuffer packetBuffer) throws IOException;

    @Nonnull
    T deserialize(PacketBuffer packetBuffer) throws IOException;

    PacketSerializer<PacketBuffer> RAW = new PacketSerializer<PacketBuffer>() {
        @Override
        public void serialize(@Nonnull PacketBuffer object, @Nonnull PacketBuffer packetBuffer) throws IOException {
            packetBuffer.writeBytes(object);
        }

        @Nonnull
        @Override
        public PacketBuffer deserialize(PacketBuffer packetBuffer) throws IOException {
            return new PacketBuffer(packetBuffer.copy());
        }
    };

    PacketSerializer<NBTTagCompound> NBT = new PacketSerializer<NBTTagCompound>() {
        @Override
        public void serialize(@Nonnull NBTTagCompound object, @Nonnull PacketBuffer packetBuffer) throws IOException {
            packetBuffer.writeCompoundTag(object);
        }

        @Nonnull
        @Override
        public NBTTagCompound deserialize(PacketBuffer packetBuffer) throws IOException {
            NBTTagCompound tag = packetBuffer.readCompoundTag();
            return tag == null ? new NBTTagCompound() : tag;
        }
    };
}
