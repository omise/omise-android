package co.omise.android.config


class UiCustomization(val uiCustomization: co.omise.android.threeds.customization.UiCustomization) {
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

        fun labelCustomization(labelCustomization: LabelCustomization): Builder = apply{
            TODO("Not yet implemented")
        }

        fun build(): UiCustomization {
            TODO()
        }

    }

    data class LabelCustomization(private val labelCustomization: co.omise.android.threeds.customization.LabelCustomization) {
        class Builder {
            private var labelCustomization = co.omise.android.threeds.customization.LabelCustomization()
            fun textFontName(fontName: String): Builder = apply {
                labelCustomization = labelCustomization.copy(textFontName = fontName)
            }

            fun textFontColor(hexColor: String): Builder = apply {
                labelCustomization = labelCustomization.copy(textFontColor = hexColor)
            }

            fun textFontSize(fontSize: Int): Builder {
                TODO("Not yet implemented")
            }

            fun headingTextColor(s: String): Builder {
                TODO("Not yet implemented")
            }

            fun headingTextFontName(s: String): Builder {
                TODO("Not yet implemented")
            }

            fun headingTextFontSize(i: Int): Builder {
                TODO("Not yet implemented")
            }

            fun build(): LabelCustomization {
                return LabelCustomization(labelCustomization)
            }

        }
    }
}