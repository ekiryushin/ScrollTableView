[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.ekiryushin/scrolltableview/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.ekiryushin/scrolltableview)

# ScrollTableView
Отображение данных в виде прокручиваемой таблицы. С возможностью закрепления шапки таблицы и нескольких колонок слева. Измененные значения, добавленные или удаленные строки помечаются специальным статусом. Благодаря ему можно без труда обработать только те данные, что добавил, изменил или удалил пользователь.
<img src="https://github.com/ekiryushin/ScrollTableView/blob/master/example/preview.gif" style="width: 50%" />

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
    implementation 'io.github.ekiryushin:scrolltableview:1.0.2'
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
В коде формируем структуру таблицы
```kotlin
//сформируем шапку таблицы, если нужно
val columns: MutableList<Cell> = mutableListOf()
var id: Long = 1
columns.add(Cell(id = 0, value = "Дата"))
for (ind in 1..10) {
    columns.add(Cell(id = id++, value = "Значение $ind"))
}
val header = RowCell(columns)

//сформируем основные данные для отображения
val data: MutableList<RowCell> = mutableListOf()
for (ind in 10..31) {
    val columnsData: MutableList<Cell> = mutableListOf()
    //добавляем колонку со значением для "Дата"
    columnsData.add(Cell(id = id++, value = "$ind.10.2021", viewed = CellView.EDIT_STRING))
    //добавляем колонки с остальными значениями
    for (indV in 1..10) {
        columnsData.add(Cell(id = id++, value = (10..25).random().toFloat().toString(), viewed = CellView.EDIT_NUMBER))
    }
    data.add(RowCell(columnsData))
}

//передаем сформированные данные
with(binding.tableDataBlock) {
    setHeaders(header)
    setData(data)
}

//настраиваем внешний вид и отображаем таблицу
with(binding.tableDataBlock) {
    //отображать или нет столбец с иконками удаления/восстановления строки
    setEnabledIconDelete(true)
    //количество закрепленных столбцов слева (не будут прокручиваться по горизонтале)
    setCountFixColumn(1)
    //цвет выделения ячеек, в которых есть изменения
    setEditedCellColorResourceId(R.color.edited)
    showTable()
}
```
При необходимости можно добавить новую строку с данными
```kotlin
//сформируем пустую строку
val columns: MutableList<Cell> = mutableListOf()
columns.add(Cell(viewed = CellView.EDIT_STRING))
for (ind in 1..10) {
    columns.add(Cell(viewed = CellView.EDIT_NUMBER))
}
binding.tableDataBlock.addRowData(RowCell(columns))
```
Получить все добавленные, удаленные строки или строки, в которых менялись значения
```kotlin
val editedData = binding.tableDataBlock.getData()?.filter { row ->
    row.status == DataStatus.ADD || row.status == DataStatus.DELETE
        || row.columns.any { column -> column.status == DataStatus.EDIT }
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
