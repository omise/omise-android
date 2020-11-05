package co.omise.android.config

import co.omise.android.threeds.customization.UiCustomization


class ThreeDSConfig private constructor(internal val threeDSConfig: co.omise.android.threeds.core.ThreeDSConfig) {

    companion object {
        private var instance: ThreeDSConfig? = null
        internal val default: ThreeDSConfig = Builder().build()

        @JvmStatic
        fun get(): ThreeDSConfig = instance ?: default
    }

    class Builder {
        companion object {
            const val DEFAULT_TIMEOUT = 5
        }

        private var uiCustomization: UiCustomization? = null
        private var timeout: Int = DEFAULT_TIMEOUT

        fun uiCustomization(uiCustomization: UiCustomization): Builder = apply {
            this.uiCustomization = uiCustomization
        }

        fun timeout(timeout: Int): Builder = apply {
            this.timeout = timeout
        }

        fun build(): ThreeDSConfig {
            return ThreeDSConfig(co.omise.android.threeds.core.ThreeDSConfig(uiCustomization, timeout))
        }
    }
}
