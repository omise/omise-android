package co.omise.android.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


abstract class CustomizationBuilder<T> {
    protected var textFontSize: Int? = null
    protected var textColor: String? = null
    protected var textFontName: String? = null
    protected var darkTextColor: String? = null
    abstract fun build(): T
}

/**
 * Configuration for UI customization.
 */
@Parcelize
data class UiCustomization internal constructor(
    internal val uiCustomization: com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
) : Parcelable {
    companion object {
        val default = UiCustomization(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization())
    }
}

/**
 * Builder for building [UiCustomization] data.
 */
class UiCustomizationBuilder {
    private var supportDarkMode: Boolean? = null
    private var labelCustomization: LabelCustomization? = null
    private var toolbarCustomization: ToolbarCustomization? = null
    private var textBoxCustomization: TextBoxCustomization? = null
    private var buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()

    /**
     * Set whether the UI should support dark mode.
     *
     * @param supportDarkMode True if the UI should support dark mode, false otherwise.
     */
    fun supportDarkMode(supportDarkMode: Boolean): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.supportDarkMode = supportDarkMode
    }

    /**
     * Set the label customization.
     *
     * @param labelCustomization [LabelCustomization] data.
     */
    fun labelCustomization(labelCustomization: LabelCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.labelCustomization = labelCustomization
    }

    /**
     * Set the text box customization.
     *
     * @param textBoxCustomization [TextBoxCustomization] data.
     */
    fun textBoxCustomization(textBoxCustomization: TextBoxCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.textBoxCustomization = textBoxCustomization
    }

    /**
     * Set the toolbar customization.
     *
     * @param toolbarCustomization [ToolbarCustomization] data.
     */
    fun toolbarCustomization(toolbarCustomization: ToolbarCustomization): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.toolbarCustomization = toolbarCustomization
    }

    /**
     * Set the button customization for the particular button.
     *
     * @param buttonType [ButtonType] type of button.
     * @param buttonCustomization [ButtonCustomization] data.
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
            this@UiCustomizationBuilder.supportDarkMode?.let { this.supportDarkMode(it) }
            this@UiCustomizationBuilder.labelCustomization?.let { this.labelCustomization = it.labelCustomization }
            this@UiCustomizationBuilder.toolbarCustomization?.let { this.toolbarCustomization = it.toolbarCustomization }
            this@UiCustomizationBuilder.textBoxCustomization?.let { this.textBoxCustomization = it.textBoxCustomization }
            this@UiCustomizationBuilder.buttonCustomizations.forEach { (buttonType, buttonCustomization) ->
                this.setButtonCustomization(buttonCustomization.buttonCustomization, buttonType.value)
            }
        }
        return UiCustomization(uiCustomization)
    }
}

/**
 * Configuration for label customization.
 */
@Parcelize
data class LabelCustomization internal constructor(
    internal val labelCustomization: com.netcetera.threeds.sdk.api.ui.logic.LabelCustomization
) : Parcelable

/**
 * Builder for building [LabelCustomization] data.
 */
class LabelCustomizationBuilder : CustomizationBuilder<LabelCustomization>() {
    private var headingTextColor: String? = null
    private var headingTextFontName: String? = null
    private var headingTextFontSize: Int? = null
    private var headingDarkTextColor: String? = null

    /**
     * Set the heading text color.
     *
     * @param headingTextColor Color in hex format.
     */
    fun headingTextColor(headingTextColor: String): LabelCustomizationBuilder = apply { this.headingTextColor = headingTextColor }

    /**
     * Set the heading text font name.
     *
     * @param headingTextFontName Font name.
     */
    fun headingTextFontName(headingTextFontName: String): LabelCustomizationBuilder =
        apply { this.headingTextFontName = headingTextFontName }

    /**
     * Set the heading text font size.
     *
     * @param headingTextFontSize Font size in sp unit.
     */
    fun headingTextFontSize(headingTextFontSize: Int): LabelCustomizationBuilder =
        apply { this.headingTextFontSize = headingTextFontSize }

    /**
     * Set the heading dark text color.
     *
     * @param headingDarkTextColor Color in hex format.
     */
    fun headingDarkTextColor(headingDarkTextColor: String): LabelCustomizationBuilder =
        apply { this.headingDarkTextColor = headingDarkTextColor }

    /**
     * Set the text color.
     *
     * @param textColor Color in hex format.
     */
    fun textColor(textColor: String): LabelCustomizationBuilder = apply { this.textColor = textColor }

    /**
     * Set the text font size.
     *
     * @param textFontSize Font size in sp unit.
     */
    fun textFontSize(textFontSize: Int): LabelCustomizationBuilder = apply { this.textFontSize = textFontSize }

    /**
     * Set the text font name.
     *
     * @param textFontName Font name.
     */
    fun textFontName(textFontName: String): LabelCustomizationBuilder = apply { this.textFontName = textFontName }

    /**
     * Set the dark text color.
     *
     * @param darkTextColor Color in hex format.
     */
    fun darkTextColor(darkTextColor: String): LabelCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    /**
     * Create an instance of [LabelCustomization].
     *
     * @return [LabelCustomization]
     */
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
@Parcelize
data class TextBoxCustomization internal constructor(
    internal val textBoxCustomization: com.netcetera.threeds.sdk.api.ui.logic.TextBoxCustomization
) : Parcelable

/**
 * Builder for building [TextBoxCustomization] data.
 */
class TextBoxCustomizationBuilder : CustomizationBuilder<TextBoxCustomization>() {
    private var borderWidth: Int? = null
    private var borderColor: String? = null
    private var cornerRadius: Int? = null
    private var darkBorderColor: String? = null

    /**
     * Set the border width.
     *
     * @param borderWidth Border width in dp unit.
     */
    fun borderWidth(borderWidth: Int): TextBoxCustomizationBuilder = apply { this.borderWidth = borderWidth }

    /**
     * Set the border color.
     *
     * @param borderColor Color in hex format.
     */
    fun borderColor(borderColor: String): TextBoxCustomizationBuilder = apply { this.borderColor = borderColor }

    /**
     * Set the corner radius.
     *
     * @param cornerRadius Corner radius in dp unit.
     */
    fun cornerRadius(cornerRadius: Int): TextBoxCustomizationBuilder = apply { this.cornerRadius = cornerRadius }

    /**
     * Set the dark border color.
     *
     * @param darkBorderColor Color in hex format.
     */
    fun darkBorderColor(darkBorderColor: String): TextBoxCustomizationBuilder = apply { this.darkBorderColor = darkBorderColor }

    /**
     * Set the text color.
     *
     * @param textColor Color in hex format.
     */
    fun textColor(textColor: String): TextBoxCustomizationBuilder = apply { this.textColor = textColor }

    /**
     * Set the text font size.
     *
     * @param textFontSize Font size in sp unit.
     */
    fun textFontSize(textFontSize: Int): TextBoxCustomizationBuilder = apply { this.textFontSize = textFontSize }

    /**
     * Set the text font name.
     *
     * @param textFontName Font name.
     */
    fun textFontName(textFontName: String): TextBoxCustomizationBuilder = apply { this.textFontName = textFontName }

    /**
     * Set the dark text color.
     *
     * @param darkTextColor Color in hex format.
     */
    fun darkTextColor(darkTextColor: String): TextBoxCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    /**
     * Create an instance of [TextBoxCustomization].
     *
     * @return [TextBoxCustomization]
     */
    override fun build(): TextBoxCustomization {
        return TextBoxCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.TextBoxCustomization().apply {
                this@TextBoxCustomizationBuilder.borderWidth?.let { this.borderWidth = it }
                this@TextBoxCustomizationBuilder.borderColor?.let { this.borderColor = it }
                this@TextBoxCustomizationBuilder.cornerRadius?.let { this.cornerRadius = it }
                this@TextBoxCustomizationBuilder.darkBorderColor?.let { this.darkBorderColor = it }
                this@TextBoxCustomizationBuilder.textColor?.let { this.textColor = it }
                this@TextBoxCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@TextBoxCustomizationBuilder.textFontName?.let { this.textFontName = it }
                this@TextBoxCustomizationBuilder.darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}

/**
 * Configuration for Toolbar customization.
 */
@Parcelize
data class ToolbarCustomization internal constructor(
    internal val toolbarCustomization: com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization
) : Parcelable

/**
 * Builder for building [ToolbarCustomization] data.
 */
class ToolbarCustomizationBuilder : CustomizationBuilder<ToolbarCustomization>() {
    private var headText: String? = null
    private var buttonText: String? = null
    private var backgroundColor: String? = null
    private var darkBackgroundColor: String? = null

    /**
     * Set the heading text.
     *
     * @param headText Heading text.
     */
    fun headText(headText: String): ToolbarCustomizationBuilder = apply { this.headText = headText }

    /**
     * Set the button text.
     *
     * @param buttonText Button text.
     */
    fun buttonText(buttonText: String): ToolbarCustomizationBuilder = apply { this.buttonText = buttonText }

    /**
     * Set the background color.
     *
     * @param backgroundColor Color in hex format.
     */
    fun backgroundColor(backgroundColor: String): ToolbarCustomizationBuilder = apply { this.backgroundColor = backgroundColor }

    /**
     * Set the dark background color.
     *
     * @param darkBackgroundColor Color in hex format.
     */
    fun darkBackgroundColor(darkBackgroundColor: String): ToolbarCustomizationBuilder =
        apply { this.darkBackgroundColor = darkBackgroundColor }

    /**
     * Set the text color.
     *
     * @param textColor Color in hex format.
     */
    fun textColor(textColor: String): ToolbarCustomizationBuilder = apply { this.textColor = textColor }

    /**
     * Set the text font size.
     *
     * @param textFontSize Font size in sp unit.
     */
    fun textFontSize(textFontSize: Int): ToolbarCustomizationBuilder = apply { this.textFontSize = textFontSize }

    /**
     * Set the text font name.
     *
     * @param textFontName Font name.
     */
    fun textFontName(textFontName: String): ToolbarCustomizationBuilder = apply { this.textFontName = textFontName }

    /**
     * Set the dark text color.
     *
     * @param darkTextColor Color in hex format.
     */
    fun darkTextColor(darkTextColor: String): ToolbarCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    /**
     * Create an instance of [ToolbarCustomization].
     *
     * @return [ToolbarCustomization]
     */
    override fun build(): ToolbarCustomization {
        return ToolbarCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization().apply {
                this@ToolbarCustomizationBuilder.headText?.let { this.headerText = it }
                this@ToolbarCustomizationBuilder.buttonText?.let { this.buttonText = it }
                this@ToolbarCustomizationBuilder.backgroundColor?.let { this.backgroundColor = it }
                this@ToolbarCustomizationBuilder.darkBackgroundColor?.let { this.darkBackgroundColor = it }
                this@ToolbarCustomizationBuilder.textColor?.let { this.textColor = it }
                this@ToolbarCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@ToolbarCustomizationBuilder.textFontName?.let { this.textFontName = it }
                this@ToolbarCustomizationBuilder.darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}

/**
 * Type of button in the challenge screen.
 */
enum class ButtonType(val value: com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType) {
    SUBMIT(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.SUBMIT),
    CONTINUE(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CONTINUE),
    NEXT(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.NEXT),
    CANCEL(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CANCEL),
    RESEND(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.RESEND),
    OPEN_OOB_APP(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.OPEN_OOB_APP)
}

/**
 * Configuration for button customization.
 */
@Parcelize
data class ButtonCustomization internal constructor(
    internal val buttonCustomization: com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization
) : Parcelable

/**
 * Builder for building [ButtonCustomization] data.
 */
class ButtonCustomizationBuilder : CustomizationBuilder<ButtonCustomization>() {
    private var cornerRadius: Int? = null
    private var backgroundColor: String? = null
    private var darkBackgroundColor: String? = null

    /**
     * Set the corner radius.
     *
     * @param cornerRadius Corner radius in dp unit.
     */
    fun cornerRadius(cornerRadius: Int): ButtonCustomizationBuilder = apply { this.cornerRadius = cornerRadius }

    /**
     * Set the background color.
     *
     * @param backgroundColor Color in hex format.
     */
    fun backgroundColor(backgroundColor: String): ButtonCustomizationBuilder = apply { this.backgroundColor = backgroundColor }

    /**
     * Set the dark background color.
     *
     * @param darkBackgroundColor Color in hex format.
     */
    fun darkBackgroundColor(darkBackgroundColor: String): ButtonCustomizationBuilder =
        apply { this.darkBackgroundColor = darkBackgroundColor }

    /**
     * Set the text color.
     *
     * @param textColor Color in hex format.
     */
    fun textColor(textColor: String): ButtonCustomizationBuilder = apply { this.textColor = textColor }

    /**
     * Set the text font size.
     *
     * @param textFontSize Font size in sp unit.
     */
    fun textFontSize(textFontSize: Int): ButtonCustomizationBuilder = apply { this.textFontSize = textFontSize }

    /**
     * Set the text font name.
     *
     * @param textFontName Font name.
     */
    fun textFontName(textFontName: String): ButtonCustomizationBuilder = apply { this.textFontName = textFontName }

    /**
     * Set the dark text color.
     *
     * @param darkTextColor Color in hex format.
     */
    fun darkTextColor(darkTextColor: String): ButtonCustomizationBuilder = apply { this.darkTextColor = darkTextColor }

    /**
     * Create an instance of [ButtonCustomization].
     *
     * @return [ButtonCustomization]
     */
    override fun build(): ButtonCustomization {
        return ButtonCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization().apply {
                this@ButtonCustomizationBuilder.cornerRadius?.let { this.cornerRadius = it }
                this@ButtonCustomizationBuilder.backgroundColor?.let { this.backgroundColor = it }
                this@ButtonCustomizationBuilder.darkBackgroundColor?.let { this.darkBackgroundColor = it }
                this@ButtonCustomizationBuilder.textColor?.let { this.textColor = it }
                this@ButtonCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@ButtonCustomizationBuilder.textFontName?.let { this.textFontName = it }
                this@ButtonCustomizationBuilder.darkTextColor?.let { this.darkTextColor = it }
            }
        )
    }
}
