[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.ekiryushin/scrolltableview/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.ekiryushin/scrolltableview)

# ScrollTableView
Библиотека не очень удачная и больше не поддерживается, т.к. плохо работает с большим объемом данных. 

Отображение данных в виде прокручиваемой таблицы. С возможностью закрепления шапки таблицы
и нескольких колонок слева. Измененные значения, добавленные или удаленные строки помечаются
специальным статусом. Благодаря ему можно без труда обработать только те данные, что добавил,
изменил или удалил пользователь.<br>
+ [Документация](docs/documentation.md)<br>
+ [История изменений](docs/history.md)<br>
<img src="https://github.com/ekiryushin/ScrollTableView/blob/master/docs/preview.gif" style="width: 30%" />

# Подключение
В `build.gradle` приложения подключаем центральный репозиторий
```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```
В зависимостях `build.gradle` нужного модуля подключаем данную библиотеку
```groovy
dependencies {
    implementation 'io.github.ekiryushin:scrolltableview:1.0.4'
}
```

# Пример использования
В разметке добавляем view таблицы для отображения
```xml
<io.github.ekiryushin.scrolltableview.ScrollTableView
    android:id="@+id/table_data_block"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
В коде формируем шапку таблицы, если нужно
```kotlin
val columns: MutableList<Cell> = mutableListOf()
var id: Long = 1
columns.add(Cell(id = id++, value = "Чтение"))
columns.add(Cell(id = id++, value = "Строковое"))
columns.add(Cell(id = id++, value = "Дата"))
for (ind in 1..10) {
    columns.add(Cell(id = id++, value = "Числовое $ind"))
}
val header = RowCell(columns)
```
Далее формируем основные данные
```kotlin
val data: MutableList<RowCell> = mutableListOf()
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
```
Передаем сформированные данные
```kotlin
with(binding.tableDataBlock) {
    setHeader(header)
    setData(data)
}
```
Настраиваем внешний вид и отображаем таблицу
```kotlin
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
```
При необходимости можно добавить новую строку с данными
```kotlin
//сформируем пустую строку
val columns: MutableList<Cell> = mutableListOf()
columns.add(Cell())
columns.add(Cell(viewed = CellView.EDIT_STRING))
columns.add(Cell(viewed = CellView.EDIT_DD_MM_YYYY))
for (ind in 1..10) {
    columns.add(Cell(viewed = CellView.EDIT_NUMBER))
}
binding.tableDataBlock.addRowData(RowCell(columns))
```
Получить заголовок таблицы
```kotlin
binding.tableDataBlock.getHeader()
```
Получить все добавленные, удаленные строки или строки, в которых менялись значения
```kotlin
val editedData = binding.tableDataBlock.getData()?.filter { row ->
    row.status == DataStatus.ADD || row.status == DataStatus.DELETE
        || row.columns.any { column -> column.status == DataStatus.EDIT }
}
//в строках, где поменялись значения, оставим только измененные значения
editedDate?.filter { row -> row.status == DataStatus.NORMAL }
    ?.forEach { row ->
        row.columns = row.columns.filter { column -> column.status == DataStatus.EDIT }
}
```

# License

   Copyright 2021 Eugene Kiryushin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
