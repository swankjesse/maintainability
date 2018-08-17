package com.publicobject.experiments

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExperimenterTest {
  @Test
  fun unconfiguredExperimenterReturnsFirstConstant() {
    val experimenter = Experimenter()
    assertThat(experimenter.getBucket<BiometricAuthentication>())
        .isEqualTo(BiometricAuthentication.NONE)
    assertThat(experimenter.getBucket<BiometricAuthentication>("jesse"))
        .isEqualTo(BiometricAuthentication.NONE)
    assertThat(experimenter.getBucket<BiometricAuthentication>("matt"))
        .isEqualTo(BiometricAuthentication.NONE)
  }

  @Test
  fun singleOptionExperimenterAlwaysReturnsThatOption() {
    val experimenter = Experimenter()
    experimenter.configureExperiment(
        BiometricAuthentication::class,
        listOf(BiometricAuthentication.FINGERPRINT to 100))

    assertThat(experimenter.getBucket<BiometricAuthentication>())
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("jesse"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("alec"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
  }

  @Test
  fun multipleOptionExperimenterReturnsBothOptions() {
    val experimenter = Experimenter()
    experimenter.configureExperiment(
        BiometricAuthentication::class,
        listOf(
            BiometricAuthentication.FINGERPRINT to 50,
            BiometricAuthentication.EYEBALL to 50))

    assertThat(experimenter.getBucket<BiometricAuthentication>())
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("jesse"))
        .isEqualTo(BiometricAuthentication.EYEBALL)
    assertThat(experimenter.getBucket<BiometricAuthentication>("alec"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
  }

  @Test
  fun multipleOptionBiasedExperimenter() {
    val experimenter = Experimenter()
    experimenter.configureExperiment(
        BiometricAuthentication::class,
        listOf(
            BiometricAuthentication.FINGERPRINT to 90,
            BiometricAuthentication.EYEBALL to 10))

    assertThat(experimenter.getBucket<BiometricAuthentication>("g"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("h"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("i"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("j"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("k"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("l"))
        .isEqualTo(BiometricAuthentication.EYEBALL)
    assertThat(experimenter.getBucket<BiometricAuthentication>("m"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("n"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("o"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("p"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
  }

  @Test
  fun experimentOverrides() {
    val experimenter = Experimenter()
    experimenter.configureExperiment(
        BiometricAuthentication::class,
        listOf(BiometricAuthentication.FINGERPRINT to 100),
        mapOf("jake" to BiometricAuthentication.EYEBALL))

    assertThat(experimenter.getBucket<BiometricAuthentication>("jesse"))
        .isEqualTo(BiometricAuthentication.FINGERPRINT)
    assertThat(experimenter.getBucket<BiometricAuthentication>("jake"))
        .isEqualTo(BiometricAuthentication.EYEBALL)
  }

  enum class BiometricAuthentication {
    NONE,
    FINGERPRINT,
    EYEBALL
  }
}