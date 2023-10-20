package co.omise.android.config

import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

        val uiCustomization = UiCustomizationBuilder()
            .supportDarkMode(true)
            .toolbarCustomization(toolbarCustomization)
            .labelCustomization(labelCustomization)
            .textBoxCustomization(textBoxCustomization)
            .buttonCustomization(ButtonType.SUBMIT, primaryButtonCustomization)
            .buttonCustomization(ButtonType.CONTINUE, primaryButtonCustomization)
            .buttonCustomization(ButtonType.NEXT, primaryButtonCustomization)
            .buttonCustomization(ButtonType.OPEN_OOB_APP, primaryButtonCustomization)
            .buttonCustomization(ButtonType.CANCEL, secondaryButtonCustomization)
            .buttonCustomization(ButtonType.RESEND, secondaryButtonCustomization)
            .build()

        assertTrue(uiCustomization.uiCustomization.darkModeSupported)
        assertEquals(toolbarCustomization.toolbarCustomization, uiCustomization.uiCustomization.toolbarCustomization)
        assertEquals(labelCustomization.labelCustomization, uiCustomization.uiCustomization.labelCustomization)
        assertEquals(textBoxCustomization.textBoxCustomization, uiCustomization.uiCustomization.textBoxCustomization)
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.SUBMIT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.CONTINUE)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.NEXT)
        )
        assertEquals(
            primaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.OPEN_OOB_APP)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.CANCEL)
        )
        assertEquals(
            secondaryButtonCustomization.buttonCustomization,
            uiCustomization.uiCustomization.getButtonCustomization(UiCustomization.ButtonType.RESEND)
        )
    }

    @Test
    fun labelCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val labelCustomization = LabelCustomizationBuilder()
            .headingTextFontSize(18)
            .headingTextFontName("Roboto-Bold")
            .headingTextColor("#000000")
            .headingDarkTextColor("#FFFFFF")
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .darkTextColor("#FFFFFF")
            .build()

        assertEquals(18, labelCustomization.labelCustomization.headingTextFontSize)
        assertEquals("Roboto-Bold", labelCustomization.labelCustomization.headingTextFontName)
        assertEquals("#000000", labelCustomization.labelCustomization.headingTextColor)
        assertEquals("#FFFFFF", labelCustomization.labelCustomization.headingDarkTextColor)
        assertEquals(16, labelCustomization.labelCustomization.textFontSize)
        assertEquals("Roboto", labelCustomization.labelCustomization.textFontName)
        assertEquals("#000000", labelCustomization.labelCustomization.textColor)
        assertEquals("#FFFFFF", labelCustomization.labelCustomization.darkTextColor)
    }

    @Test
    fun textBoxCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val textBoxCustomization = TextBoxCustomizationBuilder()
            .borderWidth(1)
            .borderColor("#1A56F0")
            .cornerRadius(4)
            .darkBorderColor("#1A56F0")
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .darkTextColor("#FFFFFF")
            .build()

        assertEquals(1, textBoxCustomization.textBoxCustomization.borderWidth)
        assertEquals("#1A56F0", textBoxCustomization.textBoxCustomization.borderColor)
        assertEquals(4, textBoxCustomization.textBoxCustomization.cornerRadius)
        assertEquals("#1A56F0", textBoxCustomization.textBoxCustomization.darkBorderColor)
        assertEquals(16, textBoxCustomization.textBoxCustomization.textFontSize)
        assertEquals("Roboto", textBoxCustomization.textBoxCustomization.textFontName)
        assertEquals("#000000", textBoxCustomization.textBoxCustomization.textColor)
        assertEquals("#FFFFFF", textBoxCustomization.textBoxCustomization.darkTextColor)
    }

    @Test
    fun toolbarCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val toolbarCustomization = ToolbarCustomizationBuilder()
            .headerText("Secure Checkout")
            .buttonText("Close")
            .backgroundColor("#FFFFFF")
            .darkBackgroundColor("#000000")
            .textFontSize(20)
            .textFontName("Roboto")
            .textColor("#000000")
            .darkTextColor("#FFFFFF")
            .build()

        assertEquals("Secure Checkout", toolbarCustomization.toolbarCustomization.headerText)
        assertEquals("Close", toolbarCustomization.toolbarCustomization.buttonText)
        assertEquals("#FFFFFF", toolbarCustomization.toolbarCustomization.backgroundColor)
        assertEquals("#000000", toolbarCustomization.toolbarCustomization.darkBackgroundColor)
        assertEquals(20, toolbarCustomization.toolbarCustomization.textFontSize)
        assertEquals("Roboto", toolbarCustomization.toolbarCustomization.textFontName)
        assertEquals("#000000", toolbarCustomization.toolbarCustomization.textColor)
        assertEquals("#FFFFFF", toolbarCustomization.toolbarCustomization.darkTextColor)
    }

    @Test
    fun buttonCustomization_shouldBeAbleToCreateInstanceWithBuilder() {
        val buttonCustomization = ButtonCustomizationBuilder()
            .cornerRadius(4)
            .backgroundColor("#1A56F0")
            .darkBackgroundColor("#1A56F0")
            .textFontSize(16)
            .textFontName("Roboto")
            .textColor("#000000")
            .darkTextColor("#FFFFFF")
            .build()

        assertEquals(4, buttonCustomization.buttonCustomization.cornerRadius)
        assertEquals("#1A56F0", buttonCustomization.buttonCustomization.backgroundColor)
        assertEquals("#1A56F0", buttonCustomization.buttonCustomization.darkBackgroundColor)
        assertEquals(16, buttonCustomization.buttonCustomization.textFontSize)
        assertEquals("Roboto", buttonCustomization.buttonCustomization.textFontName)
        assertEquals("#000000", buttonCustomization.buttonCustomization.textColor)
        assertEquals("#FFFFFF", buttonCustomization.buttonCustomization.darkTextColor)
    }
}
