package co.omise.android.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


abstract class CustomizationBuilder<T> {
    protected var textFontSize: Int? = null
    protected var textColor: String? = null
    protected var textFontName: String? = null
    abstract fun build(): T
}

/**
 * Configuration for UI customization.
 */
@Parcelize
data class UiCustomization internal constructor(
    internal val uiCustomizationMap: Map<com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType, com.netcetera.threeds.sdk.api.ui.logic.UiCustomization>
) : Parcelable {
    companion object {
        val default = UiCustomization(emptyMap())
    }
}

data class ThemeConfig(
    var labelCustomization: LabelCustomization? = null,
    var toolbarCustomization: ToolbarCustomization? = null,
    var textBoxCustomization: TextBoxCustomization? = null,
    var buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()
)

/**
 * Builder for building [UiCustomization] data.
 */
class UiCustomizationBuilder {

    private var defaultThemeConfig = ThemeConfig()
    private var darkThemeConfig = ThemeConfig()
    private var monoChromeThemeConfig = ThemeConfig()

    /**
     * Set the default theme.
     *
     * @param themeConfig [ThemeConfig] data.
     */
    fun setDefaultTheme(themeConfig: ThemeConfig): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.defaultThemeConfig = themeConfig
    }

    /**
     * Set the dark theme.
     *
     * @param themeConfig [ThemeConfig] data.
     */
    fun setDarkTheme(themeConfig: ThemeConfig): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.darkThemeConfig = themeConfig
    }

    /**
     * Set the monoChrome theme.
     *
     * @param themeConfig [ThemeConfig] data.
     */
    fun setMonoChromeTheme(themeConfig: ThemeConfig): UiCustomizationBuilder = apply {
        this@UiCustomizationBuilder.monoChromeThemeConfig = themeConfig
    }


    /**
     * Create an instance of [UiCustomization].
     *
     * @return [UiCustomization]
     */
    fun build(): UiCustomization {
        val defaultUiCustomization = com.netcetera.threeds.sdk.api.ui.logic.UiCustomization().apply {
            defaultThemeConfig.labelCustomization?.let { labelCustomization = it.labelCustomization }
            defaultThemeConfig.toolbarCustomization?.let { toolbarCustomization = it.toolbarCustomization }
            defaultThemeConfig.textBoxCustomization?.let { textBoxCustomization = it.textBoxCustomization }
            defaultThemeConfig.buttonCustomizations.forEach { (buttonType, buttonCustomization) ->
                setButtonCustomization(buttonCustomization.buttonCustomization, buttonType.value)
            }
        }

        val darkUiCustomization = com.netcetera.threeds.sdk.api.ui.logic.UiCustomization().apply {
            darkThemeConfig.labelCustomization?.let { labelCustomization = it.labelCustomization }
            darkThemeConfig.toolbarCustomization?.let { toolbarCustomization = it.toolbarCustomization }
            darkThemeConfig.textBoxCustomization?.let { textBoxCustomization = it.textBoxCustomization }
            darkThemeConfig.buttonCustomizations.forEach { (buttonType, buttonCustomization) ->
                setButtonCustomization(buttonCustomization.buttonCustomization, buttonType.value)
            }
        }

        val monoChromeUiCustomization = com.netcetera.threeds.sdk.api.ui.logic.UiCustomization().apply {
            monoChromeThemeConfig.labelCustomization?.let { labelCustomization = it.labelCustomization }
            monoChromeThemeConfig.toolbarCustomization?.let { toolbarCustomization = it.toolbarCustomization }
            monoChromeThemeConfig.textBoxCustomization?.let { textBoxCustomization = it.textBoxCustomization }
            monoChromeThemeConfig.buttonCustomizations.forEach { (buttonType, buttonCustomization) ->
                setButtonCustomization(buttonCustomization.buttonCustomization, buttonType.value)
            }
        }

        val uiCustomizationMap = hashMapOf<com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType, com.netcetera.threeds.sdk.api.ui.logic.UiCustomization>()
        uiCustomizationMap.apply {
            put(UiCustomizationType.DEFAULT.value,defaultUiCustomization)
            put(UiCustomizationType.DARK.value,darkUiCustomization)
            put(UiCustomizationType.MONOCHROME.value,monoChromeUiCustomization)
        }

        return UiCustomization(uiCustomizationMap)
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
                this@LabelCustomizationBuilder.textColor?.let { this.textColor = it }
                this@LabelCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@LabelCustomizationBuilder.textFontName?.let { this.textFontName = it }
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
                this@TextBoxCustomizationBuilder.textColor?.let { this.textColor = it }
                this@TextBoxCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@TextBoxCustomizationBuilder.textFontName?.let { this.textFontName = it }
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
    private var headerText: String? = null
    private var buttonText: String? = null
    private var backgroundColor: String? = null

    /**
     * Set the heading text.
     *
     * @param headerText Heading text.
     */
    fun headerText(headerText: String): ToolbarCustomizationBuilder = apply { this.headerText = headerText }

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
     * Create an instance of [ToolbarCustomization].
     *
     * @return [ToolbarCustomization]
     */
    override fun build(): ToolbarCustomization {
        return ToolbarCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization().apply {
                this@ToolbarCustomizationBuilder.headerText?.let { this.headerText = it }
                this@ToolbarCustomizationBuilder.buttonText?.let { this.buttonText = it }
                this@ToolbarCustomizationBuilder.backgroundColor?.let { this.backgroundColor = it }
                this@ToolbarCustomizationBuilder.textColor?.let { this.textColor = it }
                this@ToolbarCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@ToolbarCustomizationBuilder.textFontName?.let { this.textFontName = it }
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
    OPEN_OOB_APP(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.OPEN_OOB_APP),
    ADD_CH(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.ADD_CH)
}

/**
 * Type of UiCustomization in the challenge screen.
 */
internal enum class UiCustomizationType(val value: com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType) {
    DEFAULT(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType.DEFAULT),
    DARK(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType.DARK),
    MONOCHROME(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType.MONOCHROME),
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
     * Create an instance of [ButtonCustomization].
     *
     * @return [ButtonCustomization]
     */
    override fun build(): ButtonCustomization {
        return ButtonCustomization(
            com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization().apply {
                this@ButtonCustomizationBuilder.cornerRadius?.let { this.cornerRadius = it }
                this@ButtonCustomizationBuilder.backgroundColor?.let { this.backgroundColor = it }
                this@ButtonCustomizationBuilder.textColor?.let { this.textColor = it }
                this@ButtonCustomizationBuilder.textFontSize?.let { this.textFontSize = it }
                this@ButtonCustomizationBuilder.textFontName?.let { this.textFontName = it }
            }
        )
    }
}
