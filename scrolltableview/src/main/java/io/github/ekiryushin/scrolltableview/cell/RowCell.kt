package io.github.ekiryushin.scrolltableview.cell

/** Одна строка со значениями. */
data class RowCell(
    /** Список колонок в строке. */
    var columns: List<Cell>) {

    /** Состояние строки.
     * * DataStatus.NORMAL - строка без изменения. Значение по умолчанию.
     * * DataStatus.ADD - строка добавилось.
     * * DataStatus.DELETE - строка удалилась.
     */
    var status: DataStatus = DataStatus.NORMAL

    /** Состояние, которое было перед удалением строки. */
    var statusBeforeDelete: DataStatus = DataStatus.NORMAL

    /** Дублировать строку в новый объект */
    fun copy(): RowCell {
        val newColumns = this.columns.map { column -> column.copy() }
        val newRow = RowCell(newColumns)
        newRow.status = this.status
        newRow.statusBeforeDelete = this.statusBeforeDelete
        return newRow
    }
}
