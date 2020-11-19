package co.omise.android.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


internal class SDKCoroutineScope {
    private val job = Job()
    private val dispatcher = Dispatchers.Default
    val coroutineScope = CoroutineScope(job + dispatcher)
}
