package net.erikkarlsson.simplesleeptracker.data

interface Mapper<E, D> {

    fun mapFromEntity(type: E): D

    fun mapToEntity(type: D): E

}