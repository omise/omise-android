package co.omise.android.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UiCustomizationTest {
    
    @Test
    fun uiCustomization_defaultCompanionObject_shouldNotBeNull() {
        val defaultCustomization = UiCustomization.default
        
        assertNotNull(defaultCustomization)
        assertTrue(defaultCustomization.uiCustomizationMap.isEmpty())
    }
    
    @Test
    fun buttonType_allValues_shouldHaveCorrectMapping() {
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.SUBMIT,
            ButtonType.SUBMIT.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CONTINUE,
            ButtonType.CONTINUE.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.NEXT,
            ButtonType.NEXT.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.CANCEL,
            ButtonType.CANCEL.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.RESEND,
            ButtonType.RESEND.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.OPEN_OOB_APP,
            ButtonType.OPEN_OOB_APP.value
        )
        assertEquals(
            com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.ButtonType.ADD_CH,
            ButtonType.ADD_CH.value
        )
    }
    
    @Test
    fun themeConfig_defaultValues_shouldBeNull() {
        val themeConfig = ThemeConfig()
        
        assertEquals(null, themeConfig.labelCustomization)
        assertEquals(null, themeConfig.toolbarCustomization)
        assertEquals(null, themeConfig.textBoxCustomization)
        assertTrue(themeConfig.buttonCustomizations.isEmpty())
    }
    
    @Test
    fun uiCustomizationBuilder_builderPattern_shouldChainCorrectly() {
        val builder = UiCustomizationBuilder()
        val builder2 = builder.setDefaultTheme(ThemeConfig())
        val builder3 = builder2.setDarkTheme(ThemeConfig())
        val builder4 = builder3.setMonoChromeTheme(ThemeConfig())
        
        // Verify builder pattern returns same instance
        assertTrue(builder === builder2)
        assertTrue(builder2 === builder3)
        assertTrue(builder3 === builder4)
    }
    
    @Test
    fun labelCustomizationBuilder_builderPattern_shouldChainCorrectly() {
        val builder = LabelCustomizationBuilder()
        val builder2 = builder.textColor("#000000")
        val builder3 = builder2.textFontSize(14)
        val builder4 = builder3.textFontName("Arial")
        val builder5 = builder4.headingTextColor("#FF0000")
        val builder6 = builder5.headingTextFontSize(18)
        val builder7 = builder6.headingTextFontName("Roboto")
        
        assertTrue(builder === builder2)
        assertTrue(builder2 === builder3)
        assertTrue(builder3 === builder4)
        assertTrue(builder4 === builder5)
        assertTrue(builder5 === builder6)
        assertTrue(builder6 === builder7)
    }
    
    @Test
    fun textBoxCustomizationBuilder_builderPattern_shouldChainCorrectly() {
        val builder = TextBoxCustomizationBuilder()
        val builder2 = builder.borderWidth(1)
        val builder3 = builder2.cornerRadius(4)
        val builder4 = builder3.borderColor("#000000")
        val builder5 = builder4.textColor("#FFFFFF")
        val builder6 = builder5.textFontSize(14)
        val builder7 = builder6.textFontName("Arial")
        
        assertTrue(builder === builder2)
        assertTrue(builder2 === builder3)
        assertTrue(builder3 === builder4)
        assertTrue(builder4 === builder5)
        assertTrue(builder5 === builder6)
        assertTrue(builder6 === builder7)
    }
    
    @Test
    fun toolbarCustomizationBuilder_builderPattern_shouldChainCorrectly() {
        val builder = ToolbarCustomizationBuilder()
        val builder2 = builder.headerText("Test")
        val builder3 = builder2.buttonText("Close")
        val builder4 = builder3.backgroundColor("#000000")
        val builder5 = builder4.textColor("#FFFFFF")
        val builder6 = builder5.textFontSize(16)
        val builder7 = builder6.textFontName("Roboto")
        
        assertTrue(builder === builder2)
        assertTrue(builder2 === builder3)
        assertTrue(builder3 === builder4)
        assertTrue(builder4 === builder5)
        assertTrue(builder5 === builder6)
        assertTrue(builder6 === builder7)
    }
    
    @Test
    fun buttonCustomizationBuilder_builderPattern_shouldChainCorrectly() {
        val builder = ButtonCustomizationBuilder()
        val builder2 = builder.cornerRadius(8)
        val builder3 = builder2.backgroundColor("#FF0000")
        val builder4 = builder3.textColor("#FFFFFF")
        val builder5 = builder4.textFontSize(14)
        val builder6 = builder5.textFontName("Arial")
        
        assertTrue(builder === builder2)
        assertTrue(builder2 === builder3)
        assertTrue(builder3 === builder4)
        assertTrue(builder4 === builder5)
        assertTrue(builder5 === builder6)
    }
}

