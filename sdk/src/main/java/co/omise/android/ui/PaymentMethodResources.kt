package co.omise.android.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.SourceType
import co.omise.android.models.TokenizationMethod
import co.omise.android.models.backendType

internal val Capability.paymentMethodResources: List<PaymentMethodResource>
    get() {
        val items = mutableListOf<PaymentMethodResource>()
        this.paymentMethods
            .orEmpty()
            .forEach { paymentMethod ->
                when (paymentMethod.backendType) {
                    is BackendType.Token ->
                        when ((paymentMethod.backendType as BackendType.Token).tokenizationMethod) {
                            is TokenizationMethod.Card -> items.add(PaymentMethodResource.CreditCard)
                            else ->
                                PaymentMethodResource.all.find {
                                    it.tokenizationMethod == (paymentMethod.backendType as? BackendType.Token)?.tokenizationMethod
                                }?.let { items.add(it) }
                        }
                    is BackendType.Source ->
                        when ((paymentMethod.backendType as BackendType.Source).sourceType) {
                            is SourceType.Installment -> items.add(PaymentMethodResource.Installments)
                            is SourceType.InternetBanking -> items.add(PaymentMethodResource.InternetBankings)
                            is SourceType.MobileBanking -> items.add(PaymentMethodResource.MobileBankings)
                            is SourceType.Econtext ->
                                items.addAll(
                                    listOf(
                                        PaymentMethodResource.ConvenienceStore,
                                        PaymentMethodResource.PayEasy,
                                        PaymentMethodResource.Netbanking,
                                    ),
                                )
                            is SourceType.TouchNGo -> {
                                when (paymentMethod.provider) {
                                    PaymentMethodResource.ALIPAY_PLUS_PROVIDER ->
                                        items.add(
                                            PaymentMethodResource.TouchNGoAlipay,
                                        )
                                    else -> items.add(PaymentMethodResource.TouchNGo)
                                }
                            }

                            // TrueMoneyJumpApp replaces legacy TrueMoney.
                            // When TrueMoneyJumpApp is available in capability response, prefer it over legacy TrueMoney.
                            // When legacy TrueMoney is available in capability response without TrueMoneyJumpApp use it.
                            is SourceType.TrueMoneyJumpApp -> {
                                items.remove(PaymentMethodResource.TrueMoney)
                                items.add(PaymentMethodResource.TrueMoneyJumpApp)
                            }

                            // when ShopeepayJumpApp is available will use is instead of ShopeePay normal flow
                            is SourceType.ShopeePayJumpApp -> {
                                // if ShopeePay is not in the list items list will got no modify
                                items.remove(PaymentMethodResource.ShopeePay)
                                items.add(PaymentMethodResource.ShopeePayJumpApp)
                            }
                            is SourceType.GrabPay -> {
                                when (paymentMethod.provider) {
                                    PaymentMethodResource.RMS_PROVIDER ->
                                        items.add(
                                            PaymentMethodResource.GrabPayRMS,
                                        )
                                    else -> items.add(PaymentMethodResource.GrabPay)
                                }
                            }
                            else ->
                                PaymentMethodResource.all.find {
                                    it.sourceType == (paymentMethod.backendType as? BackendType.Source)?.sourceType
                                }?.let { items.add(it) }
                        }
                }
            }
        return items.distinct()
    }

internal val List<SourceType.Installment>.installmentResources: List<InstallmentResource>
    get() = this.mapNotNull { sourceType -> InstallmentResource.all.find { it.sourceType == sourceType } }

internal val List<SourceType.InternetBanking>.internetBankingResources: List<InternetBankingResource>
    get() = this.mapNotNull { sourceType -> InternetBankingResource.all.find { it.sourceType == sourceType } }

internal val List<SourceType.MobileBanking>.mobileBankingResources: List<MobileBankingResource>
    get() = this.mapNotNull { sourceType -> MobileBankingResource.all.find { it.sourceType == sourceType } }

internal sealed class PaymentMethodResource(
    @DrawableRes override val iconRes: Int,
    @StringRes override val titleRes: Int?,
    @StringRes override val subtitleRes: Int? = null,
    @DrawableRes override val indicatorIconRes: Int,
    val isCreditCard: Boolean = false,
    val sourceType: SourceType? = null,
    val tokenizationMethod: TokenizationMethod? = null,
) : OmiseListItem {
    object CreditCard : PaymentMethodResource(
        iconRes = R.drawable.payment_card,
        titleRes = R.string.payment_method_credit_card_title,
        indicatorIconRes = R.drawable.ic_next,
        isCreditCard = true,
    )

    object GooglePay : PaymentMethodResource(
        iconRes = R.drawable.googlepay,
        titleRes = R.string.googlepay,
        indicatorIconRes = R.drawable.ic_next,
        tokenizationMethod = TokenizationMethod.GooglePay,
    )

    object Installments : PaymentMethodResource(
        iconRes = R.drawable.payment_installment,
        titleRes = R.string.payment_method_installments_title,
        indicatorIconRes = R.drawable.ic_next,
    )

    object InternetBankings : PaymentMethodResource(
        iconRes = R.drawable.payment_banking,
        titleRes = R.string.payment_method_internet_banking_title,
        indicatorIconRes = R.drawable.ic_next,
    )

    object MobileBankings : PaymentMethodResource(
        iconRes = R.drawable.payment_mobile,
        titleRes = R.string.payment_method_mobile_banking_title,
        indicatorIconRes = R.drawable.ic_next,
    )

    object TescoLotus : PaymentMethodResource(
        iconRes = R.drawable.payment_tesco,
        titleRes = R.string.payment_method_tesco_lotus_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.BillPaymentTescoLotus,
    )

    object ConvenienceStore : PaymentMethodResource(
        iconRes = R.drawable.payment_conbini,
        titleRes = R.string.payment_method_convenience_store_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Econtext,
    )

    object PayEasy : PaymentMethodResource(
        iconRes = R.drawable.payment_payeasy,
        titleRes = R.string.payment_method_pay_easy_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Econtext,
    )

    object Netbanking : PaymentMethodResource(
        iconRes = R.drawable.payment_netbank,
        titleRes = R.string.payment_method_netbank_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Econtext,
    )

    object Alipay : PaymentMethodResource(
        iconRes = R.drawable.payment_alipay,
        titleRes = R.string.payment_method_alipay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Alipay,
    )

    object PromptPay : PaymentMethodResource(
        iconRes = R.drawable.payment_promptpay,
        titleRes = R.string.payment_method_promptpay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.PromptPay,
    )

    object PayNow : PaymentMethodResource(
        iconRes = R.drawable.payment_paynow,
        titleRes = R.string.payment_method_paynow_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.PayNow,
    )

    object PointsCiti : PaymentMethodResource(
        iconRes = R.drawable.payment_points_citi,
        titleRes = R.string.payment_method_points_citi_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.PointsCiti,
    )

    object TrueMoney : PaymentMethodResource(
        iconRes = R.drawable.payment_truemoney,
        titleRes = R.string.payment_truemoney_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.TrueMoney,
    )

    object TrueMoneyJumpApp : PaymentMethodResource(
        iconRes = R.drawable.payment_truemoney,
        titleRes = R.string.payment_truemoney_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.TrueMoneyJumpApp,
    )

    object Fpx : PaymentMethodResource(
        iconRes = R.drawable.payment_fpx,
        titleRes = R.string.payment_method_fpx_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Fpx(),
    )

    object AlipayCn : PaymentMethodResource(
        iconRes = R.drawable.payment_alipay_cn,
        titleRes = R.string.payment_method_alipay_cn_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.AlipayCn,
    )

    object AlipayHk : PaymentMethodResource(
        iconRes = R.drawable.payment_alipay_hk,
        titleRes = R.string.payment_method_alipay_hk_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.AlipayHk,
    )

    object Dana : PaymentMethodResource(
        iconRes = R.drawable.payment_dana,
        titleRes = R.string.payment_method_dana_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Dana,
    )

    object Gcash : PaymentMethodResource(
        iconRes = R.drawable.payment_gcash,
        titleRes = R.string.payment_method_gcash_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Gcash,
    )

    object Kakaopay : PaymentMethodResource(
        iconRes = R.drawable.payment_kakaopay,
        titleRes = R.string.payment_method_kakaopay_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Kakaopay,
    )

    object TouchNGo : PaymentMethodResource(
        iconRes = R.drawable.payment_touch_n_go,
        titleRes = R.string.payment_method_touch_n_go_title,
        subtitleRes = null,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.TouchNGo(),
    )

    object TouchNGoAlipay : PaymentMethodResource(
        iconRes = R.drawable.payment_touch_n_go,
        titleRes = R.string.payment_method_touch_n_go_title,
        subtitleRes = R.string.payment_method_alipayplus_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.TouchNGo(),
    )

    object RabbitLinepay : PaymentMethodResource(
        iconRes = R.drawable.payment_rabbit_linepay,
        titleRes = R.string.payment_method_rabbit_linepay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.RabbitLinePay,
    )

    object OcbcPao : PaymentMethodResource(
        iconRes = R.drawable.payment_ocbc_pao,
        titleRes = R.string.payment_method_mobile_banking_ocbc_pao_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.OcbcPao,
    )

    object OcbcDigital : PaymentMethodResource(
        iconRes = R.drawable.payment_ocbc_digital,
        titleRes = R.string.payment_method_ocbc_digital_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.OcbcDigital,
    )

    object Boost : PaymentMethodResource(
        iconRes = R.drawable.payment_boost,
        titleRes = R.string.payment_method_boots_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Boost,
    )

    object ShopeePay : PaymentMethodResource(
        iconRes = R.drawable.payment_shopeepay,
        titleRes = R.string.payment_method_shopeepay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.ShopeePay,
    )

    object ShopeePayJumpApp : PaymentMethodResource(
        iconRes = R.drawable.payment_shopeepay,
        titleRes = R.string.payment_method_shopeepay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.ShopeePayJumpApp,
    )

    object DuitNowOBW : PaymentMethodResource(
        iconRes = R.drawable.payment_duitnow_obw,
        titleRes = R.string.payment_method_duitnow_obw_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.DuitNowOBW,
    )

    object DuitNowQR : PaymentMethodResource(
        iconRes = R.drawable.payment_duitnow_qr,
        titleRes = R.string.payment_method_duitnow_qr_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.DuitNowQR,
    )

    object MaybankQR : PaymentMethodResource(
        iconRes = R.drawable.payment_mae_maybank,
        titleRes = R.string.payment_method_maybank_qr_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MaybankQR,
    )

    object GrabPay : PaymentMethodResource(
        iconRes = R.drawable.payment_grabpay,
        titleRes = R.string.payment_method_grabpay_title,
        subtitleRes = R.string.payment_method_grabpay_footnote,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.GrabPay(),
    )

    object GrabPayRMS : PaymentMethodResource(
        iconRes = R.drawable.payment_grabpay,
        titleRes = R.string.payment_method_grabpay_rms_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.GrabPay(),
    )

    object PayPay : PaymentMethodResource(
        iconRes = R.drawable.payment_paypay,
        titleRes = R.string.payment_method_paypay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.PayPay,
    )

    object Atome : PaymentMethodResource(
        iconRes = R.drawable.payment_atome,
        titleRes = R.string.payment_method_atome_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.Atome,
    )

    object WeChatPay : PaymentMethodResource(
        iconRes = R.drawable.wechat_pay,
        titleRes = R.string.payment_method_wechat_pay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.WeChatPay,
    )

    companion object {
        const val ALIPAY_PLUS_PROVIDER = "Alipay_plus"
        const val RMS_PROVIDER = "RMS"
        val all: List<PaymentMethodResource>
            get() = PaymentMethodResource::class.nestedClasses.mapNotNull { it.objectInstance as? PaymentMethodResource }
    }
}

internal sealed class InstallmentResource(
    @DrawableRes override val iconRes: Int,
    override val title: String? = null,
    @StringRes override val titleRes: Int? = null,
    @StringRes override val subtitleRes: Int? = null,
    @DrawableRes override val indicatorIconRes: Int,
    val sourceType: SourceType,
) : OmiseListItem {
    companion object {
        val all: List<InstallmentResource>
            get() = InstallmentResource::class.nestedClasses.mapNotNull { it.objectInstance as? InstallmentResource }
    }

    object Bbl : InstallmentResource(
        iconRes = R.drawable.payment_bbl,
        titleRes = R.string.payment_method_installment_bbl_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Bbl,
    )

    object Mbb : InstallmentResource(
        iconRes = R.drawable.payment_maybank,
        titleRes = R.string.payment_method_installment_mbb_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Mbb,
    )

    object KBank : InstallmentResource(
        iconRes = R.drawable.payment_kasikorn,
        titleRes = R.string.payment_method_installment_kasikorn_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.KBank,
    )

    object Bay : InstallmentResource(
        iconRes = R.drawable.payment_bay,
        titleRes = R.string.payment_method_installment_bay_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Bay,
    )

    object FirstChoice : InstallmentResource(
        iconRes = R.drawable.payment_first_choice,
        titleRes = R.string.payment_method_installment_first_choice_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.FirstChoice,
    )

    object Ktc : InstallmentResource(
        iconRes = R.drawable.payment_ktc,
        titleRes = R.string.payment_method_installment_ktc_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Ktc,
    )

    object Scb : InstallmentResource(
        iconRes = R.drawable.payment_scb,
        titleRes = R.string.payment_method_installment_scb_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Scb,
    )

    object Citi : InstallmentResource(
        iconRes = R.drawable.payment_citi,
        titleRes = R.string.payment_method_installment_citi_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Citi,
    )

    object Ttb : InstallmentResource(
        iconRes = R.drawable.payment_ttb,
        titleRes = R.string.payment_method_installment_ttb_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Ttb,
    )

    object Uob : InstallmentResource(
        iconRes = R.drawable.payment_uob,
        titleRes = R.string.payment_method_installment_uob_title,
        indicatorIconRes = R.drawable.ic_next,
        sourceType = SourceType.Installment.Uob,
    )
}

internal data class InstallmentTermResource(
    @DrawableRes override val iconRes: Int? = null,
    override val title: String,
    val installmentTerm: Int,
    @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect,
) : OmiseListItem

internal sealed class InternetBankingResource(
    @DrawableRes override val iconRes: Int,
    override val title: String? = null,
    @StringRes override val titleRes: Int? = null,
    @StringRes override val subtitleRes: Int? = null,
    @DrawableRes override val indicatorIconRes: Int,
    val sourceType: SourceType,
) : OmiseListItem {
    companion object {
        val all: List<InternetBankingResource>
            get() = InternetBankingResource::class.nestedClasses.mapNotNull { it.objectInstance as? InternetBankingResource }
    }

    object Bbl : InternetBankingResource(
        iconRes = R.drawable.payment_bbl,
        titleRes = R.string.payment_method_internet_banking_bbl_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.InternetBanking.Bbl,
    )

    object Bay : InternetBankingResource(
        iconRes = R.drawable.payment_bay,
        titleRes = R.string.payment_method_internet_banking_bay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.InternetBanking.Bay,
    )
}

internal sealed class MobileBankingResource(
    @DrawableRes override val iconRes: Int,
    override val title: String? = null,
    @StringRes override val titleRes: Int? = null,
    @StringRes override val subtitleRes: Int? = null,
    @DrawableRes override val indicatorIconRes: Int,
    val sourceType: SourceType,
) : OmiseListItem {
    companion object {
        val all: List<MobileBankingResource>
            get() = MobileBankingResource::class.nestedClasses.mapNotNull { it.objectInstance as? MobileBankingResource }
    }

    object Bay : MobileBankingResource(
        iconRes = R.drawable.kma,
        titleRes = R.string.payment_method_mobile_banking_bay_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MobileBanking.Bay,
    )

    object Bbl : MobileBankingResource(
        iconRes = R.drawable.bblm,
        titleRes = R.string.payment_method_mobile_banking_bbl_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MobileBanking.Bbl,
    )

    object KBank : MobileBankingResource(
        iconRes = R.drawable.payment_kplus,
        titleRes = R.string.payment_method_mobile_banking_kbank_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MobileBanking.KBank,
    )

    object KTB : MobileBankingResource(
        iconRes = R.drawable.payment_ktb_next,
        titleRes = R.string.payment_method_mobile_banking_ktb_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MobileBanking.KTB,
    )

    object Scb : MobileBankingResource(
        iconRes = R.drawable.scb_easy,
        titleRes = R.string.payment_method_mobile_banking_scb_title,
        indicatorIconRes = R.drawable.ic_redirect,
        sourceType = SourceType.MobileBanking.Scb,
    )
}

internal class FpxResource(
    @DrawableRes override val iconRes: Int,
    override val title: String? = null,
    @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect,
    override val enabled: Boolean? = false,
    val bankCode: String? = null,
) : OmiseListItem {
    companion object {
        val all: List<FpxResource>
            get() = FpxResource::class.nestedClasses.mapNotNull { it.objectInstance as? FpxResource }

        fun getBankImageFromCode(code: String?): Int {
            return when (code) {
                "affin" -> R.drawable.payment_affin
                "alliance" -> R.drawable.payment_alliance
                "agro" -> R.drawable.payment_agro
                "ambank" -> R.drawable.payment_ambank
                "islam" -> R.drawable.payment_islam
                "muamalat" -> R.drawable.payment_muamalat
                "rakyat" -> R.drawable.payment_rakyat
                "bocm" -> R.drawable.payment_bocm
                "bsn" -> R.drawable.payment_bsn
                "cimb" -> R.drawable.payment_cimb
                "hongleong" -> R.drawable.payment_hongleong
                "hsbc" -> R.drawable.payment_hsbc
                "kfh" -> R.drawable.payment_kfh
                "maybank2e" -> R.drawable.payment_maybank
                "maybank2u" -> R.drawable.payment_maybank
                "ocbc" -> R.drawable.payment_ocbc
                "public" -> R.drawable.payment_publicbank
                "rhb" -> R.drawable.payment_rhb
                "sc" -> R.drawable.payment_sc
                "uob" -> R.drawable.payment_uob
                else -> R.drawable.payment_unknown
            }
        }
    }
}

internal class DuitNowOBWResource(
    @DrawableRes override val iconRes: Int,
    override val title: String? = null,
    @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect,
    val bankCode: String? = null,
    override val enabled: Boolean? = false,
) : OmiseListItem {
    companion object {
        val all: List<DuitNowOBWResource>
            get() = DuitNowOBWResource::class.nestedClasses.mapNotNull { it.objectInstance as? DuitNowOBWResource }

        fun getBankImageFromCode(code: String?): Int {
            return when (code) {
                "affin" -> R.drawable.payment_affin
                "alliance" -> R.drawable.payment_alliance
                "agro" -> R.drawable.payment_agro
                "ambank" -> R.drawable.payment_ambank
                "islam" -> R.drawable.payment_islam
                "muamalat" -> R.drawable.payment_muamalat
                "rakyat" -> R.drawable.payment_rakyat
                "bsn" -> R.drawable.payment_bsn
                "cimb" -> R.drawable.payment_cimb
                "hongleong" -> R.drawable.payment_hongleong
                "hsbc" -> R.drawable.payment_hsbc
                "kfh" -> R.drawable.payment_kfh
                "maybank2u" -> R.drawable.payment_maybank
                "ocbc" -> R.drawable.payment_ocbc
                "public" -> R.drawable.payment_publicbank
                "rhb" -> R.drawable.payment_rhb
                "sc" -> R.drawable.payment_sc
                "uob" -> R.drawable.payment_uob
                else -> R.drawable.payment_unknown
            }
        }
    }
}
