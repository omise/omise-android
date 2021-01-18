package co.omise.android.config

/**
 * Configuration for authorizing payment process.
 */
class AuthorizingPaymentConfig private constructor(internal val threeDSConfig: ThreeDSConfig) {

    companion object {
        private var instance: AuthorizingPaymentConfig? = null
        private var default: AuthorizingPaymentConfig = Builder().threeDSConfig(ThreeDSConfig.default).build()

        /**
         * Initialize authorizing payment configuration. This function must be called before using [AuthorizingPaymentActivity].
         */
        @JvmStatic
        fun initialize(config: AuthorizingPaymentConfig) {
            instance = config
            co.omise.android.threeds.core.ThreeDSConfig.initialize(config.threeDSConfig.threeDSConfig)
        }

        /**
         * Get configuration.
         *
         * @return [AuthorizingPaymentConfig]
         */
        @JvmStatic
        fun get(): AuthorizingPaymentConfig = instance ?: default

        /**
         * Reset configuration.
         */
        @JvmStatic
        fun reset() {
            instance = null
        }
    }

    /**
     * Builder for building [AuthorizingPaymentConfig] data.
     */
    class Builder {
        private var threeDSConfig: ThreeDSConfig? = null

        /**
         * Configuration for 3DS
         *
         * @param threeDSConfig The 3DS config
         */
        fun threeDSConfig(threeDSConfig: ThreeDSConfig): Builder = apply {
            this.threeDSConfig = threeDSConfig
        }

        /**
         * Create an instance of [AuthorizingPaymentConfig].
         *
         * @return [AuthorizingPaymentConfig]
         */
        fun build(): AuthorizingPaymentConfig {
            return AuthorizingPaymentConfig(requireNotNull(threeDSConfig))
        }
    }
}