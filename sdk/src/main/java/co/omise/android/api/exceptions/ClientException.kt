package co.omise.android.api.exceptions

/**
 * Client Exception object contains information about errors during client initialization.
 */
class ClientException(cause: Exception) : Exception("Client initialization failure.", cause)
