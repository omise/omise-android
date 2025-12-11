package co.omise.android.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtensionsTest {
    
    @Test
    fun isContains_withMatchingString_shouldReturnTrue() {
        val str = "Hello World"
        
        assertTrue(str.isContains("Hello"))
        assertTrue(str.isContains("World"))
        assertTrue(str.isContains("llo Wo"))
    }
    
    @Test
    fun isContains_withNonMatchingString_shouldReturnFalse() {
        val str = "Hello World"
        
        assertFalse(str.isContains("Goodbye"))
        assertFalse(str.isContains("xyz"))
    }
    
    @Test
    fun isContains_withNullString_shouldReturnFalse() {
        val str: String? = null
        
        assertFalse(str.isContains("test"))
    }
    
    @Test
    fun isContains_withEmptyString_shouldReturnFalse() {
        val str = ""
        
        assertFalse(str.isContains("test"))
    }
    
    @Test
    fun isContains_caseSensitive_shouldWork() {
        val str = "Hello World"
        
        assertTrue(str.isContains("Hello"))
        assertFalse(str.isContains("hello")) // Case sensitive
    }
    
    @Test
    fun capitalizeFirstChar_withLowercaseFirst_shouldCapitalize() {
        assertEquals("Hello", "hello".capitalizeFirstChar())
        assertEquals("Test", "test".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withUppercaseFirst_shouldRemainSame() {
        assertEquals("Hello", "Hello".capitalizeFirstChar())
        assertEquals("WORLD", "WORLD".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withSingleChar_shouldCapitalize() {
        assertEquals("A", "a".capitalizeFirstChar())
        assertEquals("Z", "z".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withEmptyString_shouldReturnEmpty() {
        assertEquals("", "".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withMultipleWords_shouldCapitalizeFirstCharOnly() {
        assertEquals("Hello world", "hello world".capitalizeFirstChar())
        assertEquals("Test string here", "test string here".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withNumber_shouldRemainSame() {
        assertEquals("123test", "123test".capitalizeFirstChar())
    }
    
    @Test
    fun capitalizeFirstChar_withSpecialChar_shouldRemainSame() {
        assertEquals("!hello", "!hello".capitalizeFirstChar())
        assertEquals("@test", "@test".capitalizeFirstChar())
    }
}

