package co.omise.android.config

import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import co.omise.android.config.UiCustomization as OmiseSdkUiCustomization
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class UiCustomizationTest {
    @Test
    fun uiCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val toolbarCustomization = ToolbarCustomizationBuilder().build()
        val labelCustomization = LabelCustomizationBuilder().build()
        val textBoxCustomization = TextBoxCustomizationBuilder().build()
        val primaryButtonCustomization = ButtonCustomizationBuilder().build()
        val secondaryButtonCustomization = ButtonCustomizationBuilder().build()
        val buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()
        buttonCustomizations[ButtonType.SUBMIT] = primaryButtonCustomization
        buttonCustomizations[ButtonType.CONTINUE] = primaryButtonCustomization
        buttonCustomizations[ButtonType.NEXT] = primaryButtonCustomization
        buttonCustomizations[ButtonType.OPEN_OOB_APP] = primaryButtonCustomization
        buttonCustomizations[ButtonType.ADD_CH] = primaryButtonCustomization
        buttonCustomizations[ButtonType.RESEND] = secondaryButtonCustomization
        buttonCustomizations[ButtonType.CANCEL] = secondaryButtonCustomization

        val uiCustomization = UiCustomizationBuilder()
            .setDefaultTheme(
                ThemeConfig(
                    labelCustomization,
                    toolbarCustomization,
                    textBoxCustomization,
                    buttonCustomizations
            )
            ).setDarkTheme(
                ThemeConfig(
                    labelCustomization,
                    toolbarCustomization,
                    textBoxCustomization,
                    buttonCustomizations
                )
            ).setMonoChromeTheme(
                ThemeConfig(
                    labelCustomization,
                    toolbarCustomization,
                    textBoxCustomization,
                    buttonCustomizations
                )
            )
            .build()

        // Assert for default theme
        assertEquals(toolbarCustomization.toolbarCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.toolbarCustomization)
        assertEquals(labelCustomization.labelCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.labelCustomization)
        assertEquals(textBoxCustomization.textBoxCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.textBoxCustomization)
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.SUBMIT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.CONTINUE)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.NEXT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.OPEN_OOB_APP)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.CANCEL)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.RESEND)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.ADD_CH)
        )

        // Assert for dark theme
        assertEquals(toolbarCustomization.toolbarCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.toolbarCustomization)
        assertEquals(labelCustomization.labelCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.labelCustomization)
        assertEquals(textBoxCustomization.textBoxCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.textBoxCustomization)
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.SUBMIT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.CONTINUE)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.NEXT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.OPEN_OOB_APP)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.CANCEL)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.RESEND)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DARK]?.getButtonCustomization(UiCustomization.ButtonType.ADD_CH)
        )

        // Assert for monoChrome
        assertEquals(toolbarCustomization.toolbarCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.toolbarCustomization)
        assertEquals(labelCustomization.labelCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.labelCustomization)
        assertEquals(textBoxCustomization.textBoxCustomization, uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.textBoxCustomization)
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.SUBMIT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.CONTINUE)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.NEXT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.OPEN_OOB_APP)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.CANCEL)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.RESEND)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.MONOCHROME]?.getButtonCustomization(UiCustomization.ButtonType.ADD_CH)
        )
        // validate empty theme
        val emptyThemeConfig = ThemeConfig()
        val emptyUiCustomization = UiCustomizationBuilder()
            .setDefaultTheme(
                emptyThemeConfig
            ).setDarkTheme(
                emptyThemeConfig
            ).setMonoChromeTheme(
                emptyThemeConfig
            )
            .build()
        assertEquals(
            null,
            emptyUiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.labelCustomization
        )
        assertEquals(
            null,
            emptyUiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.toolbarCustomization
        )
        assertEquals(
            null,
            emptyUiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.textBoxCustomization
        )
        assertEquals(
            null,
            emptyUiCustomization.uiCustomizationMap[UiCustomization.UiCustomizationType.DEFAULT]?.getButtonCustomization(UiCustomization.ButtonType.ADD_CH)
        )
        // validate default values
        assertEquals(
            emptyMap<UiCustomization.UiCustomizationType, UiCustomization>(),
            OmiseSdkUiCustomization.default.uiCustomizationMap
        )
    }

    @Test
    fun labelCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val labelCustomization = LabelCustomizationBuilder()
            .headingTextFontSize(18)
            .headingTextFontName("Roboto-Bold")
            .headingTextColor("#000000")
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .build()

        assertEquals(18, labelCustomization.labelCustomization.headingTextFontSize)
        assertEquals("Roboto-Bold", labelCustomization.labelCustomization.headingTextFontName)
        assertEquals("#000000", labelCustomization.labelCustomization.headingTextColor)
        assertEquals(16, labelCustomization.labelCustomization.textFontSize)
        assertEquals("Roboto", labelCustomization.labelCustomization.textFontName)
        assertEquals("#000000", labelCustomization.labelCustomization.textColor)
    }

    @Test
    fun textBoxCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val textBoxCustomization = TextBoxCustomizationBuilder()
            .borderWidth(1)
            .borderColor("#1A56F0")
            .cornerRadius(4)
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .build()

        assertEquals(1, textBoxCustomization.textBoxCustomization.borderWidth)
        assertEquals("#1A56F0", textBoxCustomization.textBoxCustomization.borderColor)
        assertEquals(4, textBoxCustomization.textBoxCustomization.cornerRadius)
        assertEquals(16, textBoxCustomization.textBoxCustomization.textFontSize)
        assertEquals("Roboto", textBoxCustomization.textBoxCustomization.textFontName)
        assertEquals("#000000", textBoxCustomization.textBoxCustomization.textColor)
    }

    @Test
    fun toolbarCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val toolbarCustomization = ToolbarCustomizationBuilder()
            .headerText("Secure Checkout")
            .buttonText("Close")
            .backgroundColor("#FFFFFF")
            .textFontSize(20)
            .textFontName("Roboto")
            .textColor("#000000")
            .build()

        assertEquals("Secure Checkout", toolbarCustomization.toolbarCustomization.headerText)
        assertEquals("Close", toolbarCustomization.toolbarCustomization.buttonText)
        assertEquals("#FFFFFF", toolbarCustomization.toolbarCustomization.backgroundColor)
        assertEquals(20, toolbarCustomization.toolbarCustomization.textFontSize)
        assertEquals("Roboto", toolbarCustomization.toolbarCustomization.textFontName)
        assertEquals("#000000", toolbarCustomization.toolbarCustomization.textColor)
    }

    @Test
    fun buttonCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val buttonCustomization = ButtonCustomizationBuilder()
            .cornerRadius(4)
            .backgroundColor("#1A56F0")
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .build()

        assertEquals(4, buttonCustomization.buttonCustomization.cornerRadius)
        assertEquals("#1A56F0", buttonCustomization.buttonCustomization.backgroundColor)
        assertEquals(16, buttonCustomization.buttonCustomization.textFontSize)
        assertEquals("Roboto", buttonCustomization.buttonCustomization.textFontName)
        assertEquals("#000000", buttonCustomization.buttonCustomization.textColor)
    }
}
