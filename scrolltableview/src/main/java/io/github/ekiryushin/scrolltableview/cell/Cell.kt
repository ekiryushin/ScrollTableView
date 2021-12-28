package io.github.ekiryushin.scrolltableview.cell

/** Содержимое каждой ячейки. */
data class Cell (
    /** Какой-то идентификатор значения, которое можно использовать для сохранения в базу */
    var id: Long? = null,

    /** Значение ячейки */

    var value: String? = null,

    /**
     * Вариант отображения ячейки.
     * <ol>
     * <li>CellView.ONLY_READ - только для чтения, без возможности редактировать. Значение по умолчанию.</li>
     * <li>CellView.EDIT_STRING - значение можно редактировать как строку.</li>
     * <li>CellView.EDIT_NUMBER - значение можно редактировать как число.</li>
     * </ol>
     */
    val viewed: CellView = CellView.ONLY_READ
) {

    /**
     * Состояние значения ячейки.
     * <ol>
     * <li>DataStatus.NORMAL - значение без изменения. Значение по умолчанию.</li>
     * <li>DataStatus.EDIT - значение изменилось.</li>
     * <li>DataStatus.ADD - значение добавилось.</li>
     * </ol>
     */
    var status: DataStatus = DataStatus.NORMAL

    /** Первоначальное значение для выставления корректного состояния */
    val initialValue: String? = value
}