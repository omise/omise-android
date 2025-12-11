package co.omise.android.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceTypeTest {
    
    @Test
    fun creator_internetBankingBay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("internet_banking_bay")
        assertTrue(sourceType is SourceType.InternetBanking.Bay)
        assertEquals("internet_banking_bay", sourceType.name)
    }
    
    @Test
    fun creator_internetBankingBbl_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("internet_banking_bbl")
        assertTrue(sourceType is SourceType.InternetBanking.Bbl)
        assertEquals("internet_banking_bbl", sourceType.name)
    }
    
    @Test
    fun creator_mobileBankingBay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_bay")
        assertTrue(sourceType is SourceType.MobileBanking.Bay)
        assertEquals("mobile_banking_bay", sourceType.name)
    }
    
    @Test
    fun creator_mobileBankingBbl_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_bbl")
        assertTrue(sourceType is SourceType.MobileBanking.Bbl)
        assertEquals("mobile_banking_bbl", sourceType.name)
    }
    
    @Test
    fun creator_mobileBankingKbank_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_kbank")
        assertTrue(sourceType is SourceType.MobileBanking.KBank)
        assertEquals("mobile_banking_kbank", sourceType.name)
    }
    
    @Test
    fun creator_mobileBankingKtb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_ktb")
        assertTrue(sourceType is SourceType.MobileBanking.KTB)
        assertEquals("mobile_banking_ktb", sourceType.name)
    }
    
    @Test
    fun creator_mobileBankingScb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_scb")
        assertTrue(sourceType is SourceType.MobileBanking.Scb)
        assertEquals("mobile_banking_scb", sourceType.name)
    }
    
    @Test
    fun creator_alipay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("alipay")
        assertTrue(sourceType is SourceType.Alipay)
        assertEquals("alipay", sourceType.name)
    }
    
    @Test
    fun creator_billPaymentTescoLotus_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("bill_payment_tesco_lotus")
        assertTrue(sourceType is SourceType.BillPaymentTescoLotus)
        assertEquals("bill_payment_tesco_lotus", sourceType.name)
    }
    
    @Test
    fun creator_barcodeAlipay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("barcode_alipay")
        assertTrue(sourceType is SourceType.BarcodeAlipay)
        assertEquals("barcode_alipay", sourceType.name)
    }
    
    @Test
    fun creator_econtext_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("econtext")
        assertTrue(sourceType is SourceType.Econtext)
        assertEquals("econtext", sourceType.name)
    }
    
    @Test
    fun creator_fpx_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("fpx")
        assertTrue(sourceType is SourceType.Fpx)
        assertEquals("fpx", sourceType.name)
    }
    
    @Test
    fun creator_truemoney_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("truemoney")
        assertTrue(sourceType is SourceType.TrueMoney)
        assertEquals("truemoney", sourceType.name)
    }
    
    @Test
    fun creator_truemoneyJumpapp_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("truemoney_jumpapp")
        assertTrue(sourceType is SourceType.TrueMoneyJumpApp)
        assertEquals("truemoney_jumpapp", sourceType.name)
    }
    
    @Test
    fun creator_promptpay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("promptpay")
        assertTrue(sourceType is SourceType.PromptPay)
        assertEquals("promptpay", sourceType.name)
    }
    
    @Test
    fun creator_paynow_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("paynow")
        assertTrue(sourceType is SourceType.PayNow)
        assertEquals("paynow", sourceType.name)
    }
    
    @Test
    fun creator_alipayCn_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("alipay_cn")
        assertTrue(sourceType is SourceType.AlipayCn)
        assertEquals("alipay_cn", sourceType.name)
    }
    
    @Test
    fun creator_alipayHk_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("alipay_hk")
        assertTrue(sourceType is SourceType.AlipayHk)
        assertEquals("alipay_hk", sourceType.name)
    }
    
    @Test
    fun creator_dana_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("dana")
        assertTrue(sourceType is SourceType.Dana)
        assertEquals("dana", sourceType.name)
    }
    
    @Test
    fun creator_gcash_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("gcash")
        assertTrue(sourceType is SourceType.Gcash)
        assertEquals("gcash", sourceType.name)
    }
    
    @Test
    fun creator_kakaopay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("kakaopay")
        assertTrue(sourceType is SourceType.Kakaopay)
        assertEquals("kakaopay", sourceType.name)
    }
    
    @Test
    fun creator_touchNGo_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("touch_n_go")
        assertTrue(sourceType is SourceType.TouchNGo)
        assertEquals("touch_n_go", sourceType.name)
    }
    
    @Test
    fun creator_rabbitLinepay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("rabbit_linepay")
        assertTrue(sourceType is SourceType.RabbitLinePay)
        assertEquals("rabbit_linepay", sourceType.name)
    }
    
    @Test
    fun creator_ocbcDigital_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("mobile_banking_ocbc")
        assertTrue(sourceType is SourceType.OcbcDigital)
        assertEquals("mobile_banking_ocbc", sourceType.name)
    }
    
    @Test
    fun creator_boost_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("boost")
        assertTrue(sourceType is SourceType.Boost)
        assertEquals("boost", sourceType.name)
    }
    
    @Test
    fun creator_shopeepay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("shopeepay")
        assertTrue(sourceType is SourceType.ShopeePay)
        assertEquals("shopeepay", sourceType.name)
    }
    
    @Test
    fun creator_shopeepayJumpapp_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("shopeepay_jumpapp")
        assertTrue(sourceType is SourceType.ShopeePayJumpApp)
        assertEquals("shopeepay_jumpapp", sourceType.name)
    }
    
    @Test
    fun creator_duitnowObw_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("duitnow_obw")
        assertTrue(sourceType is SourceType.DuitNowOBW)
        assertEquals("duitnow_obw", sourceType.name)
    }
    
    @Test
    fun creator_duitnowQr_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("duitnow_qr")
        assertTrue(sourceType is SourceType.DuitNowQR)
        assertEquals("duitnow_qr", sourceType.name)
    }
    
    @Test
    fun creator_maybankQr_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("maybank_qr")
        assertTrue(sourceType is SourceType.MaybankQR)
        assertEquals("maybank_qr", sourceType.name)
    }
    
    @Test
    fun creator_atome_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("atome")
        assertTrue(sourceType is SourceType.Atome)
        assertEquals("atome", sourceType.name)
    }
    
    @Test
    fun creator_grabpay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("grabpay")
        assertTrue(sourceType is SourceType.GrabPay)
        assertEquals("grabpay", sourceType.name)
    }
    
    @Test
    fun creator_paypay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("paypay")
        assertTrue(sourceType is SourceType.PayPay)
        assertEquals("paypay", sourceType.name)
    }
    
    @Test
    fun creator_wechatPay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("wechat_pay")
        assertTrue(sourceType is SourceType.WeChatPay)
        assertEquals("wechat_pay", sourceType.name)
    }
    
    // Installment tests
    @Test
    fun creator_installmentBay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_bay")
        assertTrue(sourceType is SourceType.Installment.Bay)
        assertEquals("installment_bay", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbBay_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_bay")
        assertTrue(sourceType is SourceType.Installment.BayWlb)
        assertEquals("installment_wlb_bay", sourceType.name)
    }
    
    @Test
    fun creator_installmentFirstChoice_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_first_choice")
        assertTrue(sourceType is SourceType.Installment.FirstChoice)
        assertEquals("installment_first_choice", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbFirstChoice_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_first_choice")
        assertTrue(sourceType is SourceType.Installment.FirstChoiceWlb)
        assertEquals("installment_wlb_first_choice", sourceType.name)
    }
    
    @Test
    fun creator_installmentBbl_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_bbl")
        assertTrue(sourceType is SourceType.Installment.Bbl)
        assertEquals("installment_bbl", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbBbl_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_bbl")
        assertTrue(sourceType is SourceType.Installment.BblWlb)
        assertEquals("installment_wlb_bbl", sourceType.name)
    }
    
    @Test
    fun creator_installmentMbb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_mbb")
        assertTrue(sourceType is SourceType.Installment.Mbb)
        assertEquals("installment_mbb", sourceType.name)
    }
    
    @Test
    fun creator_installmentKtc_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_ktc")
        assertTrue(sourceType is SourceType.Installment.Ktc)
        assertEquals("installment_ktc", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbKtc_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_ktc")
        assertTrue(sourceType is SourceType.Installment.KtcWlb)
        assertEquals("installment_wlb_ktc", sourceType.name)
    }
    
    @Test
    fun creator_installmentKbank_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_kbank")
        assertTrue(sourceType is SourceType.Installment.KBank)
        assertEquals("installment_kbank", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbKbank_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_kbank")
        assertTrue(sourceType is SourceType.Installment.KBankWlb)
        assertEquals("installment_wlb_kbank", sourceType.name)
    }
    
    @Test
    fun creator_installmentScb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_scb")
        assertTrue(sourceType is SourceType.Installment.Scb)
        assertEquals("installment_scb", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbScb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_scb")
        assertTrue(sourceType is SourceType.Installment.ScbWlb)
        assertEquals("installment_wlb_scb", sourceType.name)
    }
    
    @Test
    fun creator_installmentTtb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_ttb")
        assertTrue(sourceType is SourceType.Installment.Ttb)
        assertEquals("installment_ttb", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbTtb_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_ttb")
        assertTrue(sourceType is SourceType.Installment.TtbWlb)
        assertEquals("installment_wlb_ttb", sourceType.name)
    }
    
    @Test
    fun creator_installmentUob_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_uob")
        assertTrue(sourceType is SourceType.Installment.Uob)
        assertEquals("installment_uob", sourceType.name)
    }
    
    @Test
    fun creator_installmentWlbUob_shouldReturnCorrectType() {
        val sourceType = SourceType.creator("installment_wlb_uob")
        assertTrue(sourceType is SourceType.Installment.UobWlb)
        assertEquals("installment_wlb_uob", sourceType.name)
    }
    
    @Test
    fun creator_unknownType_shouldReturnUnknown() {
        val sourceType = SourceType.creator("unknown_payment_method")
        assertTrue(sourceType is SourceType.Unknown)
        assertEquals("unknown_payment_method", sourceType.name)
    }
    
    @Test
    fun creator_nullType_shouldReturnUnknownWithNull() {
        val sourceType = SourceType.creator(null)
        assertTrue(sourceType is SourceType.Unknown)
        assertEquals(null, sourceType.name)
    }
    
    @Test
    fun allElements_shouldContainExpectedSourceTypes() {
        val allElements = SourceType.allElements
        
        assertTrue(allElements.contains(SourceType.InternetBanking.Bay))
        assertTrue(allElements.contains(SourceType.InternetBanking.Bbl))
        assertTrue(allElements.contains(SourceType.Alipay))
        assertTrue(allElements.contains(SourceType.BillPaymentTescoLotus))
        assertTrue(allElements.contains(SourceType.BarcodeAlipay))
        assertTrue(allElements.contains(SourceType.Econtext))
        assertTrue(allElements.contains(SourceType.TrueMoney))
        assertTrue(allElements.contains(SourceType.Atome))
        
        // Verify it contains installment types
        assertTrue(allElements.any { it is SourceType.Installment.Bay })
        assertTrue(allElements.any { it is SourceType.Installment.Bbl })
        assertTrue(allElements.any { it is SourceType.Installment.Scb })
    }
    
    @Test
    fun allElements_shouldNotBeEmpty() {
        val allElements = SourceType.allElements
        assertTrue(allElements.isNotEmpty())
        assertTrue(allElements.size > 20)
    }
    
    @Test
    fun sourceTypeName_shouldMatchExpectedFormat() {
        assertEquals("alipay", SourceType.Alipay.name)
        assertEquals("promptpay", SourceType.PromptPay.name)
        assertEquals("truemoney", SourceType.TrueMoney.name)
        assertEquals("internet_banking_bay", SourceType.InternetBanking.Bay.name)
        assertEquals("mobile_banking_scb", SourceType.MobileBanking.Scb.name)
        assertEquals("installment_bay", SourceType.Installment.Bay.name)
    }
    
    @Test
    fun fpx_withBanks_shouldStoreCorrectly() {
        val bank1 = Bank(code = "uob", name = "UOB", active = true)
        val bank2 = Bank(code = "hsbc", name = "HSBC", active = false)
        val fpx = SourceType.Fpx(banks = listOf(bank1, bank2))
        
        assertEquals("fpx", fpx.name)
        assertEquals(2, fpx.banks?.size)
        assertEquals("uob", fpx.banks?.get(0)?.code)
        assertEquals("hsbc", fpx.banks?.get(1)?.code)
    }
    
    @Test
    fun touchNGo_withProvider_shouldStoreCorrectly() {
        val touchNGo = SourceType.TouchNGo(provider = "mobile")
        
        assertEquals("touch_n_go", touchNGo.name)
        assertEquals("mobile", touchNGo.provider)
    }
    
    @Test
    fun grabPay_withProvider_shouldStoreCorrectly() {
        val grabPay = SourceType.GrabPay(provider = "mobile")
        
        assertEquals("grabpay", grabPay.name)
        assertEquals("mobile", grabPay.provider)
    }
}

