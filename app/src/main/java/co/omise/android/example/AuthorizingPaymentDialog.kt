package co.omise.android.example

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog


class AuthorizingPaymentDialog {
    companion object {
        @JvmStatic
        fun showAuthorizingPaymentDialog(context: Context, listener: (authorizeUrl: String, returnUrl: String) -> Unit) {
            val marginSize = context.resources.getDimensionPixelSize(R.dimen.medium_margin)
            val authorizeUrlEditText = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginSize, marginSize, marginSize, marginSize)
                }

                hint = "Authorize URL"
                setText("https://3dsms.staging-omise.co/payments/pay2_5xaujsv7zh2miq3tki0/authorize")
            }
            val returnUrlEditText = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginSize, marginSize, marginSize, marginSize)
                }
                hint = "Return URL"
            }
            val containerView = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                addView(authorizeUrlEditText)
                addView(returnUrlEditText)
            }
            AlertDialog.Builder(context)
                    .setTitle("Authorizing Payment")
                    .setView(containerView)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        listener(authorizeUrlEditText.text.toString(), returnUrlEditText.text.toString())
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }
    }
}
