package co.omise.android.config


object AuthorizingPaymentConfig {
    var threeDSConfig: ThreeDSConfig? = null
        private set

    @JvmStatic
    fun initialize(threeDSConfig: ThreeDSConfig) {
        this.threeDSConfig = threeDSConfig
    }
}