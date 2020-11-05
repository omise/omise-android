package co.omise.android.config


data class UiCustomization internal constructor(internal val uiCustomization: co.omise.android.threeds.customization.UiCustomization) {
    companion object {
        val default = UiCustomization(co.omise.android.threeds.customization.UiCustomization(
                labelCustomization = co.omise.android.threeds.customization.LabelCustomization(),
                toolbarCustomization = co.omise.android.threeds.customization.ToolbarCustomization(),
                buttonCustomizations = emptyMap(),
                textBoxCustomization = co.omise.android.threeds.customization.TextBoxCustomization()
        ))
    }

    class Builder {
        var uiCustomization: UiCustomization = default

        fun labelCustomization(labelCustomization: LabelCustomization): Builder = apply {
            uiCustomization = uiCustomization.copy(
                    uiCustomization = uiCustomization.uiCustomization.copy(
                            labelCustomization = labelCustomization.labelCustomization
                    )
            )
        }

        fun textBoxCustomization(textBoxCustomization: TextBoxCustomization): Builder = apply {
            uiCustomization = uiCustomization.copy(
                    uiCustomization = uiCustomization.uiCustomization.copy(
                            textBoxCustomization = textBoxCustomization.textBoxCustomization
                    )
            )
        }

        fun build(): UiCustomization {
            return UiCustomization(uiCustomization.uiCustomization)
        }
    }

    data class LabelCustomization internal constructor(internal val labelCustomization: co.omise.android.threeds.customization.LabelCustomization) {
        class Builder {
            private var labelCustomization = co.omise.android.threeds.customization.LabelCustomization()
            fun textFontName(fontName: String): Builder = apply {
                labelCustomization = labelCustomization.copy(textFontName = fontName)
            }

            fun textFontColor(hexColor: String): Builder = apply {
                labelCustomization = labelCustomization.copy(textFontColor = hexColor)
            }

            fun textFontSize(fontSize: Int): Builder = apply {
                labelCustomization = labelCustomization.copy(textFontSize = fontSize)
            }

            fun headingTextColor(hexColor: String): Builder = apply {
                labelCustomization = labelCustomization.copy(headingTextColor = hexColor)
            }

            fun headingTextFontName(fontName: String): Builder = apply {
                labelCustomization = labelCustomization.copy(headingTextFontName = fontName)
            }

            fun headingTextFontSize(fontSize: Int): Builder = apply {
                labelCustomization = labelCustomization.copy(headingTextFontSize = fontSize)
            }

            fun build(): LabelCustomization {
                return LabelCustomization(labelCustomization)
            }
        }
    }

    data class TextBoxCustomization internal constructor(internal val textBoxCustomization: co.omise.android.threeds.customization.TextBoxCustomization) {
        class Builder {
            private var textBoxCustomization = co.omise.android.threeds.customization.TextBoxCustomization()
            fun textFontName(fontName: String): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(textFontName = fontName)
            }

            fun textFontColor(hexColor: String): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(textFontColor = hexColor)
            }

            fun textFontSize(fontSize: Int): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(textFontSize = fontSize)
            }

            fun borderWidth(borderWidth: Int): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(borderWidth = borderWidth)
            }

            fun borderColor(hexColor: String): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(borderColor = hexColor)
            }

            fun cornerRadius(cornerRadius: Int): Builder = apply {
                textBoxCustomization = textBoxCustomization.copy(cornerRadius = cornerRadius)
            }

            fun build(): TextBoxCustomization {
                return TextBoxCustomization(textBoxCustomization)
            }
        }
    }

}