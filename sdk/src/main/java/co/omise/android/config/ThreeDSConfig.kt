package co.omise.android.config

import androidx.annotation.IntRange

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

        private var uiCustomization: UiCustomization = UiCustomization.default
        private var timeout: Int = DEFAULT_TIMEOUT

        fun uiCustomization(uiCustomization: UiCustomization): Builder = apply {
            this.uiCustomization = uiCustomization
        }

        fun timeout(@IntRange(from = 5, to = 99) timeout: Int): Builder = apply {
            this.timeout = timeout
        }

        fun build(): ThreeDSConfig {
            return ThreeDSConfig(co.omise.android.threeds.core.ThreeDSConfig(uiCustomization.uiCustomization, timeout))
        }
    }
}
