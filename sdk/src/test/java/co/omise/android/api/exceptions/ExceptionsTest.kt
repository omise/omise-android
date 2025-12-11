package co.omise.android.api.exceptions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ExceptionsTest {
    
    @Test
    fun clientException_withCause_shouldContainCorrectMessage() {
        val cause = IOException("Network error")
        val exception = ClientException(cause)
        
        assertEquals("Client initialization failure.", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun clientException_causeChain_shouldPreserveCause() {
        val originalException = IllegalArgumentException("Invalid argument")
        val clientException = ClientException(originalException)
        
        assertNotNull(clientException.cause)
        assertTrue(clientException.cause is IllegalArgumentException)
        assertEquals("Invalid argument", clientException.cause?.message)
    }
    
    @Test
    fun clientException_isException_shouldBeInstanceOfException() {
        val exception = ClientException(RuntimeException())
        
        assertTrue(exception is Exception)
        assertTrue(exception is Throwable)
    }
    
    @Test
    fun redirectionException_message_shouldReturnCorrectMessage() {
        val exception = RedirectionException()
        
        assertEquals("Redirection is not allowed.", exception.message)
    }
    
    @Test
    fun redirectionException_isThrowable_shouldBeInstanceOfThrowable() {
        val exception = RedirectionException()
        
        assertTrue(exception is Throwable)
    }
    
    @Test
    fun redirectionException_canBeThrown_shouldBeCatchable() {
        var caught = false
        
        try {
            throw RedirectionException()
        } catch (e: RedirectionException) {
            caught = true
            assertEquals("Redirection is not allowed.", e.message)
        }
        
        assertTrue("Exception should have been caught", caught)
    }
    
    @Test
    fun clientException_canBeThrown_shouldBeCatchable() {
        var caught = false
        val originalCause = IOException("Connection refused")
        
        try {
            throw ClientException(originalCause)
        } catch (e: ClientException) {
            caught = true
            assertEquals("Client initialization failure.", e.message)
            assertEquals(originalCause, e.cause)
        }
        
        assertTrue("Exception should have been caught", caught)
    }
    
    @Test
    fun clientException_differentCauses_shouldHandleVariousExceptions() {
        val causes = listOf(
            IOException("IO error"),
            IllegalStateException("Invalid state"),
            NullPointerException("Null value"),
            RuntimeException("Runtime error")
        )
        
        causes.forEach { cause ->
            val clientException = ClientException(cause)
            assertEquals("Client initialization failure.", clientException.message)
            assertEquals(cause, clientException.cause)
        }
    }
}
