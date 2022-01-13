package io.github.ekiryushin.scrolltableview.extensions

import android.widget.EditText

/**
 * Задать значение в поле ввода только в том случае, если его можно преобразовать к Float.
 * @param value значение, которое нужно проверить и вставить в поле ввода.
 */
fun EditText.setTextIfValueAsFloat(value: String?) {
    val newValue = try {
        value?.toFloat()?.toString()
    } catch (e: Exception) {
        null
    }
    this.setText(newValue)
}

/**
 * Задать значение в поле ввода только в том случае, если оно в виде даты в формате ДД.ММ.ГГГГ.
 * @param value значение, которое нужно проверить и вставить в поле ввода.
 */
fun EditText.setTextIfValueAsDDMMYYYY(value: String?) {
    if (value?.asDDMMYYYY() == true) {
        this.setText(value)
    }
}