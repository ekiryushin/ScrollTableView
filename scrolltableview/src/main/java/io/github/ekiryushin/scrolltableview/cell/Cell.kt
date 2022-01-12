package io.github.ekiryushin.scrolltableview.cell

/** Содержимое каждой ячейки. */
data class Cell (
    /** Какой-то идентификатор значения, который можно использовать для сохранения в базу. */
    var id: Long? = null,

    /** Значение ячейки. */
    var value: String? = null,

    /**
     * Вариант отображения ячейки.
     * * CellView.ONLY_READ - только для чтения, без возможности редактирования. Значение по умолчанию.
     * * CellView.EDIT_STRING - значение можно редактировать как строку.
     * * CellView.EDIT_NUMBER - значение можно редактировать как число.
     * * CellView.EDIT_DD_MM_YYYY - значение можно редактировать как дату, а хранить в формате ДД.ММ.ГГГГ.
     * * CellView.EDIT_TIMESTAMP - значение можно редактировать как дату, а хранить в миллисекундах.
     */
    val viewed: CellView = CellView.ONLY_READ
) {
    /**
     * Состояние значения ячейки.
     * * DataStatus.NORMAL - значение без изменения. Значение по умолчанию.
     * * DataStatus.EDIT - значение изменилось.
     * * DataStatus.ADD - значение добавилось.
     */
    var status: DataStatus = DataStatus.NORMAL

    /** Первоначальное значение для выставления корректного состояния. */
    val initialValue: String? = value

    /** Дублировать ячейку в новый объект */
    fun copy(): Cell {
        val newCell = Cell(this.id, this.value, this.viewed)
        newCell.status = this.status
        return newCell
    }
}