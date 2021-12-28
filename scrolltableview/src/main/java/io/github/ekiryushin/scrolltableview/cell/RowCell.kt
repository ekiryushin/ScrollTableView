package io.github.ekiryushin.scrolltableview.cell

/** Одна строка со значениями. */
data class RowCell(
    /** Список колонок в строке */
    var columns: List<Cell>) {

    /** Состояние строки
     * <ol>
     * <li>DataStatus.NORMAL - строка без изменения. Значение по умолчанию.</li>
     * <li>DataStatus.ADD - строка добавилось.</li>
     * <li>DataStatus.DELETE - строка удалилась.</li>
     * </ol>
     */
    var status: DataStatus = DataStatus.NORMAL

    /** Состояние, которое было перед удалением строки */
    var statusBeforeDelete: DataStatus = DataStatus.NORMAL

}
