package net.darkmeow.irc.data.sync

import java.util.UUID

data class DataSyncInputStatus(var publicInputs: Set<UUID> = mutableSetOf(), var privateInputs: Set<UUID> = mutableSetOf()) {

    fun shouldUpdate(newPublicInputs: Set<UUID>, newPrivateInputs: Set<UUID>): Boolean {
        return publicInputs != newPublicInputs || privateInputs != newPrivateInputs
    }

    fun update(newPublicInputs: Set<UUID>, newPrivateInputs: Set<UUID>) {
        publicInputs = newPublicInputs
        privateInputs = newPrivateInputs
    }

}
