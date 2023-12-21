package co.omise.android.config

import androidx.annotation.StyleRes

/**
 * Configuration for UI customization in the challenge flow.
 */
data class UiCustomization internal constructor(internal val uiCustomization: co.omise.android.threeds.customization.UiCustomization) {
    companion object {
        val default =
            UiCustomization(
                co.omise.android.threeds.customization.UiCustomization(
                    labelCustomization = co.omise.android.threeds.customization.LabelCustomization(),
                    toolbarCustomization = co.omise.android.threeds.customization.ToolbarCustomization(),
                    buttonCustomizations = emptyMap(),
                    textBoxCustomization = co.omise.android.threeds.customization.TextBoxCustomization(),
                ),
            )
    }

    class Builder {
        private var uiCustomization: UiCustomization = default
        private var buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> =
            mutableMapOf()

        /**
         * Set the label customization.
         *
         * @param labelCustomization Label customization data.
         */
        fun labelCustomization(labelCustomization: LabelCustomization): Builder =
            apply {
                uiCustomization =
                    uiCustomization.copy(
                        uiCustomization =
                            uiCustomization.uiCustomization.copy(
                                labelCustomization = labelCustomization.labelCustomization,
                            ),
                    )
            }

        /**
         * Set the text box customization.
         *
         * @param textBoxCustomization Text box customization data.
         */
        fun textBoxCustomization(textBoxCustomization: TextBoxCustomization): Builder =
            apply {
                uiCustomization =
                    uiCustomization.copy(
                        uiCustomization =
                            uiCustomization.uiCustomization.copy(
                                textBoxCustomization = textBoxCustomization.textBoxCustomization,
                            ),
                    )
            }

        /**
         * Set the toolbar customization.
         *
         * @param toolbarCustomization Toolbar customization data.
         */
        fun toolbarCustomization(toolbarCustomization: ToolbarCustomization): Builder =
            apply {
                uiCustomization =
                    uiCustomization.copy(
                        uiCustomization =
                            uiCustomization.uiCustomization.copy(
                                toolbarCustomization = toolbarCustomization.toolbarCustomization,
                            ),
                    )
            }

        /**
         * Set the button customization for the particular button.
         *
         * @param buttonType Type of button.
         * @param buttonCustomization Button customization data.
         */
        fun buttonCustomization(
            buttonType: ButtonType,
            buttonCustomization: ButtonCustomization,
        ): Builder =
            apply {
                buttonCustomizations[buttonType] = buttonCustomization
                uiCustomization =
                    uiCustomization.copy(
                        uiCustomization =
                            uiCustomization.uiCustomization.copy(
                                buttonCustomizations =
                                    buttonCustomizations.map {
                                        co.omise.android.threeds.customization.ButtonType.buttonTypeOf(it.key.value) to
                                            it.value.buttonCustomization
                                    }.toMap(),
                            ),
                    )
            }

        /**
         * Set the theme resource to override the default theme. The theme resource will not override the EditText and Button
         * because they are custom widgets. However you can you [TextBoxCustomization] and [ButtonCustomization] to customize those elements.
         *
         * @param theme Theme resource.
         */
        fun theme(
            @StyleRes theme: Int,
        ): Builder =
            apply {
                uiCustomization =
                    uiCustomization.copy(
                        uiCustomization =
                            uiCustomization.uiCustomization.copy(
                                theme = theme,
                            ),
                    )
            }

        /**
         * Create an instance of [UiCustomization].
         *
         * @return [UiCustomization]
         */
        fun build(): UiCustomization {
            return UiCustomization(uiCustomization.uiCustomization)
        }
    }

    /**
     * Configuration for label customization.
     */
    data class LabelCustomization internal constructor(
        internal val labelCustomization: co.omise.android.threeds.customization.LabelCustomization,
    ) {
        class Builder {
            private var labelCustomization =
                co.omise.android.threeds.customization.LabelCustomization()

            /**
             * Set the text font for texts.
             *
             * @param fontName Font path in the assets directory.
             */
            fun textFontName(fontName: String): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(textFontName = fontName)
                }

            /**
             * Set the text color for texts.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun textFontColor(hexColor: String): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(textFontColor = hexColor)
                }

            /**
             * Set the text size for texts.
             *
             * @param fontSize Font size in scalable pixels (sp).
             */
            fun textFontSize(fontSize: Int): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(textFontSize = fontSize)
                }

            /**
             * Set the text color for headers.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun headingTextColor(hexColor: String): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(headingTextColor = hexColor)
                }

            /**
             * Set the text font for headers.
             *
             * @param fontName Font path in the assets directory.
             */
            fun headingTextFontName(fontName: String): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(headingTextFontName = fontName)
                }

            /**
             * Set the text size for headers.
             *
             * @param fontSize Font size in scalable pixels (sp).
             */
            fun headingTextFontSize(fontSize: Int): Builder =
                apply {
                    labelCustomization = labelCustomization.copy(headingTextFontSize = fontSize)
                }

            /**
             * Create an instance of [LabelCustomization].
             *
             * @return [LabelCustomization]
             */
            fun build(): LabelCustomization {
                return LabelCustomization(labelCustomization)
            }
        }
    }

    /**
     * Configuration for Text box customization.
     */
    data class TextBoxCustomization internal constructor(
        internal val textBoxCustomization: co.omise.android.threeds.customization.TextBoxCustomization,
    ) {
        class Builder {
            private var textBoxCustomization =
                co.omise.android.threeds.customization.TextBoxCustomization()

            /**
             * Set the text font for the text box.
             *
             * @param fontName Font path in the assets directory.
             */
            fun textFontName(fontName: String): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(textFontName = fontName)
                }

            /**
             * Set the text color for the text box.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun textFontColor(hexColor: String): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(textFontColor = hexColor)
                }

            /**
             * Set the text size for the text box.
             *
             * @param fontSize Font size in scalable pixels (sp).
             */
            fun textFontSize(fontSize: Int): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(textFontSize = fontSize)
                }

            /**
             * Set the border width for the text box.
             *
             * @param borderWidth Border width in density-independent pixels (dp).
             */
            fun borderWidth(borderWidth: Int): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(borderWidth = borderWidth)
                }

            /**
             * Set the border color for the text box.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun borderColor(hexColor: String): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(borderColor = hexColor)
                }

            /**
             * Set the corner radius for the text box.
             *
             * @param cornerRadius Corner radius in density-independent pixels (dp).
             */
            fun cornerRadius(cornerRadius: Int): Builder =
                apply {
                    textBoxCustomization = textBoxCustomization.copy(cornerRadius = cornerRadius)
                }

            /**
             * Create an instance of [TextBoxCustomization].
             *
             * @return [TextBoxCustomization]
             */
            fun build(): TextBoxCustomization {
                return TextBoxCustomization(textBoxCustomization)
            }
        }
    }

    /**
     * Configuration for Toolbar customization.
     */
    data class ToolbarCustomization internal constructor(
        internal val toolbarCustomization: co.omise.android.threeds.customization.ToolbarCustomization,
    ) {
        class Builder {
            private var toolbarCustomization =
                co.omise.android.threeds.customization.ToolbarCustomization()

            /**
             * Set the text font for the toolbar's title and cancel button.
             *
             * @param fontName Font path in the assets directory.
             */
            fun textFontName(fontName: String): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(textFontName = fontName)
                }

            /**
             * Set the text color for the toolbar's title and cancel button.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun textFontColor(hexColor: String): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(textFontColor = hexColor)
                }

            /**
             * Set the text size for the toolbar's title.
             *
             * @param fontSize Font size in scalable pixels (sp).
             */
            fun textFontSize(fontSize: Int): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(textFontSize = fontSize)
                }

            /**
             * Set the color for the toolbar's background.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun backgroundColor(hexColor: String): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(backgroundColor = hexColor)
                }

            /**
             * Set the title text for toolbar's title.
             *
             * @param text Toolbar title's text.
             */
            fun headerText(text: String): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(headerText = text)
                }

            /**
             * Set the text for toolbar's cancel button.
             *
             * @param text Cancel button's text.
             */
            fun buttonText(text: String): Builder =
                apply {
                    toolbarCustomization = toolbarCustomization.copy(buttonText = text)
                }

            /**
             * Create an instance of [ToolbarCustomization].
             *
             * @return [ToolbarCustomization]
             */
            fun build(): ToolbarCustomization {
                return ToolbarCustomization(toolbarCustomization)
            }
        }
    }

    /**
     * Type of button in the challenge flow.
     */
    enum class ButtonType(val value: String) {
        SUBMIT_BUTTON("SUBMIT"),
        CONTINUE_BUTTON("CONTINUE"),
        NEXT_BUTTON("NEXT"),
        CANCEL_BUTTON("CANCEL"),
        RESEND_BUTTON("RESEND"),
    }

    /**
     * Configuration for button customization.
     */
    data class ButtonCustomization internal constructor(
        internal val buttonCustomization: co.omise.android.threeds.customization.ButtonCustomization,
    ) {
        class Builder {
            private var buttonCustomization =
                co.omise.android.threeds.customization.ButtonCustomization()

            /**
             * Set the text font for the toolbar's title and cancel button.
             *
             * @param fontName Font path in the assets directory.
             */
            fun textFontName(fontName: String): Builder =
                apply {
                    buttonCustomization = buttonCustomization.copy(textFontName = fontName)
                }

            /**
             * Set the color for the button text's color.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun textFontColor(hexColor: String): Builder =
                apply {
                    buttonCustomization = buttonCustomization.copy(textFontColor = hexColor)
                }

            /**
             * Set the text size for the button's text.
             *
             * @param fontSize Font size in scalable pixels (sp).
             */
            fun textFontSize(fontSize: Int): Builder =
                apply {
                    buttonCustomization = buttonCustomization.copy(textFontSize = fontSize)
                }

            /**
             * Set the color for the button's background.
             *
             * @param hexColor Color in hex format e.g. #FFFFFF
             */
            fun backgroundColor(hexColor: String): Builder =
                apply {
                    buttonCustomization = buttonCustomization.copy(backgroundColor = hexColor)
                }

            /**
             * Set the corner radius for the button.
             *
             * @param cornerRadius Corner radius in density-independent pixels (dp).
             */
            fun cornerRadius(cornerRadius: Int): Builder =
                apply {
                    buttonCustomization = buttonCustomization.copy(cornerRadius = cornerRadius)
                }

            /**
             * Create an instance of [ButtonCustomization].
             *
             * @return [ButtonCustomization].
             */
            fun build(): ButtonCustomization {
                return ButtonCustomization(buttonCustomization)
            }
        }
    }
}
