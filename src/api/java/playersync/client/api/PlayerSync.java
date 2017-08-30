package playersync.client.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nonnull;

public final class PlayerSync {

    private static SyncManager manager;

    private PlayerSync() {
    }

    /**
     * <p>Sets the manager instance.</p>
     * <p>Internal. <b>DO NOT USE</b>
     *
     * @param manager
     * @throws IllegalStateException If it is already initialized
     * @throws NullPointerException  If manager is null
     */
    public static void setManager(SyncManager manager) {
        checkState(PlayerSync.manager == null, "PlayerSync already initialized");
        PlayerSync.manager = checkNotNull(manager);
    }

    /**
     * Gets the underlying manager instance.
     *
     * @return The manager
     */
    @Nonnull
    public static SyncManager getManager() {
        return checkNotNull(manager, "PlayerSync has not initialized yet. This is probably a bug.");
    }
}
