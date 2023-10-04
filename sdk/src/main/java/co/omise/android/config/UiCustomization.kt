package co.omise.android.config


abstract class CustomizationBuilder<T> {
    protected var textFontSize: Int? = null
    protected var textColor: String? = null
    protected var textFontName: String? = null
    protected var darkTextColor: String? = null
    abstract fun build(): T
}

/**
 * configuration for the challenge screen.
 */
data class UiCustomization internal constructor(internal val uiCustomization: com.netcetera.threeds.sdk.api.ui.logic.UiCustomization) {
    companion object {
        val default = UiCustomization(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization())
    }
}

/**
 * Builder for building [UiCustomization] data.
 */
class UiCustomizationBuilder {
    private var labelCustomization: LabelCustomization? = null
    private var toolbarCustomization: ToolbarCustomization? = null
    private var textBoxCustomization: TextBoxCustomization? = null
    private var buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()

    /**
     * Set the label customization.
     *
     * @param labelCustomization Label customization data.
     */
    fun labelCustomization(labelCustomization: LabelCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.labelCustomization = labelCustomization
    }

    /**
     * Set the text box customization.
     *
     * @param textBoxCustomization Text box customization data.
     */
    fun textBoxCustomization(textBoxCustomization: TextBoxCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.textBoxCustomization = textBoxCustomization
    }

    /**
     * Set the toolbar customization.
     *
     * @param toolbarCustomization Toolbar customization data.
     */
    fun toolbarCustomization(toolbarCustomization: ToolbarCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.toolbarCustomization = toolbarCustomization
    }

    /**
     * Set the button customization for the particular button.
     *
     * @param buttonType Type of button.
     * @param buttonCustomization Button customization data.
     */
    fun buttonCustomization(buttonType: ButtonType, buttonCustomization: ButtonCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.buttonCustomizations[buttonType] = buttonCustomization
    }


    /**
     * Create an instance of [UiCustomization].
     *
     * @return [UiCustomization]
     */
    fun build(): UiCustomization {
        val uiCustomization = com.netcetera.threeds.sdk.api.ui.logic.UiCustomization().apply {
            labelCustomization?.let { this.labelCustomization = it }
            toolbarCustomization?.let { this.toolbarCustomization = it }
            textBoxCustomization?.let { this.textBoxCustomization = it }
            buttonCustomizations.forEach { (buttonType, buttonCustomization) ->
                this.setButtonCustomization(buttonCustomization.buttonCustomization, buttonType.value)
            }
        }
        return UiCustomization(uiCustomization)
    }
}

/**
 * Configuration for label customization.
 */
data class LabelCustomization internal constructor(internal val labelCustomization: com.netcetera.threeds.sdk.api.ui.logic.LabelCustomization)

class LabelCustomizationBuilder : CustomizationBuilder<LabelCustomization>() {
    private var headingTextColor: String? = null
    private var headingTextFontName: String? = null
    private var headingTextFontSize: Int? = null
    private var headingDarkTextColor: String? = null

    fun headingTextColor(headingTextColor: String): LabelCustomizationBuilder = apply { this.headingTextColor = headingTextColor }
    fun headingTextFontName(headingTextFontName: String): LabelCustomizationBuilder =
        apply { this.headingTextFontName = headingTextFontName }

    fun headingTextFontSize(headingTextFontSize: Int): LabelCustomizationBuilder =
        apply { this.headingTextFontSize = headingTextFontSize }

    fun headingDarkTextColor(headingDarkTextColor: String): LabelCustomizationBuilder =
        apply { this.headingDarkTextColor = headingDarkTextColor }

    fun textColor(textColor: String): LabelCustomizationBuilder = apply { this.textColor = textColor }
    fun textFontSize(textFontSize: Int): LabelCustomizationBuilder = apply { this.textFontSize = textFontSize }
    fun textFontName(textFontName: String): LabelCustomizationBuilder = apply { this.textFontName = textFontName }
    fun darkTextColor(darkTextColor: String): LabelCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    override fun build(): LabelCustomization {
        return LabelCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.LabelCustomization().apply {
                this@LabelCustomizationBuilder.headingTextColor?.let { this.headingTextColor = it }
                this@LabelCustomizationBuilder.headingTextFontName?.let { this.headingTextFontName = it }
                this@LabelCustomizationBuilder.headingTextFontSize?.let { this.headingTextFontSize = it }
                this@LabelCustomizationBuilder.headingDarkTextColor?.let { this.headingDarkTextColor = it }
                this@LabelCustomizationBuilder.textColor?.let { this.textColor = it }
                this@LabelCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@LabelCustomizationBuilder.textFontName?.let { this.textFontName = it }
                this@LabelCustomizationBuilder.darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}

/**
 * Configuration for Text box customization.
 */
data class TextBoxCustomization internal constructor(
    internal val textBoxCustomization: com.netcetera.threeds.sdk.api.ui.logic.TextBoxCustomization
)

class TextBoxCustomizationBuilder : CustomizationBuilder<TextBoxCustomization>() {
    private var borderWidth: Int? = null
    private var borderColor: String? = null
    private var cornerRadius: Int? = null
    private var darkBorderColor: String? = null

    fun borderWidth(borderWidth: Int): TextBoxCustomizationBuilder = apply { this.borderWidth = borderWidth }
    fun borderColor(borderColor: String): TextBoxCustomizationBuilder = apply { this.borderColor = borderColor }
    fun cornerRadius(cornerRadius: Int): TextBoxCustomizationBuilder = apply { this.cornerRadius = cornerRadius }
    fun darkBorderColor(darkBorderColor: String): TextBoxCustomizationBuilder = apply { this.darkBorderColor = darkBorderColor }
    fun textColor(textColor: String): TextBoxCustomizationBuilder = apply { this.textColor = textColor }
    fun textFontSize(textFontSize: Int): TextBoxCustomizationBuilder = apply { this.textFontSize = textFontSize }
    fun textFontName(textFontName: String): TextBoxCustomizationBuilder = apply { this.textFontName = textFontName }
    fun darkTextColor(darkTextColor: String): TextBoxCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    override fun build(): TextBoxCustomization {
        return TextBoxCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.TextBoxCustomization().apply {
                borderWidth.let { this.borderWidth = it }
                borderColor?.let { this.borderColor = it }
                cornerRadius.let { this.cornerRadius = it }
                darkBorderColor?.let { this.darkBorderColor = it }
                textColor?.let { this.textColor = it }
                textFontSize.let { this.textFontSize = it }
                textFontName?.let { this.textFontName = it }
                darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}

/**
 * Configuration for Toolbar customization.
 */
data class ToolbarCustomization internal constructor(internal val toolbarCustomization: com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization)

class ToolbarCustomizationBuilder : CustomizationBuilder<ToolbarCustomization>() {
    private var headText: String? = null
    private var buttonText: String? = null
    private var backgroundColor: String? = null
    private var darkBackgroundColor: String? = null

    fun headText(headText: String): ToolbarCustomizationBuilder = apply { this.headText = headText }
    fun buttonText(buttonText: String): ToolbarCustomizationBuilder = apply { this.buttonText = buttonText }
    fun backgroundColor(backgroundColor: String): ToolbarCustomizationBuilder = apply { this.backgroundColor = backgroundColor }
    fun darkBackgroundColor(darkBackgroundColor: String): ToolbarCustomizationBuilder =
        apply { this.darkBackgroundColor = darkBackgroundColor }

    fun textColor(textColor: String): ToolbarCustomizationBuilder = apply { this.textColor = textColor }
    fun textFontSize(textFontSize: Int): ToolbarCustomizationBuilder = apply { this.textFontSize = textFontSize }
    fun textFontName(textFontName: String): ToolbarCustomizationBuilder = apply { this.textFontName = textFontName }
    fun darkTextColor(darkTextColor: String): ToolbarCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    override fun build(): ToolbarCustomization {
        return ToolbarCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization().apply {
                headText?.let { this.headerText = it }
                buttonText?.let { this.buttonText = it }
                backgroundColor?.let { this.backgroundColor = it }
                darkBackgroundColor?.let { this.darkBackgroundColor = it }
                textColor?.let { this.textColor = it }
                textFontSize.let { this.textFontSize = it }
                textFontName?.let { this.textFontName = it }
                darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}

/**
 * Type of button in the challenge flow.
 */
enum class ButtonType(val value: com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType) {
    SUBMIT(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.SUBMIT),
    CONTINUE(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CONTINUE),
    NEXT(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.NEXT),
    CANCEL(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CANCEL),
    RESEND(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.RESEND),
    OPEN_OOB_APP(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.OPEN_OOB_APP);
}

/**
 * Configuration for button customization.
 */
data class ButtonCustomization internal constructor(internal val buttonCustomization: com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization)

class ButtonCustomizationBuilder : CustomizationBuilder<ButtonCustomization>() {
    private var cornerRadius: Int? = null
    private var backgroundColor: String? = null
    private var darkBackgroundColor: String? = null

    fun cornerRadius(cornerRadius: Int): ButtonCustomizationBuilder = apply { this.cornerRadius = cornerRadius }
    fun backgroundColor(backgroundColor: String): ButtonCustomizationBuilder = apply { this.backgroundColor = backgroundColor }
    fun darkBackgroundColor(darkBackgroundColor: String): ButtonCustomizationBuilder =
        apply { this.darkBackgroundColor = darkBackgroundColor }

    fun textColor(textColor: String): ButtonCustomizationBuilder = apply { this.textColor = textColor }
    fun textFontSize(textFontSize: Int): ButtonCustomizationBuilder = apply { this.textFontSize = textFontSize }
    fun textFontName(textFontName: String): ButtonCustomizationBuilder = apply { this.textFontName = textFontName }
    fun darkTextColor(darkTextColor: String): ButtonCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    override fun build(): ButtonCustomization {
        return ButtonCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization().apply {
                cornerRadius.let { this.cornerRadius = it }
                backgroundColor?.let { this.backgroundColor = it }
                darkBackgroundColor?.let { this.darkBackgroundColor = it }
                textColor?.let { this.textColor = it }
                textFontSize.let { this.textFontSize = it }
                textFontName?.let { this.textFontName = it }
                darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}
