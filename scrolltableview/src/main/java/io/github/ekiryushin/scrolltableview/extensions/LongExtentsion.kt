package io.github.ekiryushin.scrolltableview.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Преобразовать значение к дате в определенном формате.
 * @param dateFormat формат даты
 */
fun Long.toStringDate(dateFormat: String): String {
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    return simpleDateFormat.format(Date(this))
}