package playersync.client.api;

import net.minecraft.nbt.NBTTagCompound;

public interface SettingsHandler {

    void settingsReceived(String mod, NBTTagCompound tag);
}
