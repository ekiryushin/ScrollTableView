package io.github.ekiryushin.scrolltableview.scrolled

import android.view.View

interface OnScrollListener {
    fun onScrollChanged(view: View, x: Int, y: Int, oldX: Int, oldY: Int)
}