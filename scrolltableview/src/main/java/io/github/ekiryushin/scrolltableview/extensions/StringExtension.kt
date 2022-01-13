package io.github.ekiryushin.scrolltableview.extensions

import io.github.ekiryushin.scrolltableview.utils.SVTConstants
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Проверить является ли строка датой в формате ДД.ММ.ГГГГ.
 * @return true - строка содержит дату в формате ДД.ММ.ГГГГ, false - какая-то другая строка.
 */
fun String.asDDMMYYYY(): Boolean {
    return try {
        SimpleDateFormat(SVTConstants.DATE_FORMAT, Locale.getDefault()).parse(this)
        true
    }
    catch (e: Exception) {
        false
    }
}

/**
 * Преобразовать строку, которая содержит timestamp в строку даты формата ДД.ММ.ГГГГ.
 * @return преобразованная строка к виду ДД.ММ.ГГГГ,
 * либо null если исходное значение не удалось преобразовать к Long
 */
fun String.longToDDMMYYYY(): String? {
    return try {
        this.toLong().toStringDate(SVTConstants.DATE_FORMAT)
    }
    catch (e: Exception) {
        null
    }
}
/**
 * Преобразовать дату формата ДД.ММ.ГГГГ в миллисекунды (timestamp)
 * @return преобразованная строка к виду ДД.ММ.ГГГГ,
 * либо null если исходное значение не удалось преобразовать к Long
 */
fun String?.toTimestamp(): String? {
    if (this == null) {
        return null
    }

    return try {
        val date = SimpleDateFormat(SVTConstants.DATE_FORMAT, Locale.getDefault()).parse(this)
        date?.time.toString()
    }
    catch (e: Exception) {
        null
    }
}