package co.omise.android.api.exceptions

/**
 * Client Exception object contains information about errors during client initialization.
 *
 * @param cause The specific exception that caused the initialization failure.
 */
class ClientException(cause: Exception) : Exception("Client initialization failure.", cause)
