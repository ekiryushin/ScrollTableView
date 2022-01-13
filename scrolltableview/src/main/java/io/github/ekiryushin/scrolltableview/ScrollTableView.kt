package io.github.ekiryushin.scrolltableview

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import io.github.ekiryushin.scrolltableview.cell.RowCell
import io.github.ekiryushin.scrolltableview.scrolled.HorizontalScrollTableView
import io.github.ekiryushin.scrolltableview.scrolled.OnScrollListener
import io.github.ekiryushin.scrolltableview.scrolled.ScrollTableParams
import io.github.ekiryushin.scrolltableview.scrolled.VerticalScrollTableView
import java.util.*

/** Основной класс по отображения таблицы. */
class ScrollTableView: RelativeLayout, ViewListener {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        inflater = (context as Activity).layoutInflater
        inflater?.inflate(R.layout.stv_scroll_table_view, this, true)

        //считываем параметры
        init()

        //навешиваем обработчики прокрутки
        setListener()
    }

    private var inflater: LayoutInflater? = null

    /** Закрепленная строка заголовка. */
    private lateinit var trHeaderFix: TableRow

    /** Строка заголовка. */
    private lateinit var trHeader: TableRow

    /** Закрепленые столбцы. */
    private lateinit var tableFix: TableLayout

    /** Основная таблица. */
    private lateinit var tableData: TableLayout

    /** Презентер отображения таблиц. */
    private lateinit var presenter: ScrollTableViewPresenter

    /** Параметры отображения таблицы. */
    private var params: ScrollTableParams = ScrollTableParams()

    /** Отрисовать таблицу. */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        //обновляем размер таблицы с данными
        val trFirstRowFix: TableRow? = tableFix.getChildAt(0) as? TableRow
        val trFirstRowData: TableRow? = tableData.getChildAt(0) as? TableRow

        //обновляем размеры закрепленых столбцов
        updateColumnWidth(trHeaderFix, trFirstRowFix)

        //обновляем размеры прокручиваемых столбцов
        updateColumnWidth(trHeader, trFirstRowData)
    }

    /** Задать обработчик клика по строке.
     * @param rowId индекс строки в данных, где расположена ячейка.
     * @param columnId индекс столбца в данных, где расположена ячейка.
     * @param row строка со значениями для редактирования.
     * @param view непосредственно ячейка, по которой нажали.
     */
    override fun setRowClickListener(rowId: Int, columnId: Int, row: RowCell?, view: View?) {
        row?.let {
            val curView = view ?: getCellView(rowId, columnId)
            curView?.setOnClickListener(cellClickListener(rowId))
        }
    }

    /** Установить фон строки в зависимости от того удалена она или нет.
     * @param changeRowId индекс измененной строки.
     * @param isDeleted true строка удаленная, false - строка восстановленная.
     */
    override fun setRowBackground(changeRowId: Int, isDeleted: Boolean) {
        //получаем строку закрепленных колонок
        val tableRowFix = if (changeRowId < tableFix.childCount) {
            tableFix.getChildAt(changeRowId) as TableRow
        }
        else {
            null
        }

        //получаем строку остальных колонок
        val tableRow = if (changeRowId < tableData.childCount) {
            tableData.getChildAt(changeRowId) as TableRow
        }
        else {
            null
        }

        //меням цвет фона строки
        tableRowFix?.let { setRowEnabled(it, isDeleted) }
        tableRow?.let { setRowEnabled(it, isDeleted) }
    }

    /** Установить фон ячейки, если значение в ней измененное
     * @param changeRowId индекс строки, в которой изменилось значение.
     * @param changeColumnId индекст колонки, в которой изменилось значение.
     * @param isEdited true - значение изменилось на новое, false - значение изменилось в исходное.
     */
    override fun setCellBackground(changeRowId: Int, changeColumnId: Int, isEdited: Boolean) {
        val columnView = getCellView(changeRowId, changeColumnId)
        val textView = columnView?.findViewById<TextView>(R.id.table_data_item)
        textView?.let {
            if (isEdited) {
                it.setBackgroundResource(params.cellEditedColorResId)
            }
            else {
                it.background = null
            }
        }
    }

    /** Прокрутить список вниз. */
    override fun scrollToEnd() {
        val scrollRowData: VerticalScrollTableView = this.findViewById(R.id.table_data_scroll_row_data)
        scrollRowData.post { scrollRowData.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    /**
     * Показать окно для изменения значения.
     * @param builder подготовленное диалоговое окно.
     * @param view разметка, содержащая все поля ввода.
     */
    override fun showDialog(builder: AlertDialog.Builder, view: LinearLayout) {
        builder
            .setCancelable(false)
            .setTitle(R.string.stv_edit_title)
            .setPositiveButton(context.getString(R.string.stv_button_success)) { _, _ -> applyDialog(view) }
            .setNegativeButton(context.getString(R.string.stv_button_cancel)) { dialog, _ ->
                cancelDialog(dialog, view)
            }
            .create()
            .show()
    }

    /**
     * Показать окно выбора даты.
     * @param editText поле ввода, для которого нужно показать окно.
     * @param gregorianCalendar дата, которую нужно выделить в календаре.
     */
    override fun showDialogSelectDate(editText: EditText, gregorianCalendar: GregorianCalendar) {
        DatePickerDialog(
            context,
            params.dialogStyleId,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear+1, year)
                editText.setText(selectedDate)
            },
            gregorianCalendar.get(Calendar.YEAR),
            gregorianCalendar.get(Calendar.MONTH),
            gregorianCalendar.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    /**
     * Удалить конкретную строку.
     * @param rowId идентификатор строки для удаления.
     */
    override fun removeRow(rowId: Int) {
        if (rowId < tableFix.childCount) {
            tableFix.removeView(tableFix.getChildAt(rowId))
        }
        if (rowId < tableData.childCount) {
            tableData.removeView(tableData.getChildAt(rowId))
        }
    }

    /** Задать список заголовков.
     * @param header строка, содержащая ячейки для каждого столбца заголовка.
     */
    fun setHeader(header: RowCell) {
        presenter.setHeader(header)
    }

    /** Задать таблицу с данными без заголовка.
     * @param data список из строк, содержащих ячейки для каждого столбца.
     */
    fun setData(data: MutableList<RowCell>) {
        presenter.setData(data)
    }

    /** Задать количество закрепленных столбцов.
     * @param countFixColumn количество столбцов слева, которые не будут прокручиваться.
     */
    fun setCountFixColumn(countFixColumn: Int) {
        params.countFixColumn = countFixColumn
    }

    /** Включить или выключить отображение иконок удаления и восстановления.
     * @param enabledIconDelete true - включить отображение иконок, false - выключить.
     */
    fun setEnabledIconDelete(enabledIconDelete: Boolean) {
        params.enabledIconDelete = enabledIconDelete
    }

    /** Задать цвет измененной ячейки.
     * resourceId - ссылка на цветовой ресурс.
     */
    fun setEditedCellColorResourceId(resourceId: Int) {
        params.cellEditedColorResId = resourceId
    }

    /** Задать стиль оформления диалогового окна выбора даты.
     * styleId - ссылка на стиль.
     */
    fun setDialogSelectDateStyleId(styleId: Int) {
        params.dialogStyleId = styleId
    }

    /** Добавить строку с данными.
     * @param row новая строка, содержащая ячейки для каждого столбца.
     */
    fun addRowData(row: RowCell) {
        inflater?.let {
            presenter.addRowData(it, tableFix, tableData, row)
        }
    }

    /** Построить таблицу с данными. */
    fun showTable() {
        //закрепленная строка заголовка
        trHeaderFix.removeAllViews()

        //строка заголовка итогов
        trHeader.removeAllViews()

        //закрепленые столбцы
        tableFix.removeAllViews()

        //основная таблица
        tableData.removeAllViews()

        inflater?.let { presenter.showTable(it, trHeaderFix, trHeader, tableFix, tableData) }
    }

    /** Получить заголовок таблицы.
     * @return строка, содержащая значения по каждому заголовку таблицы.
     */
    fun getHeader() = presenter.getHeader()

    /** Получить все данные.
     * @return список строк, содержащих значения по каждому столбцу.
     */
    fun getData() = presenter.getData()

    /** Инициализация переменных. */
    private fun init() {
        trHeaderFix = this.findViewById(R.id.table_data_tr_fix_header)
        trHeader = this.findViewById(R.id.table_data_tr_header)
        tableFix = this.findViewById(R.id.table_data_table_fix)
        tableData = this.findViewById(R.id.table_data_table_value)

        presenter = ScrollTableViewPresenter(params, this)
    }

    /** Подключить слушатели изменения скролла. */
    private fun setListener() {
        val scrollHeader: HorizontalScrollTableView = this.findViewById(R.id.table_data_scroll_header)
        val scrollColumnData: HorizontalScrollTableView = this.findViewById(R.id.table_data_scroll_column_data)
        val scrollFix: VerticalScrollTableView = this.findViewById(R.id.table_data_scroll_fix)
        val scrollRowData: VerticalScrollTableView = this.findViewById(R.id.table_data_scroll_row_data)

        scrollHeader.listener = object: OnScrollListener {
            override fun onScrollChanged(view: View, x: Int, y: Int, oldX: Int, oldY: Int) {
                scrollColumnData.scrollTo(x, y)
            }
        }
        scrollColumnData.listener = object: OnScrollListener {
            override fun onScrollChanged(view: View, x: Int, y: Int, oldX: Int, oldY: Int) {
                scrollHeader.scrollTo(x, y)
            }
        }

        scrollFix.listener = object: OnScrollListener {
            override fun onScrollChanged(view: View, x: Int, y: Int, oldX: Int, oldY: Int) {
                scrollRowData.scrollTo(x, y)
            }
        }
        scrollRowData.listener = object: OnScrollListener {
            override fun onScrollChanged(view: View, x: Int, y: Int, oldX: Int, oldY: Int) {
                scrollFix.scrollTo(x, y)
            }
        }
    }

    /** Сохранение значения. */
    private fun applyDialog(view: LinearLayout) {
        //получаем координаты строки в таблице
        val rowId: Int? = view.getTag(R.id.stv_tag_row_id)?.toString()?.toInt()

        //обходим все поля ввода
        rowId?.let { row ->
            for (ind in 0 until view.childCount) {
                val item = view.getChildAt(ind)
                val editText = item.findViewById<EditText>(R.id.edit_value)
                val columnId: Int? = editText.getTag(R.id.stv_tag_column_id)?.toString()?.toInt()
                val value = editText.text.toString()
                columnId?.let { column ->
                    //обновляем значение в нужной ячейке
                    val columnView = getCellView(row, column)
                    columnView?.findViewById<TextView>(R.id.table_data_item)?.text = value

                    presenter.updateValue(row, column, value)
                }
            }
        }
    }

    /** Отмена значений. */
    private fun cancelDialog(dialog: DialogInterface, view: LinearLayout) {
        //получаем координаты строки в таблице
        val rowId: Int? = view.getTag(R.id.stv_tag_row_id)?.toString()?.toInt()
        rowId?.let { presenter.canceledChangeValues(it) }
        dialog.cancel()
    }

    /** Найти нужную ячейку. */
    private fun getCellView(rowId: Int, columnId: Int): View? {
        var id = columnId
        var table: TableLayout = tableFix
        if (id >= params.countFixColumn) {
            table = tableData
            id = columnId - params.countFixColumn
        }
        else if (params.enabledIconDelete) {
            id += 1 //не учитываем столбец с иконками удаления, изменения
        }

        //находим нужную ячейку
        if (rowId < table.childCount) {
            val rowView = table.getChildAt(rowId) as? TableRow
            rowView?.let {
                if (id < it.childCount) {
                    return it.getChildAt(id)
                }
            }
        }

        return  null
    }

    /** Синхронизировать размеры столбцов заголовка cо столбцами таблицы. */
    private fun updateColumnWidth(trHeader: TableRow, trFirst: TableRow?) {
        trFirst?.let { first ->
            for (columnId in 0 until trHeader.childCount) {
                if (first.getChildAt(columnId) == null || trHeader.getChildAt(columnId) == null) {
                    break
                }

                //вычисляем ширину и задаем ее на заголовок и всю таблицу с данными
                val width = Math.max(first.getChildAt(columnId).width, trHeader.getChildAt(columnId).width)
                first.getChildAt(columnId).minimumWidth = width
                trHeader.getChildAt(columnId).minimumWidth = width
            }
        }
    }

    /** Пометить строку удаленной или восстановленной. */
    private fun setRowEnabled(tableRow: TableRow, isDeleted: Boolean) {
        if (isDeleted) {
            tableRow.setBackgroundResource(R.color.stv_row_delete_color)
        }
        else {
            tableRow.background = null
        }

        //делаем активными или не активными все колонки
        for (columnId in 0 until tableRow.childCount) {
            val view = tableRow.getChildAt(columnId)
            view.findViewById<TextView>(R.id.table_data_item)?.isEnabled = !isDeleted
            view.isClickable = !isDeleted
        }
    }

    /** Обработка клика по значению строки. */
    private fun cellClickListener(rowId: Int) =
        OnClickListener { _ ->
            inflater?.let {
                presenter.builderEditDialog(context, it, rowId)
            }
    }
}