package co.omise.android.config

import co.omise.android.threeds.core.ThreeDSConfig


object AuthorizingPaymentConfig {
    var threeDSConfig: ThreeDSConfig? = null
        private set

    @JvmStatic
    fun initialize(threeDSConfig: ThreeDSConfig) {
        this.threeDSConfig = threeDSConfig
    }
}