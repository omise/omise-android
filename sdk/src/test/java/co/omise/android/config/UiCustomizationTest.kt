package co.omise.android.config

import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


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
}
