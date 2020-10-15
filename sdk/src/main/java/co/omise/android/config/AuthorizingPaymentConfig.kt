package co.omise.android.config


object AuthorizingPaymentConfig {
    private var threeDSConfig: ThreeDSConfig? = null

    @JvmStatic
    fun initialize(threeDSConfig: ThreeDSConfig) {
        this.threeDSConfig = threeDSConfig
    }
}