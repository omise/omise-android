package co.omise.android.extensions

import android.view.View

fun View.setOnClickListener(action: () -> Unit) {
    this.setOnClickListener { action() }
}
