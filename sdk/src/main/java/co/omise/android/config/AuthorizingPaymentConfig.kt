package co.omise.android.config

import co.omise.android.threeds.core.ThreeDSConfig


object AuthorizingPaymentConfig {
    @JvmStatic
    fun initialize(threeDSConfig: ThreeDSConfig) {
        ThreeDSConfig.initialize(threeDSConfig)
    }
}