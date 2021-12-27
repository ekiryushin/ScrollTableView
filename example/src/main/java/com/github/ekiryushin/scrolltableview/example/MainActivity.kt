package com.github.ekiryushin.scrolltableview.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.ekiryushin.scrolltableview.cell.Cell
import com.github.ekiryushin.scrolltableview.cell.CellView
import com.github.ekiryushin.scrolltableview.cell.RowCell
import com.github.ekiryushin.scrolltableview.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdd.setOnClickListener {
            //сформируем пустую строку
            val columns: MutableList<Cell> = mutableListOf()
            columns.add(Cell(viewed = CellView.EDIT_STRING))
            for (ind in 1..10) {
                columns.add(Cell(viewed = CellView.EDIT_NUMBER))
            }
            binding.tableDataBlock.addRowData(RowCell(columns))
        }

        setTableData()
    }

    private fun setTableData() {
        //сформируем шапку таблицы
        val columns: MutableList<Cell> = mutableListOf()
        var id: Long = 1
        columns.add(Cell(id = 0, value = "Дата"))
        for (ind in 1..10) {
            columns.add(Cell(id = id++, value = "Значение $ind"))
        }
        val header = RowCell(columns)
        binding.tableDataBlock.setHeaders(header)

        //сформируем основные данные для отображения
        val data: MutableList<RowCell> = mutableListOf()
        for (ind in 10..31) {
            val columnsData: MutableList<Cell> = mutableListOf()
            //добавляем колонку со значением для "Дата"
            columnsData.add(Cell(id = id++, value = "$ind.10.2021", viewed = CellView.EDIT_STRING))
            //добавляем колонки с остальными значениями
            for (indV in 1..10) {
                columnsData.add(Cell(id = id++, value = (10..25).random().toString(), viewed = CellView.EDIT_NUMBER))
            }
            data.add(RowCell(columnsData))
        }
        binding.tableDataBlock.setData(data)

        //показываем таблицу
        binding.tableDataBlock.setEnabledIconDelete(true)
        binding.tableDataBlock.setCountFixColumn(1)
        binding.tableDataBlock.setEditedCellColorResourceId(R.color.edited)
        binding.tableDataBlock.showTable()
    }
}