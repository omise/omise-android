package co.omise.android.config

import co.omise.android.threeds.customization.UiCustomization


class ThreeDSConfig(
        val uiCustomization: UiCustomization?,
        val timeout: Int?
) {

    class Builder {

        private var uiCustomization: UiCustomization? = null
        private var timeout: Int? = null

        fun uiCustomization(uiCustomization: UiCustomization): Builder {
            this.uiCustomization = uiCustomization
            return this
        }

        fun timeout(timeout: Int): Builder {
            this.timeout = timeout
            return this
        }

        fun build(): ThreeDSConfig {
            return ThreeDSConfig(uiCustomization, timeout)
        }
    }
}
