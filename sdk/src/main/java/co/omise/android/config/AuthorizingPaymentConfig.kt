package co.omise.android.config


class AuthorizingPaymentConfig private constructor(internal val threeDSConfig: ThreeDSConfig) {

    companion object {
        private var instance: AuthorizingPaymentConfig? = null
        private var default: AuthorizingPaymentConfig = Builder().threeDSConfig(ThreeDSConfig.default).build()

        @JvmStatic
        fun initialize(config: AuthorizingPaymentConfig) {
            instance = config
            co.omise.android.threeds.core.ThreeDSConfig.initialize(config.threeDSConfig.threeDSConfig)
        }

        @JvmStatic
        fun get(): AuthorizingPaymentConfig = instance ?: default

        @JvmStatic
        fun reset() {
            instance = null
        }
    }

    class Builder {
        private var threeDSConfig: ThreeDSConfig? = null
        fun threeDSConfig(threeDSConfig: ThreeDSConfig): Builder = apply {
            this.threeDSConfig = threeDSConfig
        }

        fun build(): AuthorizingPaymentConfig {
            return AuthorizingPaymentConfig(requireNotNull(threeDSConfig))
        }
    }
}