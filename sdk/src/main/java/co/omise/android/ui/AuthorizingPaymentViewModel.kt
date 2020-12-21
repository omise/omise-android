package co.omise.android.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSListener
import co.omise.android.threeds.core.ThreeDSConfig
import co.omise.android.threeds.data.models.TransactionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.jetbrains.annotations.TestOnly


internal class AuthorizingPaymentViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        ThreeDSConfig.initialize(AuthorizingPaymentConfig.get().threeDSConfig.threeDSConfig)
        val threeDS = ThreeDS(activity)
        return AuthorizingPaymentViewModel(threeDS) as T
    }
}

internal class AuthorizingPaymentViewModel(private val threeDS: ThreeDS) : ViewModel(), ThreeDSListener {

    private val _authentication = MutableLiveData<AuthenticationResult>()
    val authentication: LiveData<AuthenticationResult> = _authentication

    init {
        threeDS.setThreeDSListener(this)
    }

    @TestOnly
    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    private var coroutineScope: CoroutineScope = viewModelScope

    fun cleanup() {
        viewModelScope.cancel()
        threeDS.cleanup()
    }

    fun authorizeTransaction(authorizedUrl: String) {
        threeDS.authorizeTransaction(authorizedUrl)
    }

    override fun onCompleted(transStatus: TransactionStatus) {
        _authentication.postValue(AuthenticationResult.AuthenticationCompleted(transStatus))
    }

    override fun onFailure(e: Throwable) {
        _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
    }

    override fun onUnsupported() {
        _authentication.postValue(AuthenticationResult.AuthenticationUnsupported)
    }
}

sealed class AuthenticationResult {
    object AuthenticationUnsupported : AuthenticationResult()
    data class AuthenticationCompleted(val transStatus: TransactionStatus) : AuthenticationResult()
    data class AuthenticationFailure(val error: Throwable) : AuthenticationResult()
}
