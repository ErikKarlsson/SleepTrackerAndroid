package net.erikkarlsson.simplesleeptracker.util

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableListMultimap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import io.reactivex.Flowable
import io.reactivex.Observable

fun <T> Observable<T>.toImmutableList() =
        collect( { ImmutableList.builder<T>() }, { b,t -> b.add(t) })
            .map { it.build() }

fun <T> Observable<T>.toImmutableSet() =
        collect( { ImmutableSet.builder<T>() }, { b,t -> b.add(t) })
            .map { it.build() }

fun <T,K> Observable<T>.toImmutableMap(keyMapper: (T) -> K) =
        collect( { ImmutableMap.builder<K,T>() }, { b,t -> b.put(keyMapper(t), t) })
            .map { it.build() }

fun <T,K,V> Observable<T>.toImmutableMap(keyMapper: (T) -> K, valueMapper: (T) -> V) =
        collect( { ImmutableMap.builder<K,V>() }, { b,t -> b.put(keyMapper(t), valueMapper(t)) })
            .map { it.build() }

fun <T,K> Observable<T>.toImmutableListMultimap(keyMapper: (T) -> K) =
        collect( { ImmutableListMultimap.builder<K,T>() }, { b,t -> b.put(keyMapper(t), t) })
            .map { it.build() }

fun <T,K,V> Observable<T>.toImmutableListMultimap(keyMapper: (T) -> K, valueMapper: (T) -> V) =
        collect( { ImmutableListMultimap.builder<K,V>() }, { b,t -> b.put(keyMapper(t), valueMapper(t)) })
            .map { it.build() }

fun <T> Flowable<T>.toImmutableList() =
        collect( { ImmutableList.builder<T>() }, { b,t -> b.add(t) })
            .map { it.build() }

fun <T> Flowable<T>.toImmutableSet() =
        collect( { ImmutableSet.builder<T>() }, { b,t -> b.add(t) })
            .map { it.build() }

fun <T,K> Flowable<T>.toImmutableMap(keyMapper: (T) -> K) =
        collect( { ImmutableMap.builder<K,T>() }, { b,t -> b.put(keyMapper(t), t) })
            .map { it.build() }

fun <T,K,V> Flowable<T>.toImmutableMap(keyMapper: (T) -> K, valueMapper: (T) -> V) =
        collect( { ImmutableMap.builder<K,V>() }, { b,t -> b.put(keyMapper(t), valueMapper(t)) })
            .map { it.build() }

fun <T,K> Flowable<T>.toImmutableListMultimap(keyMapper: (T) -> K) =
        collect( { ImmutableListMultimap.builder<K,T>() }, { b,t -> b.put(keyMapper(t), t) })
            .map { it.build() }

fun <T,K,V> Flowable<T>.toImmutableListMultimap(keyMapper: (T) -> K, valueMapper: (T) -> V) =
        collect( { ImmutableListMultimap.builder<K,V>() }, { b,t -> b.put(keyMapper(t), valueMapper(t)) })
            .map { it.build() }