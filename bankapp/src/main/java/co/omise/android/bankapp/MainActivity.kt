package co.omise.android.bankapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val goBackButton = findViewById<Button>(R.id.go_back_button)

        // test uri: bankapp://omise.co/authorize?return_uri=sampleapp://omise.co/authorize_return?result=success
        goBackButton.setOnClickListener {
            intent.data?.getQueryParameter("return_uri")?.let { returnUri ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(returnUri)))
            } ?: run {
                Snackbar.make(it, "No return_uri found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
