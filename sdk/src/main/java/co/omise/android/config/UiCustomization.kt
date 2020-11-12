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
        private var uiCustomization: UiCustomization = default
        private var buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()

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

        fun toolbarCustomization(toolbarCustomization: ToolbarCustomization): Builder = apply {
            uiCustomization = uiCustomization.copy(
                    uiCustomization = uiCustomization.uiCustomization.copy(
                            toolbarCustomization = toolbarCustomization.toolbarCustomization
                    )
            )
        }

        fun buttonCustomization(submitButton: ButtonType, buttonCustomization: ButtonCustomization): Builder = apply {
            buttonCustomizations[submitButton] = buttonCustomization
            uiCustomization = uiCustomization.copy(
                    uiCustomization = uiCustomization.uiCustomization.copy(
                            buttonCustomizations = buttonCustomizations.map { co.omise.android.threeds.customization.ButtonType.buttonTypeOf(it.key.value) to it.value.buttonCustomization }.toMap()
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

    data class ToolbarCustomization internal constructor(internal val toolbarCustomization: co.omise.android.threeds.customization.ToolbarCustomization) {
        class Builder {
            private var toolbarCustomization = co.omise.android.threeds.customization.ToolbarCustomization()
            fun textFontName(fontName: String): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(textFontName = fontName)
            }

            fun textFontColor(hexColor: String): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(textFontColor = hexColor)
            }

            fun textFontSize(fontSize: Int): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(textFontSize = fontSize)
            }

            fun backgroundColor(hexColor: String): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(backgroundColor = hexColor)
            }

            fun headerText(title: String): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(headerText = title)
            }

            fun buttonText(title: String): Builder = apply {
                toolbarCustomization = toolbarCustomization.copy(buttonText = title)
            }

            fun build(): ToolbarCustomization {
                return ToolbarCustomization(toolbarCustomization)
            }
        }
    }

    enum class ButtonType(val value: String) {
        SUBMIT_BUTTON("SUBMIT"),
        CONTINUE_BUTTON("CONTINUE"),
        NEXT_BUTTON("NEXT"),
        CANCEL_BUTTON("CANCEL"),
        RESEND_BUTTON("RESEND")
    }

    data class ButtonCustomization internal constructor(internal val buttonCustomization: co.omise.android.threeds.customization.ButtonCustomization) {
        class Builder {
            private var buttonCustomization = co.omise.android.threeds.customization.ButtonCustomization()
            fun textFontName(fontName: String): Builder = apply {
                buttonCustomization = buttonCustomization.copy(textFontName = fontName)
            }

            fun textFontColor(hexColor: String): Builder = apply {
                buttonCustomization = buttonCustomization.copy(textFontColor = hexColor)
            }

            fun textFontSize(fontSize: Int): Builder = apply {
                buttonCustomization = buttonCustomization.copy(textFontSize = fontSize)
            }

            fun backgroundColor(hexColor: String): Builder = apply {
                buttonCustomization = buttonCustomization.copy(backgroundColor = hexColor)
            }

            fun cornerRadius(cornerRadius: Int): Builder = apply {
                buttonCustomization = buttonCustomization.copy(cornerRadius = cornerRadius)
            }

            fun build(): ButtonCustomization {
                return ButtonCustomization(buttonCustomization)
            }
        }
    }
}
