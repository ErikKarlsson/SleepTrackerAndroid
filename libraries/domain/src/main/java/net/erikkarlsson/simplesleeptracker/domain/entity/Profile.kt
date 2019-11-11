package net.erikkarlsson.simplesleeptracker.domain.entity

data class Profile(val lastBackupTimestamp: Long) {

    companion object {
        fun empty() = Profile(0)
    }
}
