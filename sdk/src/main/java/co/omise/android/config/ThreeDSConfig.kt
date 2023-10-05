package co.omise.android.config

import androidx.annotation.IntRange

/**
 * Configuration for 3DS2.
 */
class ThreeDSConfig private constructor(internal val threeDSConfig: co.omise.android.threeds.core.ThreeDSConfig) {

    companion object {
        private var instance: ThreeDSConfig? = null
        internal val default: ThreeDSConfig = Builder().build()

        /**
         * Get the instance of [ThreeDSConfig].
         *
         * @return [ThreeDSConfig]
         */
        @JvmStatic
        fun get(): ThreeDSConfig = instance ?: default
    }

    /**
     * Builder for building [ThreeDSConfig].
     */
    class Builder {
        companion object {
            private const val DEFAULT_TIMEOUT = 5
        }

        private var uiCustomization: UiCustomization = UiCustomization.default
        private var timeout: Int = DEFAULT_TIMEOUT

        /**
         * Configuration for UI customization in the challenge flow.
         *
         * @param uiCustomization UI customization data.
         */
        fun uiCustomization(uiCustomization: UiCustomization): Builder = apply {
            this.uiCustomization = uiCustomization
        }

        /**
         * Maximum timeout for the challenge flow.
         *
         * @param timeout Timeout in minute. The acceptable timeout is 5-99 mins.
         */
        fun timeout(@IntRange(from = 5, to = 99) timeout: Int): Builder = apply {
            this.timeout = timeout
        }

        /**
         * Build an instance of [ThreeDSConfig].
         *
         * @return [ThreeDSConfig]
         */
        fun build(): ThreeDSConfig {
            TODO("The below is not compatible with Netcetera's 3DS SDK.")
            // return ThreeDSConfig(co.omise.android.threeds.core.ThreeDSConfig(uiCustomization.uiCustomization, timeout))
        }
    }
}
