package net.erikkarlsson.simplesleeptracker.data

interface MapperCoroutines<E, D> {

    suspend fun mapFromEntity(type: E): D

    suspend fun mapToEntity(type: D): E

}
