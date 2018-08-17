package com.publicobject.experiments

import okio.Buffer
import okio.ByteString
import kotlin.reflect.KClass

class Experimenter {
  private val experiments: MutableMap<KClass<out Enum<*>>, ExperimentSnapshot<*>> = mutableMapOf()

  fun <T : Enum<T>> configureExperiment(
    experiment: KClass<T>,
    bucketToSize: List<Pair<T, Int>>,
    overrides: Map<String, T> = mapOf()
  ) {
    val totalSize = bucketToSize.map { it.second }.reduce(Int::plus)
    val biggest = bucketToSize.maxBy { it.second }!!

    experiments[experiment] = ExperimentSnapshot(
        experiment.java.name,
        totalSize,
        biggest.first,
        overrides,
        bucketToSize
    )
  }

  inline fun <reified T : Enum<T>> getBucket(identifier: String? = null) = getBucket(T::class,
      identifier)

  fun <T : Enum<T>> getBucket(experiment: KClass<T>, identifier: String? = null): T {
    val snapshot = experiments[experiment] ?: return experiment.java.enumConstants[0]
    return experiment.java.cast(snapshot.getBucket(identifier))
  }

  private data class ExperimentSnapshot<T : Enum<T>>(
    val name: String,
    val totalSize: Int,
    val defaultBucket: T,
    val overrides: Map<String, T>,
    val bucketToSize: List<Pair<T, Int>>
  ) {
    fun getBucket(identifier: String? = null): T {
      if (identifier == null) return defaultBucket

      val override = overrides[identifier]
      if (override != null) return override

      val hash = ByteString.encodeUtf8("$name:$identifier").sha256()
      val targetOffset = (Buffer().write(hash).readInt() and 0x7fffffff) % totalSize

      var cumulativeOffset = 0
      for ((bucket, bucketSize) in bucketToSize) {
        cumulativeOffset += bucketSize
        if (cumulativeOffset > targetOffset) return bucket
      }

      throw AssertionError()
    }
  }
}