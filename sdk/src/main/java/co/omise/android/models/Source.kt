package co.omise.android.models

import org.intellij.lang.annotations.Flow

class Source {
    @JvmField
    var type: SourceType? = null
    @JvmField
    var flow: FlowType? = null
    @JvmField
    val amount: Long = 0
    @JvmField
    val currency: String? = null
    @JvmField
    val barcode: String? = null
    @JvmField
    val references: References? = null
    @JvmField
    val storeId: String? = null
    @JvmField
    val storeName: String? = null
    @JvmField
    val terminalId: String? = null
    @JvmField
    val name: String? = null
    @JvmField
    val email: String? = null
    @JvmField
    val phoneNumber: String? = null
    @JvmField
    val installmentTerm: Int = 0


}