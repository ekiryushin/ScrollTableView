package io.github.ekiryushin.scrolltableview.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.github.ekiryushin.scrolltableview.cell.Cell
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.DataStatus
import io.github.ekiryushin.scrolltableview.cell.RowCell
import io.github.ekiryushin.scrolltableview.example.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val LOG_TAG = "scrolltableview"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListener()
        setTableData()
    }

    /** Навешать обработчики на кнопки. */
    private fun setClickListener() {
        binding.buttonAdd.setOnClickListener {
            //сформируем пустую строку
            val columns: MutableList<Cell> = mutableListOf()
            columns.add(Cell())
            columns.add(Cell(viewed = CellView.EDIT_STRING))
            columns.add(Cell(viewed = CellView.EDIT_DD_MM_YYYY))
            for (ind in 1..10) {
                columns.add(Cell(viewed = CellView.EDIT_NUMBER))
            }
            binding.tableDataBlock.addRowData(RowCell(columns))
        }

        //вывод в лог данных по таблице
        binding.buttonLog.setOnClickListener {
            val data = binding.tableDataBlock.getData()
            //выведем новые, удаленные строки или строки, где поменялись значения
            val editedDate = data?.filter { row ->
                row.status == DataStatus.ADD || row.status == DataStatus.DELETE
                        || row.columns.any { column -> column.status == DataStatus.EDIT }
            }
            //в строках, где поменялись значения, оставим только измененные значения
            editedDate?.filter { row -> row.status == DataStatus.NORMAL }
                ?.forEach { row ->
                    row.columns = row.columns.filter { column -> column.status == DataStatus.EDIT }
                }
            Log.d(LOG_TAG, "Шапка: ${binding.tableDataBlock.getHeader().toString()}")
            Log.d(LOG_TAG, "Данные: ${editedDate.toString()}")
        }
    }

    /** Сформировать данные для отображения. */
    private fun setTableData() {
        //сформируем шапку таблицы
        val header = generateHeader()

        //сформируем основные данные для отображения
        val data = generateData()

        //передаем сформированные данные
        with(binding.tableDataBlock) {
            setHeader(header)
            setData(data)
        }

        //настраиваем внешний вид и отображаем таблицу
        setStyleTable()
    }

    /** Сформировать заголовок таблицы. */
    private fun generateHeader(): RowCell {
        val columns: MutableList<Cell> = mutableListOf()
        var id: Long = 1
        columns.add(Cell(id = id++, value = "Чтение"))
        columns.add(Cell(id = id++, value = "Строковое"))
        columns.add(Cell(id = id++, value = "Дата"))
        for (ind in 1..10) {
            columns.add(Cell(id = id++, value = "Числовое $ind"))
        }
        return RowCell(columns)
    }

    /** Сформировать данные таблицы. */
    private fun generateData(): MutableList<RowCell> {
        val data: MutableList<RowCell> = mutableListOf()
        var id: Long = 1
        for (ind in 1..30) {
            val columnsData: MutableList<Cell> = mutableListOf()
            //добавляем колонку с данными только для чтения
            columnsData.add(Cell(id = id++, value = "$ind"))
            //добавляем колонку со строковым значением
            columnsData.add(Cell(id = id++, value = "Строка #$ind", viewed = CellView.EDIT_STRING))
            //добавляем колонку с датой
            val dateValue = String.format("%02d.01.2022", ind)
            columnsData.add(Cell(id = id++, value = dateValue, viewed = CellView.EDIT_DD_MM_YYYY))
            //добавляем колонки с остальными значениями
            for (indV in 1..10) {
                val value = (10..200).random().toFloat().toString()
                columnsData.add(Cell(id = id++, value = value, viewed = CellView.EDIT_NUMBER))
            }
            data.add(RowCell(columnsData))
        }
        return data
    }

    /** Настроить внешний вид и отобразить таблицу. */
    private fun setStyleTable() {
        with(binding.tableDataBlock) {
            //отображать или нет столбец с иконками удаления/восстановления строки
            setEnabledIconDelete(true)
            //количество закрепленных столбцов слева (не будут прокручиваться по горизонтале)
            setCountFixColumn(2)
            //цвет выделения ячеек, в которых есть изменения
            setEditedCellColorResourceId(R.color.edited)
            //стиль офрмления диалогового окна выбора даты
            setDialogSelectDateStyleId(R.style.styleDialogSelectDate)
            showTable()
        }
    }
}