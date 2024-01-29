package co.omise.android.extensions

fun String?.isContains(str: String): Boolean = this.toString().contains(str)

fun String.capitalizeFirstChar(): String {
    return replaceFirstChar { it.uppercase() }
}
